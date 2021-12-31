package application;

import application.fileTable.CatalogContent;

import java.io.File;

public class App {
    public static void main(String[] args) {
        int threadNumber = 6;
        String path = System.getProperty("user.dir");
        File source = new File( "/media/matti/Wazne/Kopie/");
        File destination = new File("/media/matti/Wazne/Zdjęcia_segregacja");

        CatalogContent catalogContent = new CatalogContent(source, destination);
//        catalogContent.print();
        catalogContent.createDirectoryTree();
        catalogContent.copyFile(threadNumber);
        catalogContent.deletedDirectory();
    }
}
