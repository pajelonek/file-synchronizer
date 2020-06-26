package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoggerCleanUper implements Runnable {

    final FileChangesLogger fileChangesLogger;

    @Value("${cleaning.log.file.interval}")
    private long cleaningLogFileInterval;

    Logger logger = LoggerFactory.getLogger(LoggerCleanUper.class);

    public LoggerCleanUper(FileChangesLogger fileChangesLogger) {
        this.fileChangesLogger = fileChangesLogger;
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    public void run() {
        List<LogFile> logFileList = fileChangesLogger.getLogFile();

        if (!logFileList.isEmpty()) {
            long currentTimeWithInterval = Instant.now().getEpochSecond() - cleaningLogFileInterval;
            List<LogFile> newLogFile = cleanUpLogFile(logFileList, currentTimeWithInterval);

            if (newLogFile.size() < logFileList.size()) {
                logger.info("Deleted {} files from logs", logFileList.size() - newLogFile.size());
                fileChangesLogger.setLogFile(newLogFile);
            }
        }
    }

    public List<LogFile> cleanUpLogFile(List<LogFile> logFileList, long currentTimeWithInterval) {
        return logFileList.stream()
                .filter(logFile -> Long.parseLong(logFile.getTimeOfChange()) > currentTimeWithInterval)
                .collect(Collectors.toList());
    }
}

