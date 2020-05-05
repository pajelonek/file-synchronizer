package com.licencjat.filesynchronizer.model.updatefiles;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "mainFolder",
        "fileRQList",
})
public class GetFileListRS {

    @JsonProperty("name")
    private String name;
    @JsonProperty("mainFolder")
    private String mainFolder;
    @JsonProperty("fileRQList")
    private List<FileRQList> fileRQList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("mainFolder")
    public String getMainFolder() {
        return mainFolder;
    }

    @JsonProperty("mainFolder")
    public void setMainFolder(String mainFolder) {
        this.mainFolder = mainFolder;
    }

    @JsonProperty("fileRQList")
    public List<FileRQList> getFileRQList() {
        return fileRQList;
    }

    @JsonProperty("fileRQList")
    public void setFileRQList(List<FileRQList> fileRQList) {
        this.fileRQList = fileRQList;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}