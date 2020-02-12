package com.abb.sugarfigurerobot;

import java.util.regex.Pattern;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

public class GPath extends GTarget implements Cloneable {
    private GPath gPathPre = null;
    private double eCur;
    private double length;
    private double eUnit;
    private GPath gPathNext = null;
    private GType gType = GType.G0;

    public GPath getgPathPre() {
        return gPathPre;
    }

    public double geteCur() {
        return eCur;
    }

    public double getLength() {
        return length;
    }

    public double geteUnit() {
        return eUnit;
    }

    public GType getgType() {
        return gType;
    }

    public void setgType(GType gType) {
        this.gType = gType;
    }

    public GPath getgPathNext() {
        return gPathNext;
    }

    public GPath(GPath gPathPre, double x, double y, double z, double e, GType gType) {
        super(x, y, z, e);
        this.gType = gType;
        this.gPathPre = gPathPre;
        if (this.gPathPre != null) {
            this.gPathPre.gPathNext = this;
        }
        this.initPath();
    }

    @Override
    public void setE(double e) {
        // TODO Auto-generated method stub
        super.setE(e);
        initPath();
    }

    @Override
    public void setX(double x) {
        // TODO Auto-generated method stub
        super.setX(x);
        initPath();
    }

    @Override
    public void setY(double y) {
        // TODO Auto-generated method stub
        super.setY(y);
        initPath();
    }

    @Override
    public void setZ(double z) {
        // TODO Auto-generated method stub
        super.setZ(z);
        initPath();
    }

    @Override
    public Object clone() {
        // TODO Auto-generated method stub
        return super.clone();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        // return super.toString();
        return String.format("%s\t%8.1f\t%8.1f\t%8.1f\t%f\t%f\t%8.1f\t%8.3f", gType, x, y, z, e, eCur, length, eUnit);
    }

    public String toEngraveString(Point pointOffset) {
        int engravingType = 0;
        if (this.gPathNext == null) {
            if (this.gType == GType.G1) {
                engravingType = 3;
            } else {
                engravingType = 0;
            }
        } else if (this.gType == GType.G0 && this.gPathNext.gType == GType.G1) {
            engravingType = 1;
        } else if (this.gType == GType.G1 && this.gPathNext.gType == GType.G1) {
            engravingType = 2;
        } else if (this.gType == GType.G1 && this.gPathNext.gType == GType.G0) {
            engravingType = 3;
        } else {
            engravingType = 0;
        }
        return String.format("%.1f\t%.1f\t%.1f\t%d", x + pointOffset.x, y + pointOffset.y, z + pointOffset.z,
                engravingType);
    }

    public static String getHeader() {
        return "\tlayer\t\tindex\tgType\t\tx\t\ty\t\t\t\tz\t\t\te\t\teCur\t\tlength\t\teUnit";
    }

    private void initPath() {
        if (this.gPathPre != null) {
            this.eCur = this.e - this.gPathPre.e;
            this.length = Point.distance(gPathPre, this);
            if (this.eCur == 0) {
                this.eUnit = 0;
            } else {
                this.eUnit = this.eCur / this.length;
            }
        }
    }

}
