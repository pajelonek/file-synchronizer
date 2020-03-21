package com.licencjat.filesynchronizer.api;


import com.licencjat.filesynchronizer.domain.FileUpdaterService;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestClientController {

    private FileUpdaterService fileUpdaterService = new FileUpdaterService();

    @RequestMapping(value = "/updateFiles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public HttpEntity<UpdateFilesRS> updateFiles(@RequestHeader(name = "Content-Type", required = true) String contentType,
                                                 @RequestHeader(name = "Accept", required = true) String accept,
                                                 @RequestBody UpdateFilesRQ updateFilesRQ) {

        return fileUpdaterService.updateFilesRSCreator(updateFilesRQ);
    }



    @RequestMapping(value = "/compareFiles", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public HttpEntity<String> compareFiles() {
        return null;
    }
}