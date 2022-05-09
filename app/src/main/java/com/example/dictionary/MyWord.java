package com.example.dictionary;

public class MyWord {
    private int id;
    private String english;
    private String vietnamese;
    private byte[] hinh;

    public MyWord() {
    }

    public MyWord(int id, String english, String vietnamese, byte[] hinh) {
        this.id = id;
        this.english = english;
        this.vietnamese = vietnamese;
        this.hinh = hinh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getVietnamese() {
        return vietnamese;
    }

    public void setVietnamese(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public byte[] getHinh() {
        return hinh;
    }

    public void setHinh(byte[] hinh) {
        this.hinh = hinh;
    }
}
