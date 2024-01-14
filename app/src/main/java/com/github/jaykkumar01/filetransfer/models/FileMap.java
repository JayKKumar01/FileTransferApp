package com.github.jaykkumar01.filetransfer.models;

public class FileMap {
    int id;
    String name;
    long totalSize;

    public FileMap(String name, long totalSize) {
        this.name = name;
        this.totalSize = totalSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
