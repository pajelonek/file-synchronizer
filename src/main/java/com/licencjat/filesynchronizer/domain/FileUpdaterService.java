package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRSBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class FileUpdaterService {

    public HttpEntity<UpdateFilesRS> updateFilesRSCreator(UpdateFilesRQ updateFilesRQ) {

        updateFiles();

        UpdateFilesRS updateFilesRS = new UpdateFilesRSBuilder("success").build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new ResponseEntity<>(
                updateFilesRS,
                httpHeaders,
                HttpStatus.OK);
    }

    private void updateFiles() {
    }

}
