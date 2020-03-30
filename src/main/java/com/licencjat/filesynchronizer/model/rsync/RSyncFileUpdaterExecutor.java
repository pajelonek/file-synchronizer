package com.licencjat.filesynchronizer.model.rsync;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.licencjat.filesynchronizer.model.updatefiles.FileRQList;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RSyncFileUpdaterExecutor {

    List<String> sources;
    String destination;
    RSync rSync = new RSync();

    public void execute(List<FileRQList> fileRQArrayList) {
        rSync.sources(sources)
                .destination(destination)
                .recursive(true)
                .verbose(true);
        try {
            CollectingProcessOutput output = rSync.execute();
            System.out.println(output.getStdOut());
            System.out.println("Exit code: " + output.getExitCode());
            if (output.getExitCode() > 0)
                System.err.println(output.getStdErr());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }


    public RSyncFileUpdaterExecutor setSource(List<String> sources) {
        this.sources = sources;
        return this;
    }


    public RSyncFileUpdaterExecutor setDestinations(String destinations) {
        this.destination = destinations;
        return this;
    }
}
