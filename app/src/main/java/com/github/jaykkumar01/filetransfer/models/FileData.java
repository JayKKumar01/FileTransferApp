package com.github.jaykkumar01.filetransfer.models;

public class FileData {
    int id;
    long index;
    byte[] bytes;
    long totalSize;
    String name;
    long curSize;

    public FileData(int id, long index, byte[] bytes, long totalSize) {
        this.id = id;
        this.index = index;
        this.bytes = bytes;
        this.totalSize = totalSize;
    }

    public long getCurSize() {
        return curSize;
    }

    public void setCurSize(long curSize) {
        this.curSize = curSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public long getIndex() {
        return index;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getTotalSize() {
        return totalSize;
    }
}
