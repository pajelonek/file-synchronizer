package com.licencjat.filesynchronizer;

import static org.junit.Assert.*;

import com.licencjat.filesynchronizer.domain.FileChangesLogger;
import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.FileLogger;
import com.licencjat.filesynchronizer.model.updatefiles.LogFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@SpringBootTest
public class FileChangesLoggerTest {

    @Autowired
    FileChangesLogger fileChangesLogger;

    @Autowired
    FileUpdaterService fileUpdaterService;

    @Value("${user.absolute.path}")
    private String userAbsolutePath;

    Logger logger = LoggerFactory.getLogger(FileUpdateServiceTest.class);

    private List<String> setOne;
    private List<String> setTwo;
    private final String hostName = "TEST";
    private final String action = "TEST";

    @Test
    public void addLogFileSetOne() {
        //when
        createResourcesFilesSetOne();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetOne();
        List<UpdateFile> updateFileList = updateFilesRQ.getUpdateFile();
        updateFileList.forEach(
                fileLog -> fileChangesLogger.addLogFile(fileLog, updateFilesRQ.getHost())
        );

        //then
        ResponseEntity<FileLogger> fileLoggerResponseEntity = fileChangesLogger.getLogFileList();
        assertEquals(fileLoggerResponseEntity.getStatusCodeValue(), 200);

        assertEquals(setOne.size(), Objects.requireNonNull(fileLoggerResponseEntity.getBody()).getLogFileList().size());
        List<String> fileListFromLogger = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertEquals(setOne, fileListFromLogger);
        List<String> fileNamesFromLogger = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertEquals(setOne, fileNamesFromLogger);

        boolean testFieldsAreCorrect = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .allMatch(logFile -> logFile.getAction().equals(hostName) && logFile.getHost().equals(action));
        assertTrue(testFieldsAreCorrect);

        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void addLogFileSetTwo() {
        //when
        createResourcesFilesSetTwo();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetTwo();
        List<UpdateFile> updateFileList = updateFilesRQ.getUpdateFile();
        updateFileList.forEach(
                fileLog -> fileChangesLogger.addLogFile(fileLog, updateFilesRQ.getHost())
        );

        //then
        ResponseEntity<FileLogger> fileLoggerResponseEntity = fileChangesLogger.getLogFileList();
        assertEquals(fileLoggerResponseEntity.getStatusCodeValue(), 200);

        assertEquals(setTwo.size(), Objects.requireNonNull(fileLoggerResponseEntity.getBody()).getLogFileList().size());
        List<String> fileListFromLogger = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertEquals(setTwo, fileListFromLogger);
        boolean testFieldsAreCorrect = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .allMatch(logFile -> logFile.getAction().equals(hostName) && logFile.getHost().equals(action));
        assertTrue(testFieldsAreCorrect);

        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void getLogFileList() {
        //when
        createResourcesFilesSetTwo();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetTwo();
        fileUpdaterService.setModificationDates(updateFilesRQ);

        //then
        ResponseEntity<FileLogger> fileLoggerResponseEntity = fileChangesLogger.getLogFileList();
        assertEquals(fileLoggerResponseEntity.getStatusCodeValue(), 200);

        List<String> fileListFromLogger = Objects.requireNonNull(fileLoggerResponseEntity.getBody()).getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());

        assertEquals(fileListFromLogger, setTwo);

        boolean testFieldsAreCorrect = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .allMatch(logFile -> logFile.getAction().equals(hostName) && logFile.getHost().equals(action));
        assertTrue(testFieldsAreCorrect);
    }

    private void createResourcesFilesSetOne() {
        createSetOne();
        try {
            File directory = new File(userAbsolutePath + "/testDirectory");
            if (!directory.exists()) {
                if (directory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                directory.deleteOnExit();
            } else logger.info("Test directory '{}/testDirectory' already exists", userAbsolutePath);
            File subDirectory = new File(userAbsolutePath + "/testDirectory/SubDirectory");
            if (!subDirectory.exists()) {
                if (subDirectory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory/SubDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                subDirectory.deleteOnExit();
            } else logger.info("Test directory '{}/testDirectory/SubDirectory' already exists", userAbsolutePath);
            for (String filePath : setOne) {
                File file = new File(userAbsolutePath + filePath);
                FileUtils.touch(file);
                file.deleteOnExit();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void createSetOne() {
        setOne = new ArrayList<>();
        setOne.add("/testDirectory/fileOne.txt");
        setOne.add("/testDirectory/fileTwo.txt");
        setOne.add("/testDirectory/SubDirectory/FileOne.txt");
    }

    private void createResourcesFilesSetTwo() {
        createSetTwo();
        try {
            File directory = new File(userAbsolutePath + "/testDirectory");
            if (!directory.exists()) {
                if (directory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                directory.deleteOnExit();
            } else logger.info("Test directory '{}/testDirectory' already exists", userAbsolutePath);
            File subDirectory = new File(userAbsolutePath + "/testDirectory/SubDirectory");
            if (!subDirectory.exists()) {
                if (subDirectory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory/SubDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                subDirectory.deleteOnExit();
            } else logger.info("Test directory '{}/testDirectory' already exists", userAbsolutePath);
            for (String filePath : setTwo) {
                File file = new File(userAbsolutePath + filePath);
                FileUtils.touch(file);
                file.deleteOnExit();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void createSetTwo() {
        setTwo = new ArrayList<>();
        setTwo.add("\\testDirectory\\fileOne.txt");
        setTwo.add("\\testDirectory\\fileTwo.txt");
        setTwo.add("\\testDirectory\\SubDirectory\\FileOne.txt");
        setTwo.add("\\testDirectory\\SubDirectory\\FileTwo.txt");
    }

    private UpdateFilesRQ createUpdateFilesRQSetOne() {
        List<UpdateFile> updateFileList = new ArrayList<>();
        for (String filePath : setOne) {
            File file = new File(userAbsolutePath + filePath);
            UpdateFile updateFile = new UpdateFile();
            updateFile.setFilePath(filePath);
            updateFile.setLastModified(String.valueOf(file.lastModified()));
            updateFile.setAction("TEST");
            updateFileList.add(updateFile);
        }
        UpdateFilesRQ updateFilesRQ = new UpdateFilesRQ();
        updateFilesRQ.setUpdateFile(updateFileList);
        updateFilesRQ.setHost(hostName);
        return updateFilesRQ;
    }

    private UpdateFilesRQ createUpdateFilesRQSetTwo() {
        List<UpdateFile> updateFileList = new ArrayList<>();
        for (String filePath : setTwo) {
            File file = new File(userAbsolutePath + filePath);
            UpdateFile updateFile = new UpdateFile();
            updateFile.setFilePath(filePath);
            updateFile.setLastModified(String.valueOf(file.lastModified()));
            updateFile.setAction("TEST");
            updateFileList.add(updateFile);
        }
        UpdateFilesRQ updateFilesRQ = new UpdateFilesRQ();
        updateFilesRQ.setUpdateFile(updateFileList);
        updateFilesRQ.setHost(hostName);
        return updateFilesRQ;
    }
}
