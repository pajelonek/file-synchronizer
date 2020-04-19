package com.licencjat.filesynchronizer.model.rsync;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRS;
import com.licencjat.filesynchronizer.model.updatefiles.UpdateFilesRSBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import java.util.stream.Collectors;

@Component
public class RSyncFileUpdaterProvider {

    @Autowired
    RSyncFileUpdaterExecutor rSyncFileUpdaterExecutor;

    @Value("${user.local.directory}")
    private String userLocalDirectory;

    @Value("${user.remote.directory}")
    private String userRemoteDirectory;

    public ResponseEntity<UpdateFilesRS> processUpdating(List<FileRQList> fileRQArrayList) {

        deleteRemovedFiles(fileRQArrayList);
        if (validate(fileRQArrayList)) {

            fileRQArrayList
                    .forEach(file -> file.setFilePath(file.getFilePath().replace(userLocalDirectory, "")));

            fileRQArrayList.stream()
                    .collect(Collectors.groupingBy(fileToUpdate -> fileToUpdate.getFilePath().substring(0, fileToUpdate.getFilePath().lastIndexOf('\\'))))
                    .forEach((prefixOfPath, fileRQ) -> {
                                List<String> sources = fileRQ.stream()
                                        .map(FileRQList::getFilePath)
                                        .map(FilePath -> userLocalDirectory + FilePath)
                                        .collect(Collectors.toList());

                                rSyncFileUpdaterExecutor
                                        .setSources(sources)
                                        .setDestination(userRemoteDirectory + prefixOfPath + "\\")
                                        .execute();

                                updateFile(fileRQ);
                            }
                    );

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        return ResponseEntity.ok(new UpdateFilesRSBuilder("success").build());
    }

    //TODO fill it
    private void deleteRemovedFiles(List<FileRQList> fileRQArrayList) {
        List<FileRQList> removedFiles = fileRQArrayList.stream()
                .filter(e -> e.getAction().equals("DELETED"))
                .collect(Collectors.toList());

        if (!removedFiles.isEmpty()) {

        }
    }

    private boolean validate(List<FileRQList> fileRQArrayList) {
        return fileRQArrayList.stream()
                .map(FileRQList::getFilePath)
                .allMatch(path -> path.startsWith(userLocalDirectory));
    }

    //TODO make correct validation for dateModificaiton
    private void updateFile(List<FileRQList> fileRQList) {
        for (FileRQList fileRQ : fileRQList) {
            File file = new File(userRemoteDirectory + fileRQ.getFilePath());
            if (!file.setLastModified(Long.parseLong(fileRQ.getLastModified()))) break;
        }
    }

    public ResponseEntity<UpdateFilesRS> processComparing(List<FileRQList> serverFileList ,List<FileRQList> clientFileList) {

        clientFileList
                .forEach(file -> file.setFilePath(file.getFilePath().replace(userLocalDirectory, "")));

        clientFileList.stream()
                .collect(Collectors.groupingBy(fileToUpdate -> fileToUpdate.getFilePath().substring(0, fileToUpdate.getFilePath().lastIndexOf('\\'))))
                .forEach((prefixOfPath, fileRQ) -> {
                            List<String> sources = fileRQ.stream()
                                    .map(FileRQList::getFilePath)
                                    .map(FilePath -> userLocalDirectory + FilePath)
                                    .collect(Collectors.toList());


                            rSyncFileUpdaterExecutor
                                    .setSources(sources)
                                    .setDestination(userRemoteDirectory + prefixOfPath + "\\")
                                    .execute();

                            updateFile(fileRQ);
                        }
                );
        return ResponseEntity.ok(new UpdateFilesRSBuilder("success").build());
    }
}