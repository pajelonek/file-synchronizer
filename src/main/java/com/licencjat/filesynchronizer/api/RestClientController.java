package com.licencjat.filesynchronizer.api;


import com.licencjat.filesynchronizer.domain.FileChangesLogger;
import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.FileLogger;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestClientController {

    private final FileUpdaterService fileUpdaterService;

    private final FileChangesLogger fileChangesLogger;

    public RestClientController(FileUpdaterService fileUpdaterService, FileChangesLogger fileChangesLogger) {
        this.fileUpdaterService = fileUpdaterService;
        this.fileChangesLogger = fileChangesLogger;
    }

    @GetMapping(value = "/getFileList")
    public ResponseEntity<UpdateFilesRQ> getFileList() {
        return fileUpdaterService.getFileList();
    }

    @GetMapping(value = "/getFileLogList")
    public ResponseEntity<FileLogger> getFileLogList() {
        return fileChangesLogger.getLogFileList();
    }


    @PostMapping(value = "/registerFiles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateFilesRS> registerFiles(@RequestBody UpdateFilesRQ updateFilesRQ) {
        return fileUpdaterService.registerFiles(updateFilesRQ);
    }

    @PostMapping(value = "/removeFiles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateFilesRS> removeFiles(@RequestBody UpdateFilesRQ updateFilesRQ) {
        return fileUpdaterService.removeFiles(updateFilesRQ);
    }

}