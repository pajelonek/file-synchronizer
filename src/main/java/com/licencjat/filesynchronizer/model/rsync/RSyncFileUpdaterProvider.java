package com.licencjat.filesynchronizer.model.rsync;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RSyncFileUpdaterProvider {

    @Autowired
    RSyncFileUpdaterExecutor rSyncFileUpdaterExecutor;

    @Value("${user.local.directory}")
    private String userLocalDirectory;

    public void process(List<FileRQList> fileRQArrayList) {
        List<String> testSources = new ArrayList<>();
        List<String> testDestination = new ArrayList<>();


        testDestination.add(userLocalDirectory);

        fileRQArrayList.stream()
                .map(FileRQList::getFilePath)
                .forEach(testSources::add);

        if (validate(fileRQArrayList)) {
            rSyncFileUpdaterExecutor
                    .setSource(testSources)
                    .setDestinations(testDestination)
                    .execute(fileRQArrayList);
        }
    }

    private boolean validate(List<FileRQList> fileRQArrayList) {
        return true;
    }
}
