package UpDown.services;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${fileRepositoryFolder}")
    private String fileRepositoryFolder;

    /**
     * @param file: file from upload Controller
     * @return new file name with extension
     * @throws IOException if folder is not writable
     */

    public String upload(MultipartFile file) throws IOException {
        //Spring prende il file (che è un MultipartFile) e intanto lo mette
        //da qualche parte nel mio PC, in una cartella temporanea!
        //va preservata l'ESTENSIONE del file
        //ci viene in aiuto commons-io
        //con una libreria che estrae l'estensione del file dal nome del file
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        //però devo CAMBIARE IL NOME DEL FILE
        //perché se due utenti caricano due file con lo stesso nome
        //si creerà un conflitto, si rompe tutto
        //quindi devo sempre fare UPLOAD DI NUOVI FILE, non rischiare di sovrascriverli
        String newFileName = UUID.randomUUID().toString();
        String completeFileName = newFileName + "." + extension;
        //controllo che esista la folder
        File finalFolder = new File(fileRepositoryFolder);
        if (!finalFolder.exists()) {
            throw new IOException("Final folder does not exist");
        }
        if (!finalFolder.isDirectory()) {
            throw new IOException("Final folder is not a directory");
        }
        //se i controlli sono andati bene, allora 1) creo la destinazione finale del file
        File finalDestination = new File(fileRepositoryFolder + "\\" + completeFileName);
        //salvo il file (che è il mio Multipart file!) nella finalDestination
        file.transferTo(finalDestination);
        return completeFileName;
    }

    public byte[] download(String fileName) throws IOException {
        File fileFromRepository = new File(fileRepositoryFolder + "\\" + fileName);
        if(!fileFromRepository.exists()) throw new IOException("File does not exist");
        return IOUtils.toByteArray(new FileInputStream(fileFromRepository));
    }

    public void remove(String fileName) throws Exception {
        File fileFromRepository = new File(fileRepositoryFolder + "\\" + fileName);
        if(!fileFromRepository.exists()) return;
        boolean deleteResult = fileFromRepository.delete();
        if(deleteResult == false) throw new Exception("Cannot delete file");
        //MA QUI NON MANCA PROPRIO L'AZIONE DELETE? NO, la fa comunque due righe sopra!
    }
}
