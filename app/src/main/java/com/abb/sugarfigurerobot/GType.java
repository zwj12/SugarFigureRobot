package com.abb.sugarfigurerobot;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

public enum GType {
    G0(0)
    ,G1(1)
    ,G17(17);

    private static final String TAG = "GType";

    private final int code;

    public int getCode() {
        return code;
    }

    GType(int code) {
        this.code = code;
    }
}
