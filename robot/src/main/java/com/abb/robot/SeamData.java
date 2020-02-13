package com.abb.robot;

import android.util.Log;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

public class SeamData implements Serializable {
    private static final String TAG = "SeamData";

    private final String strTaskName = "T_ROB1";
    private final String strDataModuleName = "JQR365WeldDataModule";
    private final String strDataName = "seam";
    private final String strDataType = "seamdata";

    private int index;

    private double purgeTime;

    private double preflowTime;

    private ArcData ignArc = new ArcData();

    private double ignMoveDelay;

    private int scrapeStart;

    private double heatSpeed;

    private double heatTime;

    private double heatDistance;

    private ArcData heatArc = new ArcData();

    private double coolTime;

    private double fillTime;

    private ArcData fillArc = new ArcData();

    private double bbackTime;

    private double rbackTime;

    private ArcData bbackArc = new ArcData();

    private double postflowTime;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getPurgeTime() {
        return purgeTime;
    }

    public void setPurgeTime(double purgeTime) {
        this.purgeTime = purgeTime;
    }

    public double getPreflowTime() {
        return preflowTime;
    }

    public void setPreflowTime(double preflowTime) {
        this.preflowTime = preflowTime;
    }

    public ArcData getIgnArc() {
        return ignArc;
    }

    public void setIgnArc(ArcData ignArc) {
        this.ignArc = ignArc;
    }

    public double getIgnMoveDelay() {
        return ignMoveDelay;
    }

    public void setIgnMoveDelay(double ignMoveDelay) {
        this.ignMoveDelay = ignMoveDelay;
    }

    public int getScrapeStart() {
        return scrapeStart;
    }

    public void setScrapeStart(int scrapeStart) {
        this.scrapeStart = scrapeStart;
    }

    public double getHeatSpeed() {
        return heatSpeed;
    }

    public void setHeatSpeed(double heatSpeed) {
        this.heatSpeed = heatSpeed;
    }

    public double getHeatTime() {
        return heatTime;
    }

    public void setHeatTime(double heatTime) {
        this.heatTime = heatTime;
    }

    public double getHeatDistance() {
        return heatDistance;
    }

    public void setHeatDistance(double heatDistance) {
        this.heatDistance = heatDistance;
    }

    public ArcData getHeatArc() {
        return heatArc;
    }

    public void setHeatArc(ArcData heatArc) {
        this.heatArc = heatArc;
    }

    public double getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(double coolTime) {
        this.coolTime = coolTime;
    }

    public double getFillTime() {
        return fillTime;
    }

    public void setFillTime(double fillTime) {
        this.fillTime = fillTime;
    }

    public ArcData getFillArc() {
        return fillArc;
    }

    public void setFillArc(ArcData fillArc) {
        this.fillArc = fillArc;
    }

    public double getBbackTime() {
        return bbackTime;
    }

    public void setBbackTime(double bbackTime) {
        this.bbackTime = bbackTime;
    }

    public double getRbackTime() {
        return rbackTime;
    }

    public void setRbackTime(double rbackTime) {
        this.rbackTime = rbackTime;
    }

    public ArcData getBbackArc() {
        return bbackArc;
    }

    public void setBbackArc(ArcData bbackArc) {
        this.bbackArc = bbackArc;
    }

    public double getPostflowTime() {
        return postflowTime;
    }

    public void setPostflowTime(double postflowTime) {
        this.postflowTime = postflowTime;
    }

    public SeamData() {
    }

    @NonNull
    @Override
    public String toString() {
        Locale l = Locale.ENGLISH;
        DecimalFormat df = new DecimalFormat("#.#");
//        return String.format(l,"[%.1f,%.1f,%s,%.1f,%d,%.1f,%.1f,%.1f,%s,%.1f,%.1f,%s,%.1f,%.1f,%s,%.1f]"
//                , this.purgeTime, this.preflowTime, this.ignArc, this.ignMoveDelay, this.scrapeStart, this.heatSpeed, this.heatTime, this.heatDistance, this.heatArc
//                , this.coolTime, this.fillTime, this.fillArc, this.bbackTime, this.rbackTime, this.bbackArc, this.postflowTime);
        return String.format(l, "[%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s]"
                , df.format(this.purgeTime), df.format(this.preflowTime), this.ignArc, this.ignMoveDelay, this.scrapeStart,
                df.format(this.heatSpeed), df.format(this.heatTime), df.format(this.heatDistance), this.heatArc,
                df.format(this.coolTime), df.format(this.fillTime), this.fillArc,
                df.format(this.bbackTime), df.format(this.rbackTime), this.bbackArc, df.format(this.postflowTime));
    }

    public void parse(String strSeamData) {
        int numStartIndex = 0;
        int numStopIndex = strSeamData.indexOf("[");

//        Log.d(TAG, strSeamData);

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.purgeTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.preflowTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf("]", numStartIndex);
        numStopIndex = numStopIndex + 1;
        this.ignArc.parse(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.ignMoveDelay = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.scrapeStart = Integer.parseInt(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.heatSpeed = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.heatTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.heatDistance = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf("]", numStartIndex);
        numStopIndex = numStopIndex + 1;
        this.heatArc.parse(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.coolTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.fillTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf("]", numStartIndex);
        numStopIndex = numStopIndex + 1;
        this.fillArc.parse(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.bbackTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf(",", numStartIndex);
        this.rbackTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf("]", numStartIndex);
        numStopIndex = numStopIndex + 1;
        this.bbackArc.parse(strSeamData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strSeamData.indexOf("]", numStartIndex);
        this.postflowTime = Float.parseFloat(strSeamData.substring(numStartIndex, numStopIndex));
    }
}
