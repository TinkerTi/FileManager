package com.tinker.filemanager.model;

import java.io.Serializable;

/**
 * Created by tiankui on 16/8/1.
 */
public class FileInfo implements Serializable {
    private static final long serialVersionUID = -4830812821556630987L;

    String fileName;
    String filePath;
    long fileSize;
    boolean isDirectory;
    String suffix;

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public FileInfo() {

    }


}
