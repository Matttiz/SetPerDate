package application.fileTable;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class FileExtend {
    private String path;
    private File file;

    public FileExtend(String path){
        this.path = path;
        this.file = new File(path);
    }


    @SneakyThrows
    public List<FileRow> findFiles(File fileToCheck){
        File[] listOfFiles = fileToCheck.listFiles();
        List<FileRow> list = new ArrayList<>();
        FileRow fileRow;
        for (File file : listOfFiles) {
            if(file.isDirectory()){
                list.addAll(findFiles(file));
            }else {
                if(file.getName().substring(file.getName().lastIndexOf(".")).equals("jpg")) {
                    BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    FileTime time = attributes.creationTime();
                    fileRow = new FileRow(file, time);
                    list.add(fileRow);
                }
            }
        }
        return list;
    }
}
