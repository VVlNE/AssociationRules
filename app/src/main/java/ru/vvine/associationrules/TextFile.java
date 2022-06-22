package ru.vvine.associationrules;

import java.io.Serializable;

public class TextFile implements Serializable {
    private String data;
    private String name;

    public TextFile(String data, String name) {
        this.data = fixData(data);
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    private String fixData(String data) {
        while (data.toCharArray()[data.toCharArray().length - 1] == '\n') {
            data = data.substring(0, data.toCharArray().length - 1);
        }
        return data;
    }

    //дописать проверку
    public boolean isRightFile() {
        return true;
    }
}