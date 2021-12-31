package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CatalogContent {
    private File source;
    private File destination;
    private List<FileRow> sourceList = new ArrayList<>();
    private List<FileRow> destinationList = new ArrayList<>();
    private List<FileRow> toCopyList = new ArrayList<>();
    private static final List<String> extensions = Arrays.asList("png", "jpg");

    public CatalogContent() {
    }

    @SneakyThrows
    public CatalogContent(File sourceCatalog, File destinationCatalog) {
        this.source = sourceCatalog;
        this.destination = destinationCatalog;
        sourceList.addAll(findFiles(source));
        addFilesFromDestination(destinationCatalog);
        integrationList();
        sort();
        setDestinationFileName();
    }

    public List<FileRow> getToCopyList() {
        return toCopyList;
    }

    public void sort() {
        Collections.sort(toCopyList, new FileRow.sortItems());
    }

    public void integrationList() {
        toCopyList.addAll(destinationList);
        toCopyList.addAll(sourceList);
        toCopyList = toCopyList.stream()
                .filter(distinctByKey(p -> p.getLastModificationDateWithHoursAndMinutesAsPrettyString() + " " + p.getExtension() + " " + p.getSize())).collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void print() {
        System.out.println();
        int i = 1;
        String integer = "";
        for (FileRow file : toCopyList) {
            integer = String.format("%3d", i);
            System.out.println(
                    integer + " " +
                            file.getLastModificationDateWithHoursAndMinutesAsPrettyString() + " "
                            + file.getThisDayPhotoCountFormatted() + " "
                            + file.isCopied() + " "
                            + file.getAbsolutPathToFile()
            );
            i++;
        }
    }

    public boolean isSameDateTime(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE));
    }

    public List<String> getUniqueDatesList() {
        List<String> dates = new ArrayList<>();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        for (FileRow file : toCopyList) {
            dates.add(simpleDateFormat.format(file.getLastModificationDate()));
        }
        return dates.stream().distinct().collect(Collectors.toList());
    }


    @SneakyThrows
    private List<FileRow> findFiles(File fileToCheck) {
        File[] listOfFiles = fileToCheck.listFiles();
        List<FileRow> list = new ArrayList<>();
        FileRow fileRow;
        if (fileToCheck.exists()) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    list.addAll(findFiles(file));
                } else {
                    String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                    if (containsExtension(extension)) {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        FileTime time = attributes.lastModifiedTime();
                        fileRow = new FileRow(file, time);
                        list.add(fileRow);
                    }
                }
            }
        }
        return list;
    }

    private boolean containsExtension(String extension) {
        return extensions.contains(extension);
    }

    @SneakyThrows
    public void addFilesFromDestination(File fileToCheck) {
        List<FileRow> listToAdd = new ArrayList<>();
        File[] listOfFiles = fileToCheck.listFiles();
        FileRow fileRow;
        if (fileToCheck.exists()) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    listToAdd.addAll(findFiles(file));
                } else {
                    String fileAbsolutPath = file.getAbsolutePath();
                    String extension = fileAbsolutPath.substring(fileAbsolutPath.lastIndexOf(".") + 1);
                    if (containsExtension(extension)) {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        FileTime time = attributes.lastModifiedTime();
                        File destination = new File(
                                fileAbsolutPath.substring(0,
                                        file.getAbsoluteFile().getAbsolutePath().lastIndexOf(".")
                                ) + "a." + extension
                        );
                        file.renameTo(destination);
                        fileRow = new FileRow(destination, time);
                        listToAdd.add(fileRow);
                    }
                }
            }
        }
        this.destinationList.addAll(listToAdd);
    }

    public void setDestinationFileName() {
        if (toCopyList.size() > 0) {
            FileRow fileToCompare = toCopyList.get(0);
            Calendar dateToCompare = Calendar.getInstance();
            Calendar dateFromFile = Calendar.getInstance();
            dateToCompare.setTime(fileToCompare.getLastModificationDate());
            int index = 1;
            for (FileRow file : toCopyList) {
                dateToCompare.setTime(fileToCompare.getLastModificationDate());
                dateFromFile.setTime(file.getLastModificationDate());
                if (isSameDateTime(dateToCompare, dateFromFile)) {
                    file.setThisDayPhotoCount(index++);
                } else {
                    index = 1;
                    file.setThisDayPhotoCount(index++);
                    fileToCompare = file;
                }
            }
        }
    }

    @SneakyThrows
    public void copyFile(int numberThreads) {
        ExecutorService es = Executors.newFixedThreadPool(numberThreads);
        for (FileRow fileRow : getToCopyList()) {
            es.execute(new CopyMultiThread(this.destination, fileRow));
        }
        es.shutdown();
        try {
            es.awaitTermination(25, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createDirectoryTree() {
        List<String> list = getUniqueDatesList();
        for (String catalog : list) {
            File directory = new File(destination.getAbsolutePath() + File.separatorChar + catalog);
            if (!(directory.exists() && directory.isDirectory())) {
                directory.mkdir();
            }
        }
    }

    public void deletedDirectory() {
        System.out.println();
        for( FileRow fileRow: toCopyList){
            System.out.println(fileRow.getFile().getAbsolutePath());
        }

        System.out.println();


        for (FileRow fileRow : destinationList) {
            System.out.println(fileRow.getFile().getAbsolutePath());
            if (!toCopyList.contains(fileRow)) {
                System.out.println(fileRow.getFile().getAbsolutePath());
                fileRow.getFile().delete();
                goToParentDirectory(fileRow.getFile());
            }
        }
    }

    public void goToParentDirectory(File file) {
        File directory = new File(file.getParent());
        if (directory.listFiles().length == 0) {
            directory.delete();
            goToParentDirectory(directory);
        }
    }
}
