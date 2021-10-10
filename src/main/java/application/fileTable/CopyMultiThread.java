package application.fileTable;

public class CopyMultiThread implements Runnable{

    public CopyMultiThread(){

    }



    public synchronized String getNewFileName(String directotry, String extension) {
        /**
         * Parsujemy numer folderu ze stringa do int
         */
        int fileNumber = 0;
        /**
         * Sprawdzamy czy już istnieje w w mapFolders wpis z kluczem o numerze danego
         * folderu Jeśli istnieje pobieramy go i zapisujemy w zmiennej fileNumber
         */
        if (CounterMultiThread.mapFolders.get(directotry) != null) {
            fileNumber = CounterMultiThread.mapFolders.get(directotry);
        }
        /**
         * iterujemy zmienną fileNumber i umieszczamy w mapFolders używając
         * odpowiedniego klucza
         */
        fileNumber++;
        CounterMultiThread.mapFolders.put(directotry, fileNumber);
        /**
         * Tworzymy ciąg znaków z nazwą pliku i następnie zwracamy ją
         */
        String fileToCreate = String.valueOf(fileNumber) + "." + extension;
        return fileToCreate;
    }




    public void run() {
        System.out.println("Wątek numer " + this.number + " rozpoczął działanie");
//        String fileName;
//        /**
//         * Blokujemy dostęp do mapFolders w celu zsynchronizowania tego obiektu
//         */
//        synchronized (CounterMultiThread.mapFolders) {
//            fileName = getNewFileName(this.destinationFolderPath, extension);
//        }
//        String fileToCreatePath = this.destinationFolderPath + FileExtended.separatorChar + fileName;
//        FileExtended fileToCreate = new FileExtended(fileToCreatePath);
//        FileExtended source = new FileExtended(this.sourceFilePath);
//        try {
//            source.copyFile(fileToCreate);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        System.out.println("Wątek numer " + this.number + " zakończył działanie");
    }
}
