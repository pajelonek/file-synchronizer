package com.licencjat.filesynchronizer;


import com.licencjat.filesynchronizer.domain.FileChangesLogger;
import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.FileLogger;
import com.licencjat.filesynchronizer.model.updatefiles.LogFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Order;
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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class FileChangesLoggerTest {

    @Autowired
    FileChangesLogger fileChangesLogger;

    @Autowired
    FileUpdaterService fileUpdaterService;

    @Value("${server.absolute.path}")
    private String userAbsolutePath;

    Logger logger = LoggerFactory.getLogger(FileUpdateServiceTest.class);

    private List<String> setOne;
    private List<String> setTwo;
    private List<String> setOneDirectoryList;
    private List<String> setTwoDirectoryList;
    private final String hostName = "TEST";
    private final String action = "TEST";
    private final String mainTestDirectory = "/testDirectory";

    @Test
    @Order(1)
    void contextLoads() {
        assertThat(fileChangesLogger).isNotNull();
        assertThat(fileUpdaterService).isNotNull();
    }

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
        assertThat(fileLoggerResponseEntity.getStatusCodeValue()).isEqualTo(200);

        assertThat(Objects.requireNonNull(fileLoggerResponseEntity.getBody()).getLogFileList().size()).isEqualTo(setOne.size());
        List<String> fileListFromLogger = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertThat(setOne).isEqualTo(fileListFromLogger);

        List<String> fileNamesFromLogger = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertThat(setOne).isEqualTo(fileNamesFromLogger);

        boolean testFieldsAreCorrect = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .allMatch(logFile -> logFile.getAction().equals(hostName) && logFile.getHost().equals(action));
        assertThat(testFieldsAreCorrect).isTrue();
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
        assertThat(fileLoggerResponseEntity.getStatusCodeValue()).isEqualTo(200);

        assertThat(Objects.requireNonNull(fileLoggerResponseEntity.getBody()).getLogFileList().size()).isEqualTo(setTwo.size());
        List<String> fileListFromLogger = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertThat(setTwo).isEqualTo(fileListFromLogger);
        boolean testFieldsAreCorrect = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .allMatch(logFile -> logFile.getAction().equals(hostName) && logFile.getHost().equals(action));
        assertThat(testFieldsAreCorrect).isTrue();
        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void getLogFileList() {
        //when
        createResourcesFilesSetTwo();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetTwo();
        fileUpdaterService.registerFiles(updateFilesRQ);

        //then
        ResponseEntity<FileLogger> fileLoggerResponseEntity = fileChangesLogger.getLogFileList();
        assertThat(fileLoggerResponseEntity.getStatusCodeValue()).isEqualTo(200);

        List<String> fileListFromLogger = Objects.requireNonNull(fileLoggerResponseEntity.getBody()).getLogFileList().stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());

        assertThat(fileListFromLogger).isEqualTo(setTwo);
        boolean testFieldsAreCorrect = fileLoggerResponseEntity.getBody().getLogFileList().stream()
                .allMatch(logFile -> logFile.getAction().equals(hostName) && logFile.getHost().equals(action));
        assertThat(testFieldsAreCorrect).isTrue();
        fileChangesLogger.cleanLogFileList();
    }

    private void createResourcesFilesSetOne() {
        createSetOne();
        createSetOneDirectoryList();
        try {
            createDirectory(userAbsolutePath + mainTestDirectory);
            for (String directory : setOneDirectoryList) {
                createDirectory(userAbsolutePath + mainTestDirectory + directory);
            }

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

    private void createSetOneDirectoryList() {
        setOneDirectoryList = new ArrayList<>();
        setOneDirectoryList.add("/SubDirectory");
        setOneDirectoryList.add("/SubDirectory/SubSubDirectory");
    }

    private void createResourcesFilesSetTwo() {
        createSetTwo();
        createSetTwoDirectoryList();
        try {
            createDirectory(userAbsolutePath + mainTestDirectory);
            for (String directory : setTwoDirectoryList) {
                createDirectory(userAbsolutePath + mainTestDirectory + directory);
            }

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

    private void createSetTwoDirectoryList() {
        setTwoDirectoryList = new ArrayList<>();
        setTwoDirectoryList.add("/SubDirectory");
        setTwoDirectoryList.add("/SubDirectory/SubSubDirectory");
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

    void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdir())
                logger.info("Successfully created test directory '{}'", path);
            else throw new Error("Could not create directory");
            directory.deleteOnExit();
        } else logger.info("Test directory '{}' already exists", path);

    }
}
