/*
  Monotone Chain Algorithm
       Time Complexity: O(n log n)
 */
package algorithms;

import setup.HeapSort;
import setup.Point;
import setup.PointStack;
import userinterface.AnimationArea;

public class MonotoneChain extends ConvexHullAlgorithm{

    /**
     * Sort the list of points by X order (if tie, sort by Y)
     * Add the first point to the solution
     * Check if the second point and third point makes a left turn
     * while it doesn't pop out the second point
     * Loop through the array using steps 3 and 4 to build the lower hull
     * Loop through the array in reverse order using steps 3 and 4 to build upper hull
     */

    private final Point[] points;
    private final PointStack convex;
    private boolean isComplete;
    private final int size;

    private boolean isStart;
    private boolean isLowerDone, isUpperDone;
    private Point firstPt;
    private int prevX, index, lowerSizeDraw;

    public MonotoneChain(Point[] points)
    {
        this.points = points;
        isComplete = false;
        size = points.length;

        convex = new PointStack();
        isStart = false;
        isLowerDone = false;
        isUpperDone = false;

    }

    /**
     * Draws the points and lines to the canvas
     *
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        convex.draw(animationArea);
        for (Point pt: points)
            animationArea.drawPoint(pt);
        if (convex.size() > 0 && index > 0)
            animationArea.drawLine(animationArea.RED, points[index], convex.lastPt());

        if (!isStart) firstStep();
        else if (isUpperDone) finalStep(animationArea);
        else if (isLowerDone) upperHull();
        else lowerHull();
    }

    /**
     * Kicks off the draw method by sorting the points and initialing the appropriate variables
     */
    private void firstStep() {
        isStart = true;
        HeapSort.sort(points, Point.BYXORDER);
        index = 0;
        firstPt = points[0];
        prevX = points[index].getX();
    }

    /**
     * Builds the lower hull part of the convex
     * Represents the first loop of the implementation
     */
    private void lowerHull() {
        if (convex.size() > 1 && Point.direction(convex.lastTwo()[1], convex.lastPt(), points[index]) <= 0)
        {
            convex.pop();
        }
        else
        {
            convex.push(points[index]);
            index++;
            while(index + 1 < size && points[index].getX() == prevX)
                index++;
            if (index!= size)
                prevX = points[index].getX();
        }

        if (index == size) {
            isLowerDone = true;
            lowerSizeDraw = convex.size();
            index = size -2;
            prevX = points[index].getX();
        }
    }

    /**
     * Builds the upper hull part of the convex.
     * Represents the second loop of the implementation
     */
    private void upperHull() {
        if (convex.size() > lowerSizeDraw && Point.direction(convex.lastTwo()[1], convex.lastPt(), points[index]) <= 0)
        {
            convex.pop();
        }
        else
        {
            convex.push(points[index]);
            index--;
            while (index - 1 > 0 && points[index].getX() == prevX)
                index--;
            if (index >= 0)
                prevX = points[index].getX();
        }

        if (index < 0) {
            isUpperDone = true;
            convex.pop();
        }
    }

    /**
     * Signal that this algorithm has been completed
     * connects the first point to the last point found
     * @param animationArea the canvas to draw on
     */
    private void finalStep(AnimationArea animationArea) {
        isComplete = true;
        animationArea.drawLine(animationArea.BLACK, firstPt, convex.lastPt());
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
     * The direct implementation of the Monotone Chain algorithm
     * Also serves as the rough draft for the draw feature
     * @return the points that forms the convex hull
     */
    private Point[] directImp() {
        HeapSort.sort(points, Point.BYXORDER);

        int prevX = -1;
        // lower hull
        for (int i = 0; i < size; i++)
        {
            while(i+1< size && points[i].getX() == prevX) i++; // ignore the points in middle of a vertical collinear line
            prevX = points[i].getX();

            while (convex.size() > 1 && Point.direction(convex.lastTwo()[1], convex.lastPt(), points[i]) <= 0)
            {
                convex.pop();
            }
            convex.push(points[i]);
        }

        int lowerSize = convex.size();
        prevX = -1;
        // upper hull
        for (int i = size-2; i >= 0; i--)
        {
            while (i-1 > 0 && points[i].getX() == prevX) i--; // ignore the points in middle of a vertical collinear line
            prevX = points[i].getX();

            while (convex.size() > lowerSize && Point.direction(convex.lastTwo()[1], convex.lastPt(), points[i]) <= 0)
            {
                convex.pop();
            }
            convex.push(points[i]);
        }
        convex.pop(); // last point is the same as the first point
        return convex.getConvex();
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 650;
    }
}
