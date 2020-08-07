/*
  Quick Hull Algorithm
       Time Complexity: O (n log n)
 */
package algorithms;

import setup.*;
import userinterface.AnimationArea;

public class QuickHull extends ConvexHullAlgorithm {

    /**
     * Find the min and max x points
     * Connect them together and find the furthest point from the line
     * Connect the furthest point to the extreme points
     * Find the points that are "facing" toward the borders and furthest away from the newly formed line
     * Recursively repeat step 3 and 4
     */

    private static class CallNode { // acts as the parameters of recursive calls
        Point[] nodePoints;
        Line nodeLine;
        boolean nodeSide;

        public CallNode(Point[] nodePoints, Line nodeLine, boolean nodeSide)
        {
            this.nodePoints = nodePoints;
            this.nodeLine = nodeLine;
            this.nodeSide = nodeSide;
        }
    }

    private final Point[] points;
    private final PointStack convex;
    private boolean isComplete;

    private Point minXPt, maxXPt;
    private Point lowestPt; // this point is only needed for the animation
    private final Stack<CallNode> calls;
    private boolean isStart;

    public QuickHull(Point[] points) {
        this.points = points;
        isComplete = false;
        isStart = false;

        for (Point pt : points)
            updateExtremePts(pt);

        calls = new Stack<>();
        convex = new PointStack();
        convex.push(minXPt);
        convex.push(maxXPt);
    }

    /**
     * Finds the points with the maximum x, minimum x, and the lowest point
     * Update the variables
     *
     * @param pt the point to be check against
     */
    private void updateExtremePts(Point pt) {
        if (minXPt == null || pt.getX() < minXPt.getX())
            minXPt = pt;
        else if(pt.getX() == minXPt.getX() && pt.getY() < minXPt.getY())
            minXPt = pt;
        if (maxXPt == null || pt.getX() > maxXPt.getX())
            maxXPt = pt;
        else if (pt.getX() == maxXPt.getX() && pt.getY() > maxXPt.getY())
            maxXPt = pt;
        if (lowestPt == null || pt.getY() < lowestPt.getY())
            lowestPt = pt;
    }

    /**
     * Draws the points and lines to the canvas
     *
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point point: points)
            animationArea.drawPoint(point);
        convex.sortNDraw(animationArea, lowestPt.BYSLOPE);
        // the sorting is needed only for the animation

        if (!isStart) firstStep();
        else if(!calls.isEmpty()) nextStep(calls.pop());
        else finalStep();
    }

    /**
     * Kick starts the algorithm to work
     * Push two calls into the call stack so they could be called upon
     */
    private void firstStep() {
        isStart = true;
        Line line = new Line(minXPt, maxXPt);

        HeapSort.sort(points, line.BYSIDE);
        int center = findCenter(points, points.length/2, line);
        CallNode call1 = new CallNode(copyNMax(points, 0, center + 2, line),
                line, false);
        CallNode call2 = new CallNode(copyNMax(points, center, points.length, line),
                line, true);
        calls.push(call2);
        calls.push(call1);
    }

    /**
     * Finds the furthest point and connect to it
     * Recursively doing so by adding the calls to the stack
     * @param tempCall the parameter of the calls
     */
    private void nextStep(CallNode tempCall) {
        Point[] section = tempCall.nodePoints;
        Line tempLine = tempCall.nodeLine;
        boolean side = tempCall.nodeSide;

        if (section == null) {
            if (calls.isEmpty()) return;
            nextStep(calls.pop());
            return;
        }
        Point furthestPt = section[0];
        if (tempLine.approxLineDist(furthestPt) == 0) { // to deal with collinear problems
            if (calls.isEmpty()) return;
            nextStep(calls.pop());
            return;
        }

        convex.push(furthestPt);
        if (section.length ==3) {
            if (calls.isEmpty()) return;
            // nextStep(calls.pop());
            return;
        }

        Line line1 = new Line(furthestPt, tempLine.getFirstPt());
        HeapSort.sort(section, line1.BYSIDE);
        int center = findCenter(section, section.length/2, line1);
        CallNode call1;
        if (!side)
            call1 = new CallNode(copyNMax(section, 0, center + 2, line1), line1, side);
        else
            call1 = new CallNode(copyNMax(section, center, section.length, line1), line1, side);

        Line line2 = new Line( tempLine.getSecondPt(), furthestPt);
        HeapSort.sort(section, line2.BYSIDE);
        center = findCenter(section, section.length/2, line2);
        CallNode call2;
        if (side)
            call2 = new CallNode(copyNMax(section, center, section.length, line2), line2, side);
        else
            call2 = new CallNode(copyNMax(section, 0, center + 2, line2), line2, side);

        calls.push(call2);
        calls.push(call1);
    }

    /**
     * Copies the array from the start index up to the end index
     * While doing so, it will search for the point with the greatest distance from the line in the range
     * It will set the point with the greatest distance to have the position of 0
     *
     * @param tempPoints the original array to copy
     * @param startInd   the starting index to copy from
     * @param endInd     the ending index to copy up to
     * @param tempLine   the line that we want to determine the distance fromm
     * @return a copy of the array in the specified and the point with the greatest distance from the line at index 0
     */
    private Point[] copyNMax(Point[] tempPoints, int startInd, int endInd, Line tempLine) {
        if (startInd >= tempPoints.length || endInd <= startInd || endInd - startInd == 2) return null;
        Point[] result = new Point[endInd - startInd];
        int furthestInd = -1;
        int furthestDist = 0;
        int x = 0;
        for (int i = startInd; i < endInd && i < tempPoints.length; i++) {
            result[x] = tempPoints[i];
            int tempDist = tempLine.approxLineDist(tempPoints[i]);
            if (furthestInd == -1 || tempDist > furthestDist)
            {
                furthestInd = x;
                furthestDist = tempDist;
            }
            x++;
        }
        exchange(result, furthestInd, 0);
        return result;
    }

    /**
     * exchange the position of the two elements in the array
     *
     * @param tempPoints the array to work on
     * @param firstInd   the index of the first element
     * @param secondInd  the inde of the second element
     */
    private void exchange(Point[] tempPoints, int firstInd, int secondInd) {
        Point tempPt = tempPoints[secondInd];
        tempPoints[secondInd] = tempPoints[firstInd];
        tempPoints[firstInd] = tempPt;
    }

    /**
     * Find the index of the first point of the line
     *
     * @param points the points to look through
     * @param center the index to start with, should start with the size of the points/2
     * @param line   the line to reference by
     * @return the index of the frist point of the line
     */
    private int findCenter(Point[] points, int center, Line line) {
        if (points[center].equals(line.getFirstPt())) return center;
        else if (line.findSide(points[center]) < 0) return findCenter(points, center + 1, line);
        else
            return findCenter(points, center - 1, line); // we are on the right side of the "center" or on the maxXPt
    }

    /**
     * signal the system that the algorithm is completed
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
     * The direct implementation for the QuickHull implementation
     * Also the rough draft for writing the draw feature
     * @return the points that forms the convex in an array
     */
    private Point[] directImp() {
        // the extremes point are searched for in the constructor to enable the draw method to work
        Line line = new Line(minXPt, maxXPt);

        HeapSort.sort(points, line.BYSIDE);
        int center = findCenter(points, points.length/2, line);
        calcHull(copyNMax(points, 0, center +2, line), line, false);
        calcHull(copyNMax(points, center , points.length, line), line, true);

        return convex.getConvex();
    }

    /**
     * Helper function for directImp that calls itself recursively
     * @param section the points to work with
     * @param tempLine the line to work with
     * @param side true if we are working with the right side
     *             false if we are working with the left side
     */
    private void calcHull(Point[] section, Line tempLine, boolean side) {
        if (section == null) return;
        Point furthestPt = section[0];
        if (tempLine.approxLineDist(furthestPt) == 0) return; // it's collinear
        convex.push(furthestPt);

        if (section.length == 3) return;

        Line line1 = new Line(furthestPt, tempLine.getFirstPt());
        HeapSort.sort(section, line1.BYSIDE);
        int center = findCenter(section, section.length/2, line1);

        if (!side)
            calcHull(copyNMax(section, 0, center +2, line1), line1, side);
        else
            calcHull(copyNMax(section, center, section.length, line1), line1, side);

        Line line2 = new Line( tempLine.getSecondPt(), furthestPt);
        HeapSort.sort(section, line2.BYSIDE);
        center = findCenter(section, section.length/2, line2);

        if(side)
            calcHull(copyNMax(section,center , section.length, line2), line2, side);
        else
            calcHull(copyNMax(section,0 , center +2, line2), line2, side);
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 1000;
    }

}
