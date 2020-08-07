/*
  Graham Scan algorithm
       Time Complexity: O(n log n)
 */
package algorithms;

import setup.HeapSort;
import setup.Point;
import setup.PointStack;
import userinterface.AnimationArea;

import java.util.HashSet;

public class GrahamScan extends ConvexHullAlgorithm{
    /**
     * Find the lowest point
     * Sort the other points based on the angle it makes with the lowest point
     * Push the first and second point into the stack
     * Then check if the third point makes a counterclockwise turn from the second point
     * while it does not, the second point gets pop out
     * Push the third point into the stack
     * repeat steps 4,5 and 6 to get a convex hull
     */

    private final Point[] points;
    private final PointStack convex;
    private boolean isComplete;
    private final int size;

    private int tested; // the number of points being looked at
    private Point lowestPt;

    public GrahamScan(Point[] points)
    {
        this.points = points;
        isComplete = false;
        size = points.length;

        tested = 0;
        convex = new PointStack();
        for (Point pt: points)
            updateLowest(pt);

        HeapSort.sort(points, lowestPt.BYSLOPE); // Use slope instead of angle because calculating angle is too expensive
        convex.push(lowestPt);
        tested++;
    }

    /**
     * Draws the points and lines to the canvas
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point point:points)
            animationArea.drawPoint(point);
        convex.draw(animationArea);
        for (int i=tested; i < size; i++)
            animationArea.drawLine(animationArea.LIGHT_GRAY, lowestPt, points[i]);
        if (tested < size)
        {
            animationArea.drawLine(animationArea.RED, points[tested], convex.lastPt());
        }
        if (tested == size) finalStep(animationArea);
        else nextStep();
    }

    /**
     * Checks whether the pt is lower than the lowestPt in terms of y
     * If y is equal, choose the lower x
     * @param pt the point lowestPt compare to
     */
    private void updateLowest(Point pt) {
        if (lowestPt == null || pt.compareTo(lowestPt) < 0)
            lowestPt = pt;
    }

    /**
     * Runs the next step that would help solve the convex hull
     */
    private void nextStep() {
        if (convex.size() == 1)
        {
            convex.push(points[tested]);
            tested++;
            return;
        }

        Point[] recentlyAdded = convex.lastTwo();
        int dir = Point.direction(recentlyAdded[1], recentlyAdded[0], points[tested]);
        if (dir <= 0) { // needs to keep popping the points until the dir is not 0
            convex.pop();
        }
        else {
            convex.push(points[tested]);
            tested++;
        }
    }

    /**
     * connects the first point to the last point and update is_complete
     * @param animationArea the canvas to draw on
     */
    private void finalStep(AnimationArea animationArea) {
        isComplete = true;
        Point lastPt = convex.lastPt();
        animationArea.drawLine(animationArea.BLACK, lowestPt, lastPt);
    }

    /**
     * whether the convex hull is complete
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
     * The direct implementation of the Graham Scan algorithm
     * Also serves as the rough draft for the draw feature
     * @return the points that forms the convex hull
     */
    private Point[] directImp() {
        // The following commented steps are already done in the constructor to enable the draw method to work
        // Find the lowest point of the points given
        // Sort the points according to the angle it makes with the lowest point
        // Push the first point to the stack

        convex.push(points[tested]); // push the second point to the stack
        tested++;
        int dir;
        while (tested < size) {
            Point[] recentlyAdded = convex.lastTwo();
            dir = Point.direction(recentlyAdded[1], recentlyAdded[0], points[tested]);
            while (dir <= 0) {
                convex.pop();
                if (convex.size() == 1) break;
                recentlyAdded = convex.lastTwo();
                dir = Point.direction(recentlyAdded[1], recentlyAdded[0], points[tested]);
            }
            convex.push(points[tested]);
            tested++;
        }
        return convex.getConvex();
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 500;
    }
}
