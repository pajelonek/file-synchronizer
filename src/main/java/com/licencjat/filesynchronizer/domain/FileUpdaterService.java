package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.UpdateFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFileStatus;
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
import java.util.*;

@Component
public class FileUpdaterService {

    @Value("${server.absolute.path}")
    private String serverDirectoryFullPath;

    private final FileChangesLogger fileChangesLogger;

    Logger logger = LoggerFactory.getLogger(FileUpdaterService.class);

    public FileUpdaterService(FileChangesLogger fileChangesLogger) {
        this.fileChangesLogger = fileChangesLogger;
    }

    public List<UpdateFile> getServerFileList(String userLocalPath) {
        List<UpdateFile> updateFile = new ArrayList<>();
        listFilesFromDirectory(Paths.get(userLocalPath), updateFile);
        return updateFile;
    }

    public void listFilesFromDirectory(Path path, List<UpdateFile> updateFile) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path pathInSubfolder : stream)
                if (!Files.isDirectory(pathInSubfolder)) {
                    UpdateFile fileRQ = new UpdateFile();
                    File file = pathInSubfolder.toFile();
                    fileRQ.setFilePath(cutPrefixFromFilePath(file.getPath()));
                    fileRQ.setLastModified(String.valueOf(file.lastModified()));
                    updateFile.add(fileRQ);
                } else listFilesFromDirectory(pathInSubfolder, updateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cutPrefixFromFilePath(String path) {
        return path.replace(serverDirectoryFullPath, "");
    }

    public ResponseEntity<UpdateFilesRQ> getFileList() {
        UpdateFilesRQ getFileListRS = new UpdateFilesRQ();
        getFileListRS.setName("UpdateFilesRQ");
        getFileListRS.setMainFolder(cutMainDirectoryFromPath(serverDirectoryFullPath));
        getFileListRS.setUpdateFile(getServerFileList(serverDirectoryFullPath));
        return ResponseEntity.ok().body(getFileListRS);
    }

    private String cutMainDirectoryFromPath(String serverMainDirectory) {
        return serverMainDirectory.replace(serverDirectoryFullPath.subSequence(0, serverDirectoryFullPath.lastIndexOf("\\")), "");
    }

    public ResponseEntity<UpdateFilesRS> setModificationDates(UpdateFilesRQ updateFilesRQ) {
        List<UpdateFileStatus> updateFilesStatusList = new ArrayList<>();
        List<UpdateFile> updateFile = updateFilesRQ.getUpdateFile();
        for (UpdateFile fileRQ : updateFile) {
            UpdateFileStatus updateFileStatus = new UpdateFileStatus();
            updateFileStatus.setFilePath(fileRQ.getFilePath());
            logger.info("Changing modification date for file: " + fileRQ.getFilePath());
            File file = new File(serverDirectoryFullPath + fileRQ.getFilePath());
            if (file.exists() && file.setLastModified(Long.parseLong(fileRQ.getLastModified()))) {
                updateFileStatus.setStatus("OK");
                updateFileStatus.setLastModified(String.valueOf(file.lastModified()));
                logger.info("Successfully modified date for file: " + fileRQ.getFilePath());
                fileChangesLogger.addLogFile(fileRQ, updateFilesRQ.getHost());
            } else {
                logger.info("Could not modified file on server: " + fileRQ.getFilePath());
                updateFileStatus.setStatus("ERROR");
            }
            updateFilesStatusList.add(updateFileStatus);
        }

        UpdateFilesRS updateFilesRS = createUpdateFilesRS(updateFilesStatusList);
        return ResponseEntity.ok().body(updateFilesRS);
    }

    public ResponseEntity<UpdateFilesRS> removeFiles(UpdateFilesRQ updateFilesRQ) {
        List<UpdateFileStatus> updateFilesStatusList = new ArrayList<>();
        List<UpdateFile> updateFile = updateFilesRQ.getUpdateFile();
        for (UpdateFile fileRQ : updateFile) {
            UpdateFileStatus updateFileStatus = new UpdateFileStatus();
            updateFileStatus.setFilePath(fileRQ.getFilePath());
            logger.info("Removing file: " + fileRQ.getFilePath());
            File file = new File(serverDirectoryFullPath + fileRQ.getFilePath());
            if (file.exists() && file.delete()) {
                updateFileStatus.setStatus("OK");
                logger.info("Successfully deleted file on server: " + fileRQ.getFilePath());
                fileChangesLogger.addLogFile(fileRQ, updateFilesRQ.getHost());
            } else {
                logger.info("Could not remove file on server: " + fileRQ.getFilePath());
                updateFileStatus.setStatus("ERROR");
            }
            updateFilesStatusList.add(updateFileStatus);
        }
        UpdateFilesRS updateFilesRS = createUpdateFilesRS(updateFilesStatusList);
        return ResponseEntity.ok().body(updateFilesRS);
    }

    private UpdateFilesRS createUpdateFilesRS(List<UpdateFileStatus> updateFilesStatusList) {
        UpdateFilesRS updateFilesRS = new UpdateFilesRS();

        boolean result = updateFilesStatusList.stream().allMatch(file -> file.getStatus().equals("OK"));
        if (result) updateFilesRS.setStatus("ok");
        else updateFilesRS.setStatus("error");

        updateFilesRS.setUpdateFile(updateFilesStatusList);
        return updateFilesRS;
    }
}
