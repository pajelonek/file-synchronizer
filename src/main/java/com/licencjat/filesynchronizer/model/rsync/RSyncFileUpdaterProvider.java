package com.licencjat.filesynchronizer.model.rsync;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRSBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RSyncFileUpdaterProvider {

    @Autowired
    RSyncFileUpdaterExecutor rSyncFileUpdaterExecutor;

    @Value("${user.local.directory}")
    private String userLocalDirectory;

    @Value("${user.remote.directory}")
    private String userRemoteDirectory;

    public ResponseEntity<UpdateFilesRS> process(List<FileRQList> fileRQArrayList) {
        List<String> testSources = new ArrayList<>();
        List<String> testDestination = new ArrayList<>();

        testDestination.add(userLocalDirectory);

        fileRQArrayList.stream()
                .map(FileRQList::getFilePath)
                .forEach(testSources::add);

        if (validate(fileRQArrayList)) {
            rSyncFileUpdaterExecutor
                    .setSource(testSources)
                    .setDestinations(testDestination.get(0))
                    .execute(fileRQArrayList);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        return ResponseEntity.ok(new UpdateFilesRSBuilder("success").build());
    }

    private boolean validate(List<FileRQList> fileRQArrayList) {
        return fileRQArrayList.stream()
                .map(FileRQList::getFilePath)
                .anyMatch(path -> !path.startsWith(userRemoteDirectory));
    }
}
