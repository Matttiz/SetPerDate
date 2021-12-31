package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;

public class CopyMultiThread implements Runnable {

    private static int number = 0;
    private int actualNumber;
    private File destination;
    private FileRow fileRow;

    public CopyMultiThread(File destination, FileRow fileRow) {
        this.destination = destination;
        this.fileRow = fileRow;
        number += 1;
        actualNumber = number;
    }

    @SneakyThrows
    private void copyOneFile() {
        fileRow.copyFileAndSetCopied(destination);
    }

    public void run() {
        String number = String.format("%4d", this.actualNumber);
        System.out.println("Wątek numer " + number + " rozpoczął działanie");
        copyOneFile();
        System.out.println("Wątek numer " + number + " zakończył działanie");
    }
}
