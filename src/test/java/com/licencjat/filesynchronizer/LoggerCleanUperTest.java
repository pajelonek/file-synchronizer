package com.licencjat.filesynchronizer;

import com.licencjat.filesynchronizer.domain.FileChangesLogger;
import com.licencjat.filesynchronizer.domain.LoggerCleanUper;
import com.licencjat.filesynchronizer.model.updatefiles.LogFile;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LoggerCleanUperTest {

    @Autowired
    LoggerCleanUper loggerCleanUper;

    @Autowired
    FileChangesLogger fileChangesLogger;

    private List<String> setOne;
    @Test
    @Order(1)
    void contextLoads(){
        assertThat(loggerCleanUper).isNotNull();
        assertThat(fileChangesLogger).isNotNull();
    }

    @Test
    void cleanUpLogFileTest(){
        //when
        long currentTime = Instant.now().getEpochSecond();
        int timeToMockLogFile = 50;
        long newerTime = currentTime + timeToMockLogFile;
        long olderTime = currentTime - timeToMockLogFile;
        createSetOne();
        List<LogFile> testLogFileList = mockLogFileList(setOne, newerTime, olderTime);

        //given
        List<LogFile> cleanLogFile = loggerCleanUper.cleanUpLogFile(testLogFileList, currentTime);

        //then
        assertThat(cleanLogFile.size()).isEqualTo(setOne.size() - 1);
        boolean result = cleanLogFile.stream()
                .allMatch(logFile -> Long.parseLong(logFile.getTimeOfChange()) > currentTime);
        assertThat(result).isTrue();

        fileChangesLogger.cleanLogFileList();
    }

    private List<LogFile> mockLogFileList(List<String> setOne, long newerTime, long olderTime) {
        List<LogFile> logFileList = new ArrayList<>();
        for(String filePath : setOne){
            LogFile logFile = new LogFile();
            logFile.setHost("TEST");
            logFile.setAction("TEST");
            logFile.setLastModified("123123");
            logFile.setTimeOfChange(String.valueOf(newerTime));
            logFile.setFilePath(filePath);
            logFileList.add(logFile);
        }
        setLastLogFileAsOlder(logFileList, olderTime);
        return logFileList;
    }

    private void setLastLogFileAsOlder(List<LogFile> logFileList, long olderTime) {
        int sizeOfSetOne = setOne.size();
        logFileList.get(sizeOfSetOne - 1).setTimeOfChange(String.valueOf(olderTime));
    }

    private void createSetOne() {
        setOne = new ArrayList<>();
        setOne.add("/testDirectory/fileOne.txt");
        setOne.add("/testDirectory/fileTwo.txt");
        setOne.add("/testDirectory/SubDirectory/FileOne.txt");
    }


}
