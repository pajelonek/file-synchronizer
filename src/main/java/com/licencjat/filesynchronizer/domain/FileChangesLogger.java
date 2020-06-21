package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.FileLogger;
import com.licencjat.filesynchronizer.model.updatefiles.LogFile;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileChangesLogger {

    private List<LogFile> logFileList = new ArrayList<>();

    Logger logger = LoggerFactory.getLogger(FileChangesLogger.class);

    public void addLogFile(UpdateFile updateFile, String hostname){
        logger.info("Adding logfile for {} from {}",updateFile.getFilePath(), hostname);
        LogFile logFile = createLogFile(updateFile, hostname);
        logFileList.add(logFile);
    }

    public long getCurrentTime(){
        return Instant.now().getEpochSecond();
    }

    public ResponseEntity<FileLogger> getLogFileList() {
        FileLogger fileLogger = new FileLogger();
        fileLogger.setCurrentTime(String.valueOf(getCurrentTime()));
        fileLogger.setLogFileList(logFileList);
        return ResponseEntity.ok().body(fileLogger);
    }

    public LogFile createLogFile(UpdateFile updateFile, String hostname){
        LogFile logFile = new LogFile();
        logFile.setTimeOfChange(String.valueOf(getCurrentTime()));
        logFile.setLastModified(updateFile.getLastModified());
        logFile.setFilePath(updateFile.getFilePath());
        logFile.setHost(hostname);
        logFile.setAction(updateFile.getAction());
        return logFile;
    }

    public void cleanLogFileList(){
     logFileList = new ArrayList<>();
    }
}
