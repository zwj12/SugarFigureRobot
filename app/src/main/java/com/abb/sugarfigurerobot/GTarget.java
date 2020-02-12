package com.abb.sugarfigurerobot;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

import java.util.Objects;

public class GTarget extends Point implements Cloneable {
    protected double e;

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public GTarget(double x, double y, double z, double e) {
        super(x, y, z);
        this.e = e;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return Objects.hash(x, y, z, e);
    }

    @Override
    public boolean equals(Object arg0) {
        // TODO Auto-generated method stub
        if (this == arg0) {
            return true;
        }
        if (arg0 == null) {
            return false;
        }
        if (getClass() != arg0.getClass()) {
            return false;
        }
        GTarget other = (GTarget) arg0;
        return super.equals(other) && Objects.equals(e, other.e);
    }

    @Override
    public Object clone() {
        // TODO Auto-generated method stub
        return super.clone();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
        // return String.format("%8.1f\t%8.1f\t%8.1f\t%f\t%f\t%8.1f\t%8.3f\t%8.3f",
        // x,y,z,e,eCur,length,eUnit,perpendicularDistance);
    }

}
