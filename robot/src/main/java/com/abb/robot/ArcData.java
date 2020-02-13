package com.abb.robot;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

public class ArcData implements Serializable {

    private int sched;
    private int mode;
    private double voltage;
    private double wirefeed;
    private double control;
    private double current;
    private double voltage2;
    private double wirefeed2;
    private double control2;

    public int getSched() {
        return sched;
    }

    public void setSched(int sched) {
        this.sched = sched;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getWirefeed() {
        return wirefeed;
    }

    public void setWirefeed(double wirefeed) {
        this.wirefeed = wirefeed;
    }

    public double getControl() {
        return control;
    }

    public void setControl(double control) {
        this.control = control;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getVoltage2() {
        return voltage2;
    }

    public void setVoltage2(double voltage2) {
        this.voltage2 = voltage2;
    }

    public double getWirefeed2() {
        return wirefeed2;
    }

    public void setWirefeed2(double wirefeed2) {
        this.wirefeed2 = wirefeed2;
    }

    public double getControl2() {
        return control2;
    }

    public void setControl2(double control2) {
        this.control2 = control2;
    }

    public ArcData() {
    }

    @NonNull
    @Override
    public String toString() {
        Locale l = Locale.ENGLISH;
        DecimalFormat df = new DecimalFormat("#.#");
//        return String.format(l,"[%d,%d,%.1f,%.1f,%.1f,%.0f,%.1f,%.1f,%.1f]"
//                , this.sched, this.mode, this.voltage, this.wirefeed, this.control, this.current, this.voltage2, this.wirefeed2, this.control2);
        return String.format(l,"[%d,%d,%s,%s,%s,%s,%s,%s,%s]"
                , this.sched, this.mode,df.format( this.voltage),df.format(  this.wirefeed),
                df.format(this.control), df.format( this.current), df.format( this.voltage2),
                df.format( this.wirefeed2), df.format( this.control2));
    }

   void parse(String strArcData) {
        int numStartIndex = 0;
        int numStopIndex = strArcData.indexOf("[");

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.sched = Integer.parseInt(strArcData.substring(numStartIndex, numStopIndex)) ;

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.mode =Integer.parseInt( strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.voltage =Float.parseFloat(strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.wirefeed = Float.parseFloat(strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.control =Float.parseFloat( strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.current = Float.parseFloat(strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.voltage2 = Float.parseFloat(strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf(",", numStartIndex);
        this.wirefeed2 =Float.parseFloat( strArcData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strArcData.indexOf("]", numStartIndex);
        this.control2 =Float.parseFloat( strArcData.substring(numStartIndex, numStopIndex));

    }

}
