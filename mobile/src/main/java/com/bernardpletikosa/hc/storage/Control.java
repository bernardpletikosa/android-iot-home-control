package com.bernardpletikosa.hc.storage;

import java.util.regex.Pattern;

public class Control {

    private String name;
    private int on;
    private int off;

    public Control(String control) {
        final String[] controlParams = control.split(Pattern.quote("##"));
        this.name = controlParams[0];
        this.on = Integer.parseInt(controlParams[1]);
        this.off = Integer.parseInt(controlParams[2]);
    }

    public String getName() {
        return name;
    }

    public int getOn() {
        return on;
    }

    public int getOff() {
        return off;
    }
}
