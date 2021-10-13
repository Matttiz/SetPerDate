package application.fileTable;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        String destinationPath;



//        ExecutorService es = Executors.newFixedThreadPool(numberThreads);

        for(FileRow fileRow : catalogContent.getList()){
                if(!fileRow.isCopied()) {
                    destinationPath = destination.getAbsolutePath()
                            + File.separatorChar
                            + fileRow.getCreationDateAsPrettyString()
                            + File.separatorChar
                            + fileRow.getThisDayPhotoCount()
                            + fileRow.getExtension();

                    FileUtils.copyFile(new File(fileRow.getAbsolutPathToFile()), new File(destinationPath));
                    fileRow.setCopied(true);
                }
        }

//        try {
//            es.awaitTermination(20, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }

    public void setDestinationFileName(){
        catalogContent.setDestinationFileName();
    }

    public void print(){
        catalogContent.print();
    }
}
