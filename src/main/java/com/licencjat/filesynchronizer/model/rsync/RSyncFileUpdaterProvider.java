package com.licencjat.filesynchronizer.model.rsync;

import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RSyncFileUpdaterProvider {

    @Autowired
    RSyncFileUpdaterExecutor rSyncFileUpdaterExecutor;

    List<String> testSources = new ArrayList<>();
    List<String> testDestination = new ArrayList<>();


    public void process(List<FileRQList> fileRQArrayList) {
        testSources.add("C:/Users/SG0306258/OneDrive - Sabre/Desktop/testlicencjat/test1.txt");
        testSources.add("C:/Users/SG0306258/OneDrive - Sabre/Desktop/testlicencjat/test2.txt");

        testDestination.add(fileRQArrayList.get(0).getFilePath().substring(0,58));
        if (validate(fileRQArrayList)) {
            rSyncFileUpdaterExecutor
                    .setSource(testSources)
                    .setDestinations(testDestination)
//                    .setFileList("FileList")
                    .execute(fileRQArrayList);
        }
    }

    private boolean validate(List<FileRQList> fileRQArrayList) {
        return true;
    }
}
