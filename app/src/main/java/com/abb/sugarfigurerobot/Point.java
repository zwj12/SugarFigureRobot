package com.abb.sugarfigurerobot;

import java.util.Objects;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

public class Point implements Cloneable {

    static Point pointOrigin = new Point(0, 0, 0);

    protected double x;
    protected double y;
    protected double z;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double distance(Point point1, Point point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y)
                + (point1.z - point2.z) * (point1.z - point2.z));
    }

    /**
     * distance of point to [point1,point2]
     *
     * @param point
     * @param point1
     * @param point2
     * @return
     */
    public static double perpendicularDistance(Point point, Point point1, Point point2) {
        double[] a = new double[3], b = new double[3];
        a[0] = point1.x - point.x;
        a[1] = point1.y - point.y;
        a[2] = point1.z - point.z;
        b[0] = point2.x - point.x;
        b[1] = point2.y - point.y;
        b[2] = point2.z - point.z;
        Point crossProductPoint = new Point(0, 0, 0);
        crossProductPoint.x = a[1] * b[2] - a[2] * b[1];
        crossProductPoint.y = a[2] * b[0] - a[0] * b[2];
        crossProductPoint.z = a[0] * b[1] - a[1] * b[0];
        double crossProduct = distance(Point.pointOrigin, crossProductPoint);
        double distance12 = distance(point1, point2);
        return crossProduct / distance12;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return Objects.hash(x, y, z);
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
        Point other = (Point) arg0;
        return Objects.equals(x, other.x) && Objects.equals(y, other.y) && Objects.equals(z, other.z);
    }

    public boolean equalsOnlyXYX(Object arg0) {
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
        Point other = (Point) arg0;
        return Objects.equals(x, other.x) && Objects.equals(y, other.y) && Objects.equals(z, other.z);
    }

    @Override
    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("MyObject can't clone");
        }
        return o;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
//		return super.toString();
        return String.format("%f, %f, %f", x,y,z);
    }

    public static Point getSize(Point a, Point b)
    {
        Point size=new Point(0,0,0);
        size.x=Math.abs(a.x-b.x);
        size.y=Math.abs(a.y-b.y);
        size.z=Math.abs(a.z-b.z);
        return size;
    }

    public Point Add(Point addend) {
        Point result=new Point(0,0,0);
        result.x=this.x+addend.x;
        result.y=this.y+addend.y;
        result.z=this.z+addend.z;
        return result;
    }
}