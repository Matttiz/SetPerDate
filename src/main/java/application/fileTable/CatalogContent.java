package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        removeDuplicate();
        setDestinationFileName();
        sort();
    }

    public File getSource() {
        return source;
    }

    public List<FileRow> getSourceList() {
        return sourceList;
    }

    public void sort() {
        Collections.sort(sourceList, new FileRow.sortItems());
    }

    public void removeDuplicate() {
        FileRow fileRow = new FileRow();
        boolean deleted = false;
        if (destination.exists()) {
            for (FileRow destinationFile : destinationList) {
                for (FileRow sourceFile : sourceList) {
                    fileRow = sourceFile;
                    if (destinationFile.getSize() == (sourceFile.getSize())
                            && destinationFile.getCreation() == sourceFile.getCreation()
                            && destinationFile.getExtension() == sourceFile.getExtension()) {
                        sourceList.remove(sourceFile);
                        deleted = true;
                    }
                }
                if (!deleted) {
                    toCopyList.add(fileRow);
                    deleted = false;
                }
            }
        } else {
            toCopyList.addAll(sourceList);
        }
    }

    public void print() {
        System.out.println();
        int i = 1;
        String integer = "";
        for (FileRow file : sourceList) {
            integer = String.format("%3d", i);
            System.out.println(
                    integer + " " +
                            file.getCreationDateWithHoursAndMinutesAsPrettyString() + " "
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
            dates.add(simpleDateFormat.format(file.getCreation()));
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
                        FileTime time = attributes.creationTime();
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
                        FileTime time = attributes.creationTime();
                        File destination = new File(
                                fileAbsolutPath.substring(0,
                                        file.getAbsoluteFile().getAbsolutePath().lastIndexOf(".")
                                ) + "a." + extension
                        );
                        if (isNameEqualDate(fileAbsolutPath)) {
                            file.renameTo(destination);
                            fileRow = new FileRow(destination, time);
                            listToAdd.add(fileRow);
                        }
                    }
                }
            }
        }
        this.destinationList.addAll(listToAdd);
    }

    public void setDestinationFileName() {
        FileRow fileToCompare = toCopyList.get(0);
        Calendar dateToCompare = Calendar.getInstance();
        Calendar dateFromFile = Calendar.getInstance();
        dateToCompare.setTime(fileToCompare.getCreation());
        int index = 1;
        for (FileRow file : toCopyList) {
            dateToCompare.setTime(fileToCompare.getCreation());
            dateFromFile.setTime(file.getCreation());
            if (isSameDateTime(dateToCompare, dateFromFile)) {
                file.setThisDayPhotoCount(index++);
            } else {
                index = 1;
                file.setThisDayPhotoCount(index++);
                fileToCompare = file;
            }
        }
    }

    private boolean isNameEqualDate(String fileAbsolutPath) {
        Pattern pattern = Pattern.compile("^\\d{1,}$");
        String fileName = fileAbsolutPath.substring(fileAbsolutPath.lastIndexOf(File.separator) + 1, fileAbsolutPath.lastIndexOf("."));
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find();
    }

    @SneakyThrows
    public void copyFile(int numberThreads) {
        ExecutorService es = Executors.newFixedThreadPool(numberThreads);
        for (FileRow fileRow : getSourceList()) {
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
        Stream<LocalDate> dates = list.stream()
                .map(name -> LocalDate.parse(name.substring(name.lastIndexOf(File.separator))))
                .sorted(Comparator.naturalOrder());
        for (String catalog : list) {
            File directory = new File(destination.getAbsolutePath() + File.separatorChar + catalog);
            if (!(directory.exists() && directory.isDirectory())) {
                directory.mkdir();
            }
        }
    }
}
