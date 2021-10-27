package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DestinationStructure {
    List<File> destinationSubDirectory = new ArrayList<>();
    private CatalogContent catalogContent;

    @SneakyThrows
    public DestinationStructure(File destination, CatalogContent catalogContent){
        this.catalogContent = catalogContent;
        List<String> list = this.catalogContent.getUniqueDatesList();
        for (String catalog: list) {
            File directory = new File(destination.getAbsolutePath() + File.separatorChar + catalog);
            System.out.println(directory.getName());
            if (!(directory.exists() && directory.isDirectory())){
                directory.mkdir();
                destinationSubDirectory.add(directory);
            }
            if (directory.exists()){
                catalogContent.addFilesFromDestinationToSource(directory);
            }
        }
    }

    public void removeDuplicates(){
        catalogContent.removeDuplicate();
    }

    @SneakyThrows
    public void copyFile(File destination, int numberThreads){
        ExecutorService es = Executors.newFixedThreadPool(numberThreads);

        for(FileRow fileRow : catalogContent.getList()){
            es.execute(new CopyMultiThread(destination,fileRow));
            fileRow.copyFileAndSetCopied(destination);
        }
        es.shutdown();
        try {
            es.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setDestinationFileName(){
        catalogContent.setDestinationFileName();
    }

    public void print(){
        catalogContent.print();
    }
}
