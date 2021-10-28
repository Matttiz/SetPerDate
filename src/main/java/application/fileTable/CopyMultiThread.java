package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;

public class CopyMultiThread implements Runnable{

    private static int number = 0;
    private int actualNumber;
    private File destination;
    private FileRow fileRow;
    public CopyMultiThread(File destination, FileRow fileRow) {
        this.destination = destination;
        this.fileRow = fileRow;
        number += 1;
        actualNumber= number;
    }

    @SneakyThrows
    private void copyOneFile() {
        fileRow.copyFileAndSetCopied(destination);
    }

    public void run() {
        System.out.println("Wątek numer " + this.actualNumber + " rozpoczął działanie \n"
                + fileRow.getAbsolutPathToFile());
        copyOneFile();
        System.out.println("Wątek numer " + this.actualNumber + " zakończył działanie");
    }
}
