package com.bernardpletikosa.hc.storage;

import java.util.regex.Pattern;

public class Control {

    public String name;
    public int on;
    public int off;

    public Control(String control) {
        final String[] controlParams = control.split(Pattern.quote("##"));
        this.name = controlParams[0];
        this.on = Integer.parseInt(controlParams[1]);
        this.off = Integer.parseInt(controlParams[2]);
    }

}
