package com.licencjat.filesynchronizer.model.rsync;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.licencjat.filesynchronizer.model.updatefiles.FileRQ;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RSyncFileUpdaterExecutor {

    String source;
    List<String> sources;
    String destination;
    List<String> destinations;
    RSync rSync = new RSync();

    public void execute(ArrayList<FileRQ> fileRQArrayList) {
        rSync.sources(sources)
                .destination(destinations.get(0))
                .recursive(true)
                .verbose(true);
        try {
            CollectingProcessOutput output = rSync.execute();
            System.out.println(output.getStdOut());
            System.out.println("Exit code: " + output.getExitCode());
            if (output.getExitCode() > 0)
                System.err.println(output.getStdErr());
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }


    public RSyncFileUpdaterExecutor setSource(String source) {
        this.source = source;
        return this;
    }

    public RSyncFileUpdaterExecutor setSource(List<String> sources) {
        this.sources = sources;
        return this;
    }

    public RSyncFileUpdaterExecutor setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public RSyncFileUpdaterExecutor setDestinations(List<String> destinations) {
        this.destinations = destinations;
        return this;
    }

    public RSyncFileUpdaterExecutor setFileList(String fileList) {
        return this;
    }
}
