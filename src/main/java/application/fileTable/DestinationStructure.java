package application.fileTable;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

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
            File directory = new File(destination.getAbsolutePath() + File.separatorChar + catalog);
            if(!(directory.exists() && directory.isDirectory())){
                directory.mkdir();
                destinationSubDirectory.add(directory);
            }
            if(directory.exists()){
                catalogContent.addFilesFromDestinationToSource(directory);
            }
        }
    }

    @SneakyThrows
    public void copyFile(File destination){
        String destinationFile;
        for(FileRow fileRow : catalogContent.getList()){
            if(!fileRow.isCopied()) {
                destinationFile = destination.getAbsolutePath()
                        + File.separatorChar
                        + fileRow.getCreationDateAsPrettyString()
                        + File.separatorChar
                        + fileRow.getThisDayPhotoCount()
                        + fileRow.getExtension();

                FileUtils.copyFile(new File(fileRow.getAbsolutPathToFile()), new File(destinationFile));
                fileRow.setCopied(true);
            }
        }
    }
}
