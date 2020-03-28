package com.licencjat.filesynchronizer.api;


import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestClientController {

    @Autowired
    private FileUpdaterService fileUpdaterService;

    @PostMapping(value = "/updateFiles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateFilesRS> updateFiles(@RequestBody UpdateFilesRQ updateFilesRQ) {
        return fileUpdaterService.updateFilesRSCreator(updateFilesRQ);
    }


    @PostMapping(value = "/compareFiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<String> compareFiles() {
        return null;
    }
}