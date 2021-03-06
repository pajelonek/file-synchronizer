package pl.jelonek.filesynchronizer.server.domain;

import pl.jelonek.filesynchronizer.server.model.updatefiles.UpdateFile;
import pl.jelonek.filesynchronizer.server.model.updatefiles.UpdateFileStatus;
import pl.jelonek.filesynchronizer.server.model.updatefiles.UpdateFilesRQ;
import pl.jelonek.filesynchronizer.server.model.updatefiles.UpdateFilesRS;
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

    @Value("${server.ssh.path}")
    private String serverSshPath;

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
        getFileListRS.setMainFolder(serverSshPath);
        getFileListRS.setUpdateFile(getServerFileList(serverDirectoryFullPath));
        return ResponseEntity.ok().body(getFileListRS);
    }

    public ResponseEntity<UpdateFilesRS> removeFiles(UpdateFilesRQ updateFilesRQ) {
        List<UpdateFileStatus> updateFilesStatusList = new ArrayList<>();
        List<UpdateFile> updateFileList = updateFilesRQ.getUpdateFile();
        for (UpdateFile updateFile : updateFileList) {
            UpdateFileStatus updateFileStatus = new UpdateFileStatus();
            updateFileStatus.setFilePath(updateFile.getFilePath());
            logger.info("Removing file: " + updateFile.getFilePath());
            File file = new File(serverDirectoryFullPath + updateFile.getFilePath());
            if (file.exists() && file.delete()) {
                updateFileStatus.setStatus("OK");
                logger.info("Successfully deleted file on server: " + updateFile.getFilePath());
                fileChangesLogger.addLogFile(updateFile, updateFilesRQ.getHost());
            } else {
                logger.info("Could not remove file on server: " + updateFile.getFilePath());
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
