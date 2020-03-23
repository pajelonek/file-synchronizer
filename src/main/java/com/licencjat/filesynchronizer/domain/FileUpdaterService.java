package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRSBuilder;
import com.licencjat.filesynchronizer.model.rsync.RSyncFileUpdaterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class FileUpdaterService {

    @Autowired
    RSyncFileUpdaterProvider rSyncFileUpdaterProvider;

    public HttpEntity<UpdateFilesRS> updateFilesRSCreator(UpdateFilesRQ updateFilesRQ) {

        ArrayList<FileRQ> fileRQList = updateFilesRQ.getFileRQList();

        if(!fileRQList.isEmpty()){
            rSyncFileUpdaterProvider.process(fileRQList);
        }


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
