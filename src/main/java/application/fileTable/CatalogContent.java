package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogContent {
    private File source;
    private List<FileRow> list = new ArrayList<>();
    private static final String[] extensions = {"png","jpg"};


    @SneakyThrows
    public CatalogContent(File source) {
        this.source = source;
        list.addAll(findFiles(source));
        sort();
    }

    public File getSource() {
        return source;
    }

    public List<FileRow> getList() {
        return list;
    }

    public void sort(){
        Collections.sort(list, new FileRow.sortItems());
    }

    public void print(){
        System.out.println();
        for(FileRow file : list){
            System.out.println(
                    file.getCreationDateWithHoursAndMinutesAsPrettyString() + " "
                            + file.getThisDayPhotoCountFormatted() + " "
                            + file.isCopied() + " "
                            + file.getAbsolutPathToFile());
        }
    }

    public void setDestinationFileName(){
        FileRow fileToCompare = list.get(0);
        Calendar dateToCompare = Calendar.getInstance();
        Calendar dateFromFile = Calendar.getInstance();
        dateToCompare.setTime(fileToCompare.getCreation());

        int index = 1;
        for(FileRow file : list){
            dateToCompare.setTime(fileToCompare.getCreation());
            dateFromFile.setTime(file.getCreation());
          if (isSameDateTime(dateToCompare,dateFromFile)){
               file.setThisDayPhotoCount(index++);
           }else{
               index = 1;
               file.setThisDayPhotoCount(index++);
               fileToCompare = file;
           }
        }
    }


    public boolean isSameDateTime(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE));
    }

    public List<String> getUniqueDatesList(){
        List<String> dates = new ArrayList<>();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        for(FileRow file : list) {
            dates.add(simpleDateFormat.format(file.getCreation()));
        }
        return dates.stream().distinct().collect(Collectors.toList());
    }

    @SneakyThrows
    public List<FileRow> findFiles(File fileToCheck){
        File[] listOfFiles = fileToCheck.listFiles();
        List<FileRow> list = new ArrayList<>();
        FileRow fileRow;
        for (File file : listOfFiles) {
            if(file.isDirectory()){
                list.addAll(findFiles(file));
            }else {
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                if(containsExtension(extension)) {
                    BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    FileTime time = attributes.creationTime();
                    fileRow = new FileRow(file, time);
                    list.add(fileRow);
                }
            }
        }
        return list;
    }

    public boolean containsExtension(String extension){
        for(String array:extensions){
            if(extension.equals(array)){
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    public void addFilesFromDestinationToSource(File fileToCheck){
        File[] listOfFiles = fileToCheck.listFiles();
        List<FileRow> list = new ArrayList<>();
        FileRow fileRow;
        for (File file : listOfFiles) {
            if(file.isDirectory()){
                list.addAll(findFiles(file));
            }else {
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                if(containsExtension(extension)) {
                    BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    FileTime time = attributes.creationTime();
                    File destination = new File(
                            file.getAbsoluteFile().getAbsolutePath().substring(0,
                                    file.getAbsoluteFile().getAbsolutePath().lastIndexOf(".")
                            ) + "a." + extension
                    );
                    file.renameTo(destination);
                    fileRow = new FileRow(destination, time);
                    list.add(fileRow);
                }
            }
        }
        list.addAll(list);
    }
}
