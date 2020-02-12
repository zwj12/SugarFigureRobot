package com.abb.sugarfigurerobot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

public class DouglasPeukcer {

    /**
     * @param points
     * @param firstPoint
     * @param lastPoint
     * @param tolerance
     * @param pointIndexsToKeep,
     *            exclusive the fist and last point
     */
    private static void DouglasPeuckerReduction(ArrayList<? extends Point> points, int firstPoint, int lastPoint,
                                                double tolerance, ArrayList<Integer> pointIndexsToKeep) {
        double maxDistance = 0;
        int indexFarthest = 0;

        for (int index = firstPoint; index < lastPoint; index++) {
            double deviation = Point.perpendicularDistance(points.get(index), points.get(firstPoint),
                    points.get(lastPoint));
            if (deviation > maxDistance) {
                maxDistance = deviation;
                indexFarthest = index;
            }
        }

        if (maxDistance > tolerance && indexFarthest > 0) {
            pointIndexsToKeep.add(indexFarthest);
            DouglasPeuckerReduction(points, firstPoint, indexFarthest, tolerance, pointIndexsToKeep);
            DouglasPeuckerReduction(points, indexFarthest, lastPoint, tolerance, pointIndexsToKeep);
        }
    }

    public static ArrayList<? extends Point> DouglasPeukcerLine(ArrayList<? extends Point> points, double tolerance) {
        if (points == null || points.size() < 3) {
            return points;
        }
        int firstPoint = 0;
        int lastPoint = points.size() - 1;
        ArrayList<Integer> pointIndexsToKeep = new ArrayList<Integer>();
        pointIndexsToKeep.add(firstPoint);
        pointIndexsToKeep.add(lastPoint);
        while (((Point) (points.get(firstPoint))).equalsOnlyXYX(points.get(lastPoint))) {
            lastPoint--;
            if(lastPoint==-1)
            {
                //if you want to block the single point, comment the below three lines
                //ArrayList<Point> returnPoints = new ArrayList<Point>();
                //returnPoints.add(points.get(0));
                //returnPoints.add(points.get(points.size() - 1));
                return null;
            }
        }
        DouglasPeuckerReduction(points, firstPoint, lastPoint, tolerance, pointIndexsToKeep);
        Collections.sort(pointIndexsToKeep);

        // Comparator<Integer> c = new Comparator<Integer>() {
        // @Override
        // public int compare(Integer o1, Integer o2) {
        // if ((int) o1 < (int) o2)
        // return 1;
        // else
        // return -1;
        // }
        // };
        // pointIndexsToKeep.sort(c);

        ArrayList<Point> returnPoints = new ArrayList<Point>();
        for (Integer index : pointIndexsToKeep) {
            returnPoints.add(points.get(index));
        }
        return returnPoints;
    }

}
