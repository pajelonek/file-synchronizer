package com.licencjat.filesynchronizer.model.updatefiles;

public class UpdateFilesRSBuilder {

    private String status;

    public UpdateFilesRSBuilder(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UpdateFilesRS build() {
        UpdateFilesRS updateFilesRS = new UpdateFilesRS();
        updateFilesRS.setStatus(this.status);
        return updateFilesRS;
    }
}