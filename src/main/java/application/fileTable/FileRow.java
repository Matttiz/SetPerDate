package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class FileRow {

    private String absolutPathToFile;
    private long size;
    private Date creation;
    private int thisDayPhotoCount;
    private boolean copied;
    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    private static String patternDate = "yyyy-MM-dd";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private static SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);


    public FileRow(){
    }


    @SneakyThrows
    public FileRow(File file, FileTime time){
        absolutPathToFile  = file.getAbsoluteFile().getAbsolutePath();
        size = file.getTotalSpace();
        creation = new Date(time.toMillis());
    }


    public String getAbsolutPathToFile() {
        return absolutPathToFile;
    }

    public Date getCreation() {
        return creation;
    }

    public String getCreationDateWithHoursAndMinutesAsPrettyString() {
        return simpleDateFormat.format(creation);
    }

    public String getCreationDateAsPrettyString() {
        return simpleDateFormatDate.format(creation);
    }

    public int getThisDayPhotoCount() {
        return thisDayPhotoCount;
    }

    public String getThisDayPhotoCountFormatted() {
        return String.format("%3d", thisDayPhotoCount);
    }

    public void setThisDayPhotoCount(int thisDayPhotoCount) {
        this.thisDayPhotoCount = thisDayPhotoCount;
    }

    public boolean isCopied() {
        return copied;
    }

    public void setCopied(boolean copied) {
        this.copied = copied;
    }

    public static class sortItems implements Comparator<FileRow> {
        @Override
        public int compare(FileRow a, FileRow b) {
            return a.getCreation().compareTo(b.getCreation());
        }
    }

    public long getSize() {
        return size;
    }

    public String getExtension(){
        return getAbsolutPathToFile().substring(getAbsolutPathToFile().lastIndexOf("."));
    }
}
