package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import com.licencjat.filesynchronizer.model.rsync.RSyncFileUpdaterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileUpdaterService implements FileUpdaterInterface {

    @Value("${user.remote.directory}")
    private String userRemoteDirectory;

    @Autowired
    RSyncFileUpdaterProvider rSyncFileUpdaterProvider;

    public ResponseEntity<UpdateFilesRS> updateFilesRSCreator(UpdateFilesRQ updateFilesRQ) {

        List<FileRQList> fileRQList = updateFilesRQ.getFileRQList();

        if (fileRQList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return rSyncFileUpdaterProvider.processUpdating(fileRQList);
    }

    public ResponseEntity<UpdateFilesRS> compareFilesRSCreator(UpdateFilesRQ updateFilesRQ) {

        List<FileRQList> clientFileList = updateFilesRQ.getFileRQList();

        if (clientFileList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<FileRQList> serverFileList = getServerFileList();

        return rSyncFileUpdaterProvider.processComparing(serverFileList, clientFileList);
    }

    private List<FileRQList> getServerFileList() {
        List<FileRQList> fileRQList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(userRemoteDirectory))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    FileRQList fileRQ = new FileRQList();
                    File file = path.toFile();
                    fileRQ.setFilePath(file.getPath());
                    fileRQ.setLastModified(String.valueOf(file.lastModified()));
                    fileRQList.add(fileRQ);
                }
                else listFilesFromDirectory(path, fileRQList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileRQList;
    }

    public void listFilesFromDirectory(Path path,  List<FileRQList> fileRQList) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path pathInSubfolder : stream)
                if (!Files.isDirectory(pathInSubfolder)) {
                    FileRQList fileRQ = new FileRQList();
                    File file = pathInSubfolder.toFile();
                    fileRQ.setFilePath(file.getPath());
                    fileRQ.setLastModified(String.valueOf(file.lastModified()));
                    fileRQList.add(fileRQ);
                }
                else listFilesFromDirectory(pathInSubfolder, fileRQList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
