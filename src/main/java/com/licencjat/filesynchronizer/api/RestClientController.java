package com.licencjat.filesynchronizer.api;


import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.GetFileListRS;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestClientController {

    @Autowired
    private FileUpdaterService fileUpdaterService;

    @GetMapping(value = "/getFileList")
    public ResponseEntity<GetFileListRS> compareFiles() {
        return fileUpdaterService.getFileList();
    }

    @PostMapping(value = "/setModificationDate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateFilesRS> compareFiles(@RequestBody UpdateFilesRQ updateFilesRQ) {
        return fileUpdaterService.setModificationDate(updateFilesRQ);
    }

    @PostMapping(value = "/removeFiles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateFilesRS> removeFiles(@RequestBody UpdateFilesRQ updateFilesRQ) {
        return fileUpdaterService.removeFiles(updateFilesRQ);
    }

}