package com.licencjat.filesynchronizer;

import static org.junit.Assert.*;

import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFileStatus;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
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

    @Value("${user.absolute.path}")
    private String userAbsolutePath;

    Logger logger = LoggerFactory.getLogger(FileUpdateServiceTest.class);

    private List<String> setOne;
    private List<String> setTwo;

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
    }

    @Test
    public void removeFilesTestWrongSet() {
        //when
        createResourcesFilesSetOne();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetOne();
        int indexOfWrongFile = 1;
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
    }

    @Test
    public void setModificationDateTestSetOne() {
        //when
        createResourcesFilesSetOne();

        //given
        UpdateFilesRQ updateFilesRQ = createUpdateFilesRQSetOne();
        List<String> modificationDateList = fileUpdaterService.getServerFileList(userAbsolutePath + "\\testDirectory\\").stream()
                .map(UpdateFile::getLastModified)
                .collect(Collectors.toList());

        //then
        ResponseEntity<UpdateFilesRS> responseEntity = fileUpdaterService.setModificationDates(updateFilesRQ);
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getUpdateFile().size(), setOne.size());
        assertEquals(responseEntity.getBody().getStatus(), "ok");
        List<String> modificationDateListAfterRequest = responseEntity.getBody().getUpdateFile().stream()
                .map(UpdateFileStatus::getLastModified)
                .collect(Collectors.toList());

        assertEquals(modificationDateListAfterRequest, modificationDateList);

        boolean result = responseEntity.getBody().getUpdateFile().stream()
                .allMatch(updateFileStatus -> updateFileStatus.getStatus().equals("OK"));
        assertTrue(result);
    }


    private void createResourcesFilesSetOne() {
        createSetOne();
        try {
            File directory = new File(userAbsolutePath + "/testDirectory");
            if(!directory.exists()) {
                if (directory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                directory.deleteOnExit();
            } else logger.info("Test directory '{}/testDirectory' already exists", userAbsolutePath);
            File subDirectory = new File(userAbsolutePath + "/testDirectory/SubDirectory");
            if(!subDirectory.exists()) {
                if (subDirectory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory/SubDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                subDirectory.deleteOnExit();
            } else logger.info("Test directory '{}/testDirectory/SubDirectory' already exists", userAbsolutePath);
            for(String filePath : setOne){
                File file = new File(userAbsolutePath + filePath);
                FileUtils.touch(file);
                file.deleteOnExit();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void createSetOne(){
        setOne = new ArrayList<>();
        setOne.add("/testDirectory/fileOne.txt");
        setOne.add("/testDirectory/fileTwo.txt");
        setOne.add("/testDirectory/SubDirectory/FileOne.txt");
    }

    private void createResourcesFilesSetTwo() {
        createSetTwo();
        try {
            File directory = new File(userAbsolutePath + "/testDirectory");
            if(!directory.exists()) {
                if (directory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                directory.deleteOnExit();
            }
            else logger.info("Test directory '{}/testDirectory' already exists", userAbsolutePath);
            File subDirectory = new File(userAbsolutePath + "/testDirectory/SubDirectory");
            if(!subDirectory.exists()) {
                if (subDirectory.mkdir())
                    logger.info("Successfully created test directory '{}/testDirectory/SubDirectory'", userAbsolutePath);
                else throw new Error("Could not create directory");
                subDirectory.deleteOnExit();
            }  logger.info("Test directory '{}/testDirectory' already exists", userAbsolutePath);
            for(String filePath : setTwo){
                File file = new File(userAbsolutePath + filePath);
                FileUtils.touch(file);
                file.deleteOnExit();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void createSetTwo(){
        setTwo = new ArrayList<>();
        setTwo.add("\\testDirectory\\fileOne.txt");
        setTwo.add("\\testDirectory\\fileTwo.txt");
        setTwo.add("\\testDirectory\\SubDirectory\\FileOne.txt");
        setTwo.add("\\testDirectory\\SubDirectory\\FileTwo.txt");
    }

    private UpdateFilesRQ createUpdateFilesRQSetOne(){
        List<UpdateFile> updateFileList = new ArrayList<>();
        for(String filePath : setOne){
            File file = new File(userAbsolutePath + filePath);
            UpdateFile updateFile = new UpdateFile();
            updateFile.setFilePath(filePath);
            updateFile.setLastModified(String.valueOf(file.lastModified()));
            updateFileList.add(updateFile);
        }
        UpdateFilesRQ updateFilesRQ = new UpdateFilesRQ();
        updateFilesRQ.setUpdateFile(updateFileList);
        return updateFilesRQ;
    }

    private UpdateFilesRQ createUpdateFilesRQEmptyList() {
        UpdateFilesRQ updateFilesRQ = new UpdateFilesRQ();
        updateFilesRQ.setUpdateFile(new ArrayList<>());
        return updateFilesRQ;
    }
}
