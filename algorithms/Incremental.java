/*
  Incremental Algorithm
       Time Complexity: O (n log n)
 */
package algorithms;

import setup.HeapSort;
import setup.Point;
import setup.PointCircular;
import setup.PointStack;
import userinterface.AnimationArea;

public class Incremental extends ConvexHullAlgorithm{

    /**
     * Sort the points by X order
     * Form a small convex from the first two points
     * Look at the next point and merge the convex to it
     * Repeatedly doing so for all the points
     */

    private final Point[] points;
    private final PointCircular convex;
    private boolean isComplete;
    private final int size;

    private PointCircular rightMostPC;
    private boolean isStart;
    private int index, oldX;

    public Incremental(Point[] points)
    {
        this.points = points;
        isComplete = false;
        size = points.length;
        isStart = false;

        HeapSort.sort(points, Point.BYXORDER);
        convex = new PointCircular(points[0]);
    }

    /**
     * Draws the points and lines to the canvas
     *
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point pt: points){
            pt.setRed(false);
            animationArea.drawPoint(pt);
        }
        convex.draw(animationArea);

        if (!isStart) firstStep();
        else if (index < size) nextStep();
        else finalStep();
    }

    /**
     * Kicks off the algorithm by forming the small convex and initialising the appropriate variables
     */
    private void firstStep() {
        isStart = true;
        int secondInd = 1;
        while (secondInd + 1 < size &&
                points[secondInd+1].getX() == points[0].getX())
            secondInd++;
        findSmallConvex(secondInd);

        index = secondInd +1;
        oldX = points[secondInd].getX();
    }

    /**
     * Represents the loop of the direct implementation,
     * merge points to the already formed convex
     */
    private void nextStep() {
        while(index+1 < size && points[index+1].getX() == oldX)
            index++;
        oldX = points[index].getX();
        merge(new PointCircular(points[index]));
        index++;

    }

    /**
     * Signal that the algorithm has been completely executed
     */
    private void finalStep() {
        isComplete = true;
    }

    /**
     * whether the convex hull is complete
     *
     * @return true if it is completed, otherwise false
     */
    @Override
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * @return the points that forms the convex hull
     */
    @Override
    public Point[] getResult() {
        return directImp();
    }

    /**
     * The direct implementation of the Incremental algorithm
     * Also serves as the rough draft for the draw feature
     * @return the points that forms the convex hull
     */
    private Point[] directImp() {
        // the sorting step is already done in the constructor to enable the draw feature to work
        int secondInd = 1;
        while (secondInd + 1 < size &&
                points[secondInd+1].getX() == points[0].getX())
            secondInd++;
        findSmallConvex(secondInd);

        int prevX = points[secondInd].getX();
        for (int i = secondInd+1; i < size; i++)
        {
            while (i + 1 < size && points[i+1].getX()==prevX)
                i++;
            prevX = points[i].getX();

            merge(new PointCircular(points[i]));
        }

        PointStack result = new PointStack();
        for (PointCircular temp: convex)
        {
            result.push(temp.getPoint());
        }
        return result.getConvex();
    }

    /**
     * Forms a convex with two points
     * @param secondInd the index of the point to add
     */
    private void findSmallConvex(int secondInd) {
        PointCircular secondPC = new PointCircular(points[secondInd]);
        rightMostPC = secondPC;
        convex.setCW(secondPC);
        secondPC.setCW(convex);
    }

    /**
     * Merge the small convex to the point given
     * @param mergePC the point circular that contains the point to connect to
     */
    private void merge(PointCircular mergePC) {
        PointCircular lowerRightmost = rightMostPC;
        PointCircular upperRightmost = rightMostPC;
        int dir;

        // lower tangent line
        PointCircular oldLower;
        do {
            oldLower = lowerRightmost;

            //rightmost has to move in a clockwise direction (left turn)
            dir = Point.direction(mergePC.getPoint(), lowerRightmost.getPoint(), lowerRightmost.getCW().getPoint());
            while (dir >= 0) {
                if (dir == 0) {
                    int distOrig = Point.distance(mergePC.getPoint(), lowerRightmost.getPoint());
                    int distNew = Point.distance(mergePC.getPoint(), lowerRightmost.getCW().getPoint());
                    // if same y(horizontal line) or same slope , I need the farthest
                    if (distOrig < distNew)
                        lowerRightmost = lowerRightmost.getCW();
                    else break;
                }
                else lowerRightmost = lowerRightmost.getCW();
                dir = Point.direction(mergePC.getPoint(), lowerRightmost.getPoint(), lowerRightmost.getCW().getPoint());
            }
        } while (!oldLower.equals(lowerRightmost));

        // upper tangent line
        PointCircular oldUpper;
        do {
            oldUpper = upperRightmost;

            // rightmost has to move in a counterclockwise direction (right turn)
            dir = Point.direction(mergePC.getPoint(), upperRightmost.getPoint(), upperRightmost.getCounterCW().getPoint());
            while (dir <= 0) {
                if (dir == 0) {
                    int distOrig = Point.distance(mergePC.getPoint(), upperRightmost.getPoint());
                    int distNew = Point.distance(mergePC.getPoint(), upperRightmost.getCounterCW().getPoint());
                        // if same y(horizontal line) or same slope , I need the farthest
                    if (distOrig < distNew)
                        upperRightmost = upperRightmost.getCounterCW();
                    else break;
                }
                else upperRightmost = upperRightmost.getCounterCW();
                dir = Point.direction(mergePC.getPoint(), upperRightmost.getPoint(), upperRightmost.getCounterCW().getPoint());
            }
        } while (!oldUpper.equals(upperRightmost));

        upperRightmost.setCW(mergePC);
        mergePC.setCW(lowerRightmost);
        rightMostPC = mergePC;
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 750;
    }
}
