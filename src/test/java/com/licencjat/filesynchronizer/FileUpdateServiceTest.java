package com.licencjat.filesynchronizer;

import static org.junit.Assert.*;

import com.licencjat.filesynchronizer.domain.FileChangesLogger;
import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.*;
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
public class FileUpdateServiceTest {

    @Autowired
    FileUpdaterService fileUpdaterService;

    @Autowired
    FileChangesLogger fileChangesLogger;

    @Value("${server.absolute.path}")
    private String userAbsolutePath;

    Logger logger = LoggerFactory.getLogger(FileUpdateServiceTest.class);

    private List<String> setOne;
    private List<String> setTwo;
    private List<String> setOneDirectoryList;
    private List<String> setTwoDirectoryList;
    private final String hostName = "TEST";
    private final String mainTestDirectory = "/testDirectory";

    @Test
    public void removeFilesTestSetOne() {
        //when
        createResourcesFilesSetOne();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetOne();

        //then
        ResponseEntity<UpdateFilesRS> responseEntity = fileUpdaterService.removeFiles(updateFilesRQ);
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getUpdateFile().size(), setOne.size());
        assertEquals(responseEntity.getBody().getStatus(), "ok");
        boolean result = responseEntity.getBody().getUpdateFile().stream()
                .allMatch(updateFileStatus -> updateFileStatus.getStatus().equals("OK"));
        assertTrue(result);

        List<LogFile> logFileList = Objects.requireNonNull(fileChangesLogger.getLogFileList().getBody()).getLogFileList();
        List<String> setOneFromLogger = logFileList.stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertEquals(setOne, setOneFromLogger);
        assertEquals(setOne.size(), setOneFromLogger.size());

        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void removeFilesTestSetTwo() {
        //when
        createResourcesFilesSetTwo();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetTwo();

        //then
        ResponseEntity<UpdateFilesRS> responseEntity = fileUpdaterService.removeFiles(updateFilesRQ);
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getUpdateFile().size(), setTwo.size());
        assertEquals(responseEntity.getBody().getStatus(), "ok");
        boolean result = responseEntity.getBody().getUpdateFile().stream()
                .allMatch(updateFileStatus -> updateFileStatus.getStatus().equals("OK"));
        assertTrue(result);

        List<LogFile> logFileList = Objects.requireNonNull(fileChangesLogger.getLogFileList().getBody()).getLogFileList();
        List<String> setOneFromLogger = logFileList.stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertEquals(setTwo, setOneFromLogger);
        assertEquals(setTwo.size(), setOneFromLogger.size());

        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void removeFilesTestEmptySet() {
        //when

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQEmptyList();

        //then
        ResponseEntity<UpdateFilesRS> responseEntity = fileUpdaterService.removeFiles(updateFilesRQ);
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        assertTrue(Objects.requireNonNull(responseEntity.getBody()).getUpdateFile().isEmpty());
        assertEquals(responseEntity.getBody().getStatus(), "ok");

        List<LogFile> logFileList = Objects.requireNonNull(fileChangesLogger.getLogFileList().getBody()).getLogFileList();
        assertTrue(logFileList.isEmpty());
        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void removeFilesTestWrongSet() {
        //when
        createResourcesFilesSetOne();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetOne();
        int indexOfWrongFile = 1;
        int numberOfWrongFiles = 1;
        updateFilesRQ.getUpdateFile().get(indexOfWrongFile).setFilePath("abcd");


        //then
        ResponseEntity<UpdateFilesRS> responseEntity = fileUpdaterService.removeFiles(updateFilesRQ);
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getUpdateFile().size(), setOne.size());
        assertEquals(responseEntity.getBody().getStatus(), "error");
        boolean result = responseEntity.getBody().getUpdateFile().stream()
                .allMatch(updateFileStatus -> updateFileStatus.getStatus().equals("OK"));
        assertFalse(result);
        assertEquals(responseEntity.getBody().getUpdateFile().get(indexOfWrongFile).getStatus(), "ERROR");

        List<LogFile> logFileList = Objects.requireNonNull(fileChangesLogger.getLogFileList().getBody()).getLogFileList();
        List<String> setOneFromLogger = logFileList.stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());

        assertEquals(setOne.size(), setOneFromLogger.size() + numberOfWrongFiles);
        assertEquals(logFileList.size(), setOneFromLogger.size());

        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void getFileListTestSetTwo() {
        //when
        createResourcesFilesSetTwo();

        //given


        //then
        List<UpdateFile> updateFileList = fileUpdaterService.getServerFileList(userAbsolutePath + "/testDirectory/");

        assertEquals(updateFileList.size(), setTwo.size());
        List<String> setTwoFromResponse = updateFileList.stream()
                .map(UpdateFile::getFilePath).collect(Collectors.toList());

        assertEquals(setTwo, setTwoFromResponse);
        assertFalse(updateFileList.isEmpty());

        boolean result = updateFileList.stream().allMatch(file -> Long.parseLong(file.getLastModified()) > 0);
        assertTrue(result);
        fileChangesLogger.cleanLogFileList();
    }

    @Test
    public void setModificationDateTestSetTwo() {
        //when
        createResourcesFilesSetTwo();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetTwo();
        List<String> modificationDateList = fileUpdaterService.getServerFileList(userAbsolutePath + "\\testDirectory\\").stream()
                .map(UpdateFile::getLastModified)
                .collect(Collectors.toList());

        //then
        ResponseEntity<UpdateFilesRS> responseEntity = fileUpdaterService.setModificationDates(updateFilesRQ);
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getUpdateFile().size(), setTwo.size());
        assertEquals(responseEntity.getBody().getStatus(), "ok");
        List<String> modificationDateListAfterRequest = responseEntity.getBody().getUpdateFile().stream()
                .map(UpdateFileStatus::getLastModified)
                .collect(Collectors.toList());

        assertEquals(modificationDateListAfterRequest, modificationDateList);

        boolean result = responseEntity.getBody().getUpdateFile().stream()
                .allMatch(updateFileStatus -> updateFileStatus.getStatus().equals("OK"));
        assertTrue(result);

        List<LogFile> logFileList = Objects.requireNonNull(fileChangesLogger.getLogFileList().getBody()).getLogFileList();
        List<String> setOneFromLogger = logFileList.stream()
                .map(LogFile::getFilePath)
                .collect(Collectors.toList());
        assertEquals(setTwo, setOneFromLogger);
        assertEquals(setTwo.size(), setOneFromLogger.size());

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

    private UpdateFilesRQ createUpdateFilesRQEmptyList() {
        UpdateFilesRQ updateFilesRQ = new UpdateFilesRQ();
        updateFilesRQ.setUpdateFile(new ArrayList<>());
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
