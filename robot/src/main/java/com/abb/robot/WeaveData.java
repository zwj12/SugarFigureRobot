package com.abb.robot;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

public class WeaveData implements Serializable {

    private int index;

    private int weaveShape;

    private int weaveType;

    private double weaveLength;

    private double weaveWidth;

    private double weaveHeight;

    private double dwellLeft;

    private double dwellCenter;

    private double dwellRight;

    private double weaveDir;

    private double weaveTilt;

    private double weaveOri;

    private double weaveBias;

    private double orgWeaveWidth;

    private double orgWeaveHeight;

    private double orgWeaveBias;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getWeaveShape() {
        return weaveShape;
    }

    public void setWeaveShape(int weaveShape) {
        this.weaveShape = weaveShape;
    }

    public int getWeaveType() {
        return weaveType;
    }

    public void setWeaveType(int weaveType) {
        this.weaveType = weaveType;
    }

    public double getWeaveLength() {
        return weaveLength;
    }

    public void setWeaveLength(double weaveLength) {
        this.weaveLength = weaveLength;
    }

    public double getWeaveWidth() {
        return weaveWidth;
    }

    public void setWeaveWidth(double weaveWidth) {
        this.weaveWidth = weaveWidth;
    }

    public double getWeaveHeight() {
        return weaveHeight;
    }

    public void setWeaveHeight(double weaveHeight) {
        this.weaveHeight = weaveHeight;
    }

    public double getDwellLeft() {
        return dwellLeft;
    }

    public void setDwellLeft(double dwellLeft) {
        this.dwellLeft = dwellLeft;
    }

    public double getDwellCenter() {
        return dwellCenter;
    }

    public void setDwellCenter(double dwellCenter) {
        this.dwellCenter = dwellCenter;
    }

    public double getDwellRight() {
        return dwellRight;
    }

    public void setDwellRight(double dwellRight) {
        this.dwellRight = dwellRight;
    }

    public double getWeaveDir() {
        return weaveDir;
    }

    public void setWeaveDir(double weaveDir) {
        this.weaveDir = weaveDir;
    }

    public double getWeaveTilt() {
        return weaveTilt;
    }

    public void setWeaveTilt(double weaveTilt) {
        this.weaveTilt = weaveTilt;
    }

    public double getWeaveOri() {
        return weaveOri;
    }

    public void setWeaveOri(double weaveOri) {
        this.weaveOri = weaveOri;
    }

    public double getWeaveBias() {
        return weaveBias;
    }

    public void setWeaveBias(double weaveBias) {
        this.weaveBias = weaveBias;
    }

    public double getOrgWeaveWidth() {
        return orgWeaveWidth;
    }

    public void setOrgWeaveWidth(double orgWeaveWidth) {
        this.orgWeaveWidth = orgWeaveWidth;
    }

    public double getOrgWeaveHeight() {
        return orgWeaveHeight;
    }

    public void setOrgWeaveHeight(double orgWeaveHeight) {
        this.orgWeaveHeight = orgWeaveHeight;
    }

    public double getOrgWeaveBias() {
        return orgWeaveBias;
    }

    public void setOrgWeaveBias(double orgWeaveBias) {
        this.orgWeaveBias = orgWeaveBias;
    }

    public WeaveData() {
    }

    @NonNull
    @Override
    public String toString() {
        Locale l = Locale.ENGLISH;
        DecimalFormat df = new DecimalFormat("#.#");
//        return String.format(l, "[%d,%d,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f]"
//                , this.weaveShape, this.weaveType, this.weaveLength, this.weaveWidth, this.weaveHeight, this.dwellLeft, this.dwellCenter, this.dwellRight
//                , this.weaveDir, this.weaveTilt, this.weaveOri, this.weaveBias, this.orgWeaveWidth, this.orgWeaveHeight, this.orgWeaveBias);
        return String.format(l, "[%d,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s]"
                , this.weaveShape, this.weaveType, df.format(this.weaveLength),
                df.format(this.weaveWidth), df.format(this.weaveHeight), df.format(this.dwellLeft),
                df.format(this.dwellCenter), df.format(this.dwellRight),
                df.format(this.weaveDir), df.format(this.weaveTilt), df.format(this.weaveOri),
                df.format(this.weaveBias), df.format(this.orgWeaveWidth), df.format(this.orgWeaveHeight),
                df.format(this.orgWeaveBias));
    }

    public void parse(String strWeaveData) {
        int numStartIndex = 0;
        int numStopIndex = strWeaveData.indexOf("[");

        // console.log(strWeaveData);

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveShape = Integer.parseInt(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveType = Integer.parseInt(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveLength = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveWidth = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveHeight = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.dwellLeft = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.dwellCenter = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.dwellRight = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveDir = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveTilt = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveOri = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.weaveBias = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.orgWeaveWidth = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf(",", numStartIndex);
        this.orgWeaveHeight = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeaveData.indexOf("]", numStartIndex);
        this.orgWeaveBias = Float.parseFloat(strWeaveData.substring(numStartIndex, numStopIndex));
    }
}
