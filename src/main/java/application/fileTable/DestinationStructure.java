package application.fileTable;

import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DestinationStructure {
    List<File> destinationSubDirectory = new ArrayList<>();
    private CatalogContent catalogContent;

    @SneakyThrows
    public DestinationStructure(File destination, CatalogContent catalogContent){
        this.catalogContent = catalogContent;
        List<String> list =this.catalogContent.getUniqueDatesList();
        for (String catalog: list){
            File directory = new File(destination.getAbsolutePath() + File.separatorChar+ catalog);
//            if(directory.exists() && directory.isDirectory()){
//
//            }
            if(!(directory.exists() && directory.isDirectory())){
                directory.mkdir();
                destinationSubDirectory.add(directory);
            }
        }
    }
}
