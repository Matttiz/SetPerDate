package application;

import application.fileTable.CatalogContent;
import application.fileTable.DestinationStructure;

import java.io.File;

public class App {
    public static void main(String[] args) {
        int threadNumber = 4;
        String path =  System.getProperty("user.dir");
        File source = new File(path + "/src/test/resources/source2");
        File destination = new File(path + "/src/test/resources/destination2");

        CatalogContent catalogContent = new CatalogContent(source);
        catalogContent.setDestinationFileName();
//        catalogContent.print();

        DestinationStructure destinationStructure = new DestinationStructure(
                destination, catalogContent);
        destinationStructure.removeDuplicates();
        destinationStructure.setDestinationFileName();
//        destinationStructure.print();


        destinationStructure.copyFile(destination, threadNumber);
//        catalogContent.print();

    }
}
