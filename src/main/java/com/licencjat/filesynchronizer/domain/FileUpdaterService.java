package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import com.licencjat.filesynchronizer.model.updatefiles.GetFileListRS;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class FileUpdaterService {

    @Value("${user.remote.directory}")
    private String userRemoteDirectory;

    Logger logger = LoggerFactory.getLogger(FileUpdaterService.class);

    private List<FileRQList> getServerFileList() {
        List<FileRQList> fileRQList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(userRemoteDirectory))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    FileRQList fileRQ = new FileRQList();
                    File file = path.toFile();
                    fileRQ.setFilePath(cutPrefixFromFilePath(file.getPath()));
                    fileRQ.setLastModified(String.valueOf(file.lastModified()));
                    fileRQList.add(fileRQ);
                } else listFilesFromDirectory(path, fileRQList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileRQList;
    }

    public void listFilesFromDirectory(Path path, List<FileRQList> fileRQList) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path pathInSubfolder : stream)
                if (!Files.isDirectory(pathInSubfolder)) {
                    FileRQList fileRQ = new FileRQList();
                    File file = pathInSubfolder.toFile();
                    fileRQ.setFilePath(cutPrefixFromFilePath(file.getPath()));
                    fileRQ.setLastModified(String.valueOf(file.lastModified()));
                    fileRQList.add(fileRQ);
                } else listFilesFromDirectory(pathInSubfolder, fileRQList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cutPrefixFromFilePath(String path) {
        return path.replace(userRemoteDirectory, "");
    }

    public ResponseEntity<GetFileListRS> getFileList() {
        GetFileListRS getFileListRS = new GetFileListRS();
        getFileListRS.setName("GetFileListRS");
        getFileListRS.setMainFolder(userRemoteDirectory);
        getFileListRS.setFileRQList(getServerFileList());
        return ResponseEntity.ok().body(getFileListRS);
    }

    public ResponseEntity<UpdateFilesRS> setModificationDate(UpdateFilesRQ updateFilesRQ) {
        List<FileRQList> fileRQList = updateFilesRQ.getFileRQList();
        for (FileRQList fileRQ : fileRQList) {
            logger.info("Changing modification date for file: " + fileRQ.getFilePath());
            File file = new File(userRemoteDirectory + fileRQ.getFilePath());
            if (file.exists() && file.setLastModified(Long.parseLong(fileRQ.getLastModified()))) {
                logger.info("Successfully modified date for file: " + fileRQ.getFilePath());
            } else throw new Error("Could not find file on server");
        }
        return ResponseEntity.ok().body(null);
    }

    public ResponseEntity<UpdateFilesRS> removeFiles(UpdateFilesRQ updateFilesRQ) {
        List<FileRQList> fileRQList = updateFilesRQ.getFileRQList();
        for (FileRQList fileRQ : fileRQList) {
            logger.info("Removing file: " + fileRQ.getFilePath());
            File file = new File(userRemoteDirectory + fileRQ.getFilePath());
            if (file.exists() && file.delete()) {
                logger.info("Successfully deleted file {} on server: " + fileRQ.getFilePath());
            } else throw new Error("Could not find file on server");
        }
        return ResponseEntity.ok().body(null);
    }
}
