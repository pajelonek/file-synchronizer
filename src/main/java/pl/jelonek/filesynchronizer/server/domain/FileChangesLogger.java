package pl.jelonek.filesynchronizer.server.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.jelonek.filesynchronizer.server.model.updatefiles.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileChangesLogger {

    private List<LogFile> logFileList = new ArrayList<>();

    private boolean isSynchronizedTime = false;

    private String lastSynchronizedTime;

    Logger logger = LoggerFactory.getLogger(FileChangesLogger.class);

    public void addLogFile(UpdateFile updateFile, String hostname) {
        logger.info("Adding logfile for {} from {}", updateFile.getFilePath(), hostname);
        checkIfLastSynchronizedTimeIsSet();
        setLastSynchronizedTime(String.valueOf(getCurrentTime()));
        LogFile logFile = createLogFile(updateFile, hostname);
        logFileList.add(logFile);
    }

    private void checkIfLastSynchronizedTimeIsSet() {
        if (!isSynchronizedTime) {
            setIsSynchronizedTime(true);
            setLastSynchronizedTime(String.valueOf(getCurrentTime()));
        }
    }

    public long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public ResponseEntity<FileLogger> getLogFileList() {
        checkIfLastSynchronizedTimeIsSet();
        FileLogger fileLogger = createFileLogger();
        return ResponseEntity.ok().body(fileLogger);
    }

    private FileLogger createFileLogger() {
        FileLogger fileLogger = new FileLogger();
        fileLogger.setCurrentTime(String.valueOf(getCurrentTime()));
        fileLogger.setLogFileList(logFileList);
        fileLogger.setLastSynchronizedTime(lastSynchronizedTime);
        return fileLogger;
    }

    public LogFile createLogFile(UpdateFile updateFile, String hostname) {
        LogFile logFile = new LogFile();
        logFile.setTimeOfChange(String.valueOf(getCurrentTime()));
        logFile.setLastModified(updateFile.getLastModified());
        logFile.setFilePath(updateFile.getFilePath());
        logFile.setHost(hostname);
        logFile.setAction(updateFile.getAction());
        return logFile;
    }

    public ResponseEntity<UpdateFilesRS> registerFiles(UpdateFilesRQ updateFilesRQ) {
        List<UpdateFile> updateFileList = updateFilesRQ.getUpdateFile();
        UpdateFilesRS updateFilesRS = new UpdateFilesRS();
        updateFilesRS.setStatus("ok");
        if(!updateFileList.isEmpty()) {
            for (UpdateFile updateFile : updateFileList) {
                addLogFile(updateFile, updateFilesRQ.getHost());
            }
        }
        return ResponseEntity.ok().body(updateFilesRS);
    }


    public void cleanLogFileList() {
        logFileList = new ArrayList<>();
    }

    public List<LogFile> getLogFile() {
        return logFileList;
    }

    public void setLogFile(List<LogFile> logFileList) {
        this.logFileList = logFileList;
    }

    public boolean getIsSynchronizedTime(){
        return isSynchronizedTime;
    }

    public boolean setIsSynchronizedTime(boolean isSynchronizedTime){
        return this.isSynchronizedTime = isSynchronizedTime;
    }

    public String getLastSynchronizedTime() {
        return lastSynchronizedTime;
    }

    public void setLastSynchronizedTime(String lastSynchronizedTime) {
        this.lastSynchronizedTime = lastSynchronizedTime;
    }
}
