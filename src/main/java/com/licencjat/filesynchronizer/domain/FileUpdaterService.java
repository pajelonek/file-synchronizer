package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FileUpdaterService implements FileUpdaterInterface {

    @Autowired
    RSyncFileUpdaterProvider rSyncFileUpdaterProvider;

    public ResponseEntity<UpdateFilesRS> updateFilesRSCreator(UpdateFilesRQ updateFilesRQ) {

        List<FileRQList> fileRQList = updateFilesRQ.getFileRQList();

        if (!fileRQList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return rSyncFileUpdaterProvider.process(fileRQList);
    }

}
