package com.abb.sugarfigurerobot;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

public enum GType {
    ProcessLStart(1)
    ,ProcessL(2)
    ,ProcessLEnd(3)
    ,ProcessCStart(4)
    ,ProcessC(5)
    ,ProcessCEnd(6)
    ,MoveL(7)
    ,MoveC(8)
    ,MoveJ(9);

    private static final String TAG = "GType";

    private final int code;

    public int getCode() {
        return code;
    }

    GType(int code) {
        this.code = code;
    }
}
