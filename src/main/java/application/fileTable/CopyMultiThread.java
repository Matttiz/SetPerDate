package application.fileTable;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CopyMultiThread implements Runnable{

    private static int number = 0 ;
    private File destination;
    private FileRow fileRow;
    public CopyMultiThread(File destination, FileRow fileRow) {
        this.destination = destination;
        this.fileRow = fileRow;
        number += 1;
        System.out.println(number);
    }



    @SneakyThrows
    private void copyOneFile(File destination,FileRow fileRow) {
        if (!fileRow.isCopied()) {
            String destinationFile = destination.getAbsolutePath()
                    + File.separatorChar
                    + fileRow.getCreationDateAsPrettyString()
                    + File.separatorChar
                    + fileRow.getThisDayPhotoCount()
                    + fileRow.getExtension();

            FileUtils.copyFile(new File(fileRow.getAbsolutPathToFile()), new File(destinationFile));
            fileRow.setCopied(true);
        }
    }

    public void run() {
        System.out.println("Wątek numer " + this.number + " rozpoczął działanie");
        copyOneFile(destination, fileRow);
        System.out.println("Wątek numer " + this.number + " zakończył działanie");
    }
}
