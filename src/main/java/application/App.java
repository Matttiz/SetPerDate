package application;

import application.fileTable.CatalogContent;
import application.fileTable.DestinationStructure;

import java.io.File;

public class App {
    public static void main(String[] args) {
        String path =  System.getProperty("user.dir");
        File source = new File(path + "/src/test/resources/source");
        File destination = new File(path + "/src/test/resources/destination");
        CatalogContent catalogContent = new CatalogContent(source);
        catalogContent.setDestinationFileName();
        catalogContent.print();

        DestinationStructure destinationStructure = new DestinationStructure(
                destination, catalogContent);
        destinationStructure.removeDuplicates();


        destinationStructure.copyFile(destination);
        catalogContent.print();

    }
}
