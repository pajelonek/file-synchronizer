package com.licencjat.filesynchronizer.domain;

import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRQ;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;

import org.springframework.http.ResponseEntity;

public interface FileUpdaterInterface {
    ResponseEntity<UpdateFilesRS> updateFilesRSCreator(UpdateFilesRQ updateFilesRQ);
}
