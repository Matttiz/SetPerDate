package application;

import application.fileTable.CatalogContent;

import java.io.File;

public class App {
    public static void main(String[] args) {
        int threadNumber = 1;
        String path =  System.getProperty("user.dir");
//        File source = new File("/home/matti/Obrazy/Zdjęcia");
        File source = new File(path + "/src/test/resources/source2");
        File destination = new File(path + "/src/test/resources/destination3");

        CatalogContent catalogContent = new CatalogContent(source,destination);
        catalogContent.print();
        catalogContent.createDirectoryTree();
        catalogContent.copyFile(threadNumber);

    }
}
