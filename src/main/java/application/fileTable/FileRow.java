package application.fileTable;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class FileRow {

    private String absolutPathToFile;
    private long size;
    private Date lastModificationDate;
    private int thisDayPhotoCount;
    private File file;
    private boolean copied;
    final private static String pattern = "yyyy-MM-dd HH:mm:ss";
    final private static String patternDate = "yyyy-MM-dd";
    final private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    final private static SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);


    public FileRow() {
    }

    @SneakyThrows
    public FileRow(File file, FileTime time) {
        this.absolutPathToFile = file.getAbsoluteFile().getAbsolutePath();
        this.size = file.getTotalSpace();
        this.lastModificationDate = new Date(time.toMillis());
        this.file = file;
    }

    public String getAbsolutPathToFile() {
        return absolutPathToFile;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public String getLastModificationDateWithHoursAndMinutesAsPrettyString() {
        return simpleDateFormat.format(lastModificationDate);
    }

    public String getLastModificationDateAsPrettyString() {
        return simpleDateFormatDate.format(lastModificationDate);
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
            return a.getLastModificationDate().compareTo(b.getLastModificationDate());
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
            return (this.getLastModificationDate().equals(fileRow.getLastModificationDate())
                    && this.getSize() == fileRow.getSize()
                    && this.getExtension().equals(fileRow.getExtension()));
        }
    }

    public void copyFileAndSetCopied(File destination) throws IOException {
        if (!this.isCopied()) {
            this.setCopied(true);
            String destinationFile = destination.getAbsolutePath()
                    + File.separatorChar
                    + this.getLastModificationDateAsPrettyString()
                    + File.separatorChar
                    + this.getThisDayPhotoCount()
                    + this.getExtension();

            if (!this.getFile().getAbsolutePath().equals(destinationFile)) {
                System.out.println("Kopiuję " + this.getFile().getAbsolutePath() + " do " + destinationFile);
                FileUtils.copyFile(new File(this.getAbsolutPathToFile()), new File(destinationFile));
            }

            if (!destinationFile.equals(this.getFile().getAbsolutePath())
                    && destinationFile.contains(destination.getAbsolutePath())
                    && !destinationFile.equals(destination.getAbsolutePath())) {
                FileUtils.deleteQuietly(this.getFile());
            }
        }
    }
}
