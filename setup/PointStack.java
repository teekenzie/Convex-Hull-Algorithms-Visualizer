package setup;

import userinterface.AnimationArea;

import java.awt.*;
import java.util.Comparator;

public class PointStack{

    // based on the last in first out principle

    private int size;
    private Point[] points;

    public PointStack()
    {
        size = 0;
        points = new Point[4]; // start with 4 since the minimum number of points is already 3
    }

    /**
     * whether the stack is empty
     * @return true if the stack is empty, otherwise false
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     *
     * @return the size of the stack
     */
    public int size()
    {
        return size;
    }

    /**
     * By doubling the array only when it is full
     *      and cutting it down half when it only fills a quarter
     *      we amortized the cost of copying
     * @param newSize the new size of the array
     * @return a newly created points array with everything copied and the specified space
     */
    private Point[] makeCopy(int newSize)
    {
        Point[] tempPoints = new Point[newSize];
        for (int i = 0; i < size; i++)
            tempPoints[i] = points[i];
        return tempPoints;
    }

    /**
     * Add the new point to the top of the stack
     * @param pt a point
     */
    public void push(Point pt)
    {
        if (size == points.length) points = makeCopy(2*points.length);
        points[size] = pt;
        size++;
        pt.setRed(true); // indiates that the point is now part of the solution
    }

    /**
     *
     * @return the point most recently added
     */
    public Point pop()
    {
        if (size == 0) try {
            throw new NoSuchFieldException("No more elements in the stack");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (size > 0 && size == points.length/4)
            points = makeCopy(points.length/2);
        size--;
        Point tempPt = points[size];
        tempPt.setRed(false); // indiates that the point is no longer part of the solution
        return tempPt;
    }

    /**
     * make the stack empty
     * WARNING: the points that were here are still red
     */
    public void clearStack()
    {
        size = 0;
        points = new Point[4];
    }

    /**
     * Avoid using this, takes n complexity
     *      because have to copy everything to new array
     *      so the returned array size would fit perfectly
     * @return the points that are currently in the stack
     */
    public Point[] getConvex()
    {
        return makeCopy(size);
    }

    /**
     * Draw black points between the result/convex points
     * WARNING: Does not connect the last point to the first point
     * @param animationArea the canvas to draw on
     */
    public void draw(AnimationArea animationArea) {
        if (size <= 1) return;
        if (!points[0].isRed()){
            points[0].setRed(true);
        }
        for (int i = 1; i<size; i++)
        {
            if (!points[i].isRed()) {
                points[i].setRed(true);
            }
            animationArea.drawLine(animationArea.BLACK, points[i-1], points[i]);
        }
    }

    /**
     * Draws a line between the first point and the last point
     * @param animationArea the canvas to draw on
     */
    private void connectFirstLast(AnimationArea animationArea, Color color) {
        animationArea.drawLine(color, points[size()-1], points[0]);
    }

    /**
     * Sort the points before drawing it
     * Will also connect the first point to the last point
     * ALERT: Using this method will break the stack from stack order
     * @param animationArea the canvas to draw on
     * @param comparator the order/way to sort the points
     */
    public void sortNDraw(AnimationArea animationArea, Comparator<Point> comparator) {
        HeapSort.sort(points, comparator, size());
        draw(animationArea);
        connectFirstLast(animationArea, animationArea.BLACK);
    }

    /**
     * Sorts the point by X order before drawing it
     * ALERT: Using this method will break the stack from stack order
     * @param animationArea the canvas to draw on
     */
    public void sortXNDraw(AnimationArea animationArea) {
        HeapSort.sort(points, Point.BYXORDER, size());
        draw(animationArea);
    }

    public void drawSmallConvex(AnimationArea animationArea, Color color) {
        if (size <= 1) return;
        for (int i = 1; i<size; i++)
        {
            animationArea.drawLine(color, points[i-1], points[i]);
        }
        connectFirstLast(animationArea, color);
    }

    /**
     * Provides access to the two most recently added points without removing them
     *      The most recently added element is the first point of the returned arrays
     * Used by QuickHull
     * @return an array that contains the two most recently added element
     */
    public Point[] lastTwo()
    {
        return new Point[]{ points[size-1], points[size-2] };
    }

    /**
     * Provides access to the most recently added point without removing them
     * @return the most recently added point
     */
    public Point lastPt() { return points[size-1]; }

    /**
     * Returns the point that was first inserted into the stack and hasn't been popped out.
     * Doesn't pop out the first point by calling this method
     * @return point that was first inserted, null if it doesn't exist
     */
    public Point firstPt() {
        if(size() > 0)
            return points[0];
        else
            return null;
    }

    /**
     * Returns the array that the stack stores with extra points and the extra space
     * When using this array, be sure not to use only index less than size
     * @return the array that the stack store
     */
    public Point[] getStack() {
        return points;
    }
}
