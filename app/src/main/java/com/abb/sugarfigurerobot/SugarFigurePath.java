package com.abb.sugarfigurerobot;

import java.util.ArrayList;

/**
 * @author CNMIZHU7
 * @date 2/12/2020
 * description：
 */

public class SugarFigurePath {
    private ArrayList<GPath> listGPath = new ArrayList<>();

    public ArrayList<GPath> getListGPath() {
        return listGPath;
    }

    private ArrayList<? extends Point> listGPathCompressed = null;

    public ArrayList<? extends Point> getListGPathCompressed() {
        return listGPathCompressed;
    }

    private static double distanceTolerance = 10;

    public static Point pointOffset = new Point(725, 10, 200);
    //	public static Point pointOffset = new Point(0,0,0);
    public static Point pointMin = new Point(0, 0, 0);
    public static Point pointMax = new Point(0, 0, 0);

    public static Point pointAllowMin = new Point(700, -15, 200);
    public static Point pointAllowMax = new Point(750, 35, 250);

    public void Clear() {
        listGPath.clear();
        listGPathCompressed = null;
        pointMin.x = Integer.MAX_VALUE;
        pointMin.y = Integer.MAX_VALUE;
        pointMin.z = Integer.MAX_VALUE;
        pointMax.x = Integer.MIN_VALUE;
        pointMax.y = Integer.MIN_VALUE;
        pointMax.z = Integer.MIN_VALUE;
    }

    public void AddGPath(GPath gPath) {
        if (gPath != null) {
            listGPath.add(gPath);
            SetPointEdge(gPath);
        }
    }

    public void SetPointEdge(GPath gPath) {
        pointMin.x = pointMin.x < gPath.x ? pointMin.x : gPath.x;
        pointMin.y = pointMin.y < gPath.y ? pointMin.y : gPath.y;
        pointMin.z = pointMin.z < gPath.z ? pointMin.z : gPath.z;
        pointMax.x = pointMax.x > gPath.x ? pointMax.x : gPath.x;
        pointMax.y = pointMax.y > gPath.y ? pointMax.y : gPath.y;
        pointMax.z = pointMax.z > gPath.z ? pointMax.z : gPath.z;
    }

    public void CompressLayerPath() {
        listGPathCompressed= DouglasPeukcer.DouglasPeukcerLine(listGPath, SugarFigurePath.distanceTolerance);

    }
}
