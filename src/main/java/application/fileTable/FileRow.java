package application.fileTable;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class FileRow {

    private String absolutPathToFile;
    private long size;
    private Date creation;
    private int thisDayPhotoCount;
    private File file;
    private boolean copied;
    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    private static String patternDate = "yyyy-MM-dd";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private static SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);


    public FileRow() {
    }

    @SneakyThrows
    public FileRow(File file, FileTime time) {
        this.absolutPathToFile = file.getAbsoluteFile().getAbsolutePath();
        this.size = file.getTotalSpace();
        this.creation = new Date(time.toMillis());
        this.file = file;
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

    public File getFile() {
        return file;
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

    public String getExtension() {
        return getAbsolutPathToFile().substring(getAbsolutPathToFile().lastIndexOf("."));
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final FileRow fileRow = (FileRow) obj;
        if (this == fileRow) {
            return true;
        } else {
            return (this.getCreation().equals(fileRow.getCreation()) && this.getSize() == fileRow.getSize());
        }
    }

    public void copyFileAndSetCopied(File destination) throws IOException {
        if (!this.isCopied()) {
            this.setCopied(true);
            String destinationFile = destination.getAbsolutePath()
                    + File.separatorChar
                    + this.getCreationDateAsPrettyString()
                    + File.separatorChar
                    + this.getThisDayPhotoCount()
                    + this.getExtension();

            FileUtils.copyFile(new File(this.getAbsolutPathToFile()), new File(destinationFile));
        }
    }
}
