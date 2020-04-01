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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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


        if (validate(fileRQArrayList)) {

            List<String> localFilePaths = mapFilePaths(fileRQArrayList);
            //\test1.txt
            //\test\test1.txt
            //\test\test2.txt

            Set<String> uniqueFilePathsPrefixes = localFilePaths.stream()
                    .map(localFilePath -> localFilePath.substring(0, localFilePath.lastIndexOf('\\')))
                    .collect(Collectors.toSet());
            //""
            //"\test"
            Map<String, List<String>> mappedFilePaths = new HashMap<>();

            uniqueFilePathsPrefixes.forEach(filePathPrefix -> {
                List<String> mappedLocalFilePaths = localFilePaths.stream()
                        .filter(el -> el.substring(0, el.lastIndexOf('\\')).equals(filePathPrefix))
                        .collect(Collectors.toList());
                mappedFilePaths.put(filePathPrefix, mappedLocalFilePaths);
            });
            //"" - {\test1.txt}
            //"\test" - {\test1.txt, \test2.txt}

            mappedFilePaths.forEach((prefix, sufix) -> {
                List<String> sources = sufix.stream()
                        .map(source -> userLocalDirectory+source)
                        .collect(Collectors.toList());

                rSyncFileUpdaterExecutor
                        .setSources(sources)
                        .setDestination(userRemoteDirectory+prefix+"\\")
                        .execute();

            });


        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        return ResponseEntity.ok(new UpdateFilesRSBuilder("success").build());
    }

    private boolean validate(List<FileRQList> fileRQArrayList) {
        return fileRQArrayList.stream()
                .map(FileRQList::getFilePath)
                .allMatch(path -> path.startsWith(userLocalDirectory));
    }

    private List<String> mapFilePaths(List<FileRQList> fileRQArrayList) {
        return fileRQArrayList.stream()
                .map(FileRQList::getFilePath)
                .map(el -> el.replace(userLocalDirectory, ""))
                .collect(Collectors.toList());
    }
}
