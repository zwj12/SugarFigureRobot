package com.abb.robot;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

public class WeldData extends BaseObservable
        implements Serializable {
    private final  String strTaskName = "T_ROB1";
    private final  String strDataModuleName = "JQR365WeldDataModule";
    private final  String strDataName = "weld";
    private final  String strDataType = "welddata";

    public String getStrTaskName() {
        return strTaskName;
    }

    public String getStrDataModuleName() {
        return strDataModuleName;
    }

    public String getStrDataName() {
        return strDataName;
    }

    public String getStrDataType() {
        return strDataType;
    }

    private int index;

    private double weldSpeed;

    private double orgWeldSpeed;

    private ArcData mainArc=new ArcData();

    private ArcData orgArc = new ArcData();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Bindable
    public double getWeldSpeed() {
        return weldSpeed;
    }

    public void setWeldSpeed(double weldSpeed) {
        this.weldSpeed = weldSpeed;
        notifyPropertyChanged(BR.weldSpeed);
    }

    public double getOrgWeldSpeed() {
        return orgWeldSpeed;
    }

    public void setOrgWeldSpeed(double orgWeldSpeed) {
        this.orgWeldSpeed = orgWeldSpeed;
    }

    public ArcData getMainArc() {
        return mainArc;
    }

    public void setMainArc(ArcData mainArc) {
        this.mainArc = mainArc;
    }

    public ArcData getOrgArc() {
        return orgArc;
    }

    public void setOrgArc(ArcData orgArc) {
        this.orgArc = orgArc;
    }

    public WeldData() {
    }

    @NonNull
    @Override
    public String toString() {
        Locale l = Locale.ENGLISH;
        DecimalFormat df = new DecimalFormat("#.#");
//        return String.format(l,"[%.1f,%.1f,%s,%s]"
//                , this.weldSpeed, this.orgWeldSpeed, this.mainArc, this.orgArc);
        return String.format(l,"[%s,%s,%s,%s]"
                ,df.format( this.weldSpeed), df.format(this.orgWeldSpeed), this.mainArc, this.orgArc);

    }

    /**
     *
     * @param strWeldData
     * [3,0,[4,0,-5,0,0,230,0,0,0],[0,0,0,0,0,0,0,0,0]]
     */
    public void parse(String strWeldData) {
        int numStartIndex = 0;
        int numStopIndex = strWeldData.indexOf("[");

        // console.log(strWeldData);

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeldData.indexOf(",", numStartIndex);
//        this.weldSpeed = Float.parseFloat(strWeldData.substring(numStartIndex, numStopIndex));
        this.setWeldSpeed(Float.parseFloat(strWeldData.substring(numStartIndex, numStopIndex)));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeldData.indexOf(",", numStartIndex);
        this.orgWeldSpeed = Float.parseFloat(strWeldData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeldData.indexOf("]", numStartIndex);
        numStopIndex =numStopIndex +1;
        this.mainArc.parse(strWeldData.substring(numStartIndex, numStopIndex));

        numStartIndex = numStopIndex + 1;
        numStopIndex = strWeldData.indexOf("]", numStartIndex);
        numStopIndex =numStopIndex +1;
        this.orgArc.parse(strWeldData.substring(numStartIndex, numStopIndex));
    }


}
