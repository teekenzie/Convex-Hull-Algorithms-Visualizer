/*
  Divide and Conquer
       Time Complexity: O(n log n)
 */
package algorithms;

import setup.*;
import userinterface.AnimationArea;

public class DivideNConquer extends ConvexHullAlgorithm {

    /**
     * Sort the points by X order (if tie, sort by Y order)
     * Recursively divide the array into halves until there is <= 2 points in the array
     * Recursively merge the convex together
     */

    private static class CallNode { }
    private static class MergeNode extends CallNode{}
    private static class DrawSmallNode extends CallNode {
        int startInd;
        int endInd;
        public DrawSmallNode(int startInd, int endInd) {
            this.startInd = startInd;
            this.endInd = endInd;
        }
    }

    private final Point[] points;
    private final Stack<PointCircular> convex;
    private boolean isComplete;
    private final int size;

    private final Stack<CallNode> calls;
    private boolean isStart;

    public DivideNConquer(Point[] points)
    {
        this.points = points;
        size = points.length;
        isComplete = false;
        isStart = false;

        convex = new Stack<>();
        calls = new Stack<>();
    }

    /**
     * Draws the points and lines to the canvas
     *
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point pt: points)
        {
            pt.setRed(false);
            animationArea.drawPoint(pt);
        }
        for (PointCircular temp: convex)
            temp.draw(animationArea);
        if (!isStart) firstStep();
        else if(!calls.isEmpty()) {
            nextStep();
        }
        else finalStep();
    }

    /**
     * Kicks off the animation by placing calls into the calls stack
     */
    private void firstStep() {
        isStart = true;
        HeapSort.sort(points, Point.BYXORDER);

        calls.push(new MergeNode());
        drawSplit(size/2, size);
        drawSplit(0, size/2);
    }

    /**
     * Recursivly being called upon to place calls into the calls stack
     * @param startInd the starting index of the part of the array to work with
     * @param endInd the ending index (exclusive) of the part of the array to work with
     */
    private void drawSplit(int startInd, int endInd) {
        if (endInd - startInd <=2)
        {
            calls.push(new DrawSmallNode(startInd, endInd));
            return;
        }
        calls.push(new MergeNode());

        int range = endInd - startInd;
        drawSplit(range/2 + startInd, endInd);
        drawSplit(startInd, range/2 + startInd);
    }

    /**
     * Starts popping calls out of the stack and executing them
     */
    private void nextStep() {
        CallNode call = calls.pop();
        if (call instanceof DrawSmallNode) {
            DrawSmallNode drawSmallConvex  = (DrawSmallNode) call;
            int first = drawSmallConvex.startInd;
            int second = drawSmallConvex.endInd;
            findConvex(first, second);
        }
        else if (call instanceof MergeNode) {
            PointCircular second = convex.pop();
            PointCircular first = convex.pop();
            merge(first, second);
        }
    }

    /**
     * Signal that the animation is complete
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
     * The direct immplementation of the Divide and Conquer algorithm
     * Also serves as the rough draft for the draw features
     * @return the points that forms the convex hull
     */
    private Point[] directImp() {
        HeapSort.sort(points, Point.BYXORDER);

        split(0, size/2);
        split(size/2, size);

        PointCircular rightConvex = convex.pop();
        PointCircular leftConvex= convex.pop();
        merge(leftConvex, rightConvex);

        PointStack result = new PointStack();
        for (PointCircular temp : convex.pop())
            result.push(temp.getPoint());
        return result.getConvex();
    }

    /**
     * Split the points into two and then merge them after
     * @param startInd the starting index to access in the array
     * @param endInd the ending index to access in the array
     */
    private void split (int startInd, int endInd) {
        if (endInd - startInd <= 2)
        {
            findConvex(startInd, endInd);
            return;
        }
        int range = endInd-startInd;
        split(startInd, range/2 + startInd);
        split(range/2 + startInd, endInd);

        PointCircular rightConvex = convex.pop();
        PointCircular leftConvex= convex.pop();
        merge(leftConvex, rightConvex);
    }

    /**
     * Forms a convex out of 2 or less points
     * Push the most first point made pointCircular into the stack
     * if there is only one point, it connects to itself
     * if there are two points, they connect to each other
     * Push the drew convex to the convex stack
     * @param startInd the starting index of the range of points
     * @param endInd the ending index of the range of points
     */
    private void findConvex(int startInd, int endInd) {
        int pointer = startInd;
        if (pointer + 2 < size &&
                points[pointer+1].getX()==points[pointer].getX() &&
                points[pointer+2].getX() == points[pointer].getX())
        {
            disableMiddle(pointer);
        }

        PointCircular first = new PointCircular(points[pointer]);
        pointer++;
        if (pointer + 2 < size &&
                points[pointer+1].getX()==points[pointer].getX() &&
                points[pointer+2].getX() == points[pointer].getX())
        {
            disableMiddle(pointer);
        }
        if (pointer >= endInd) {
            if (first.getCollinearDisable()) convex.push(null);
            else convex.push(first);
            return; // only one element, we are done
        }

        PointCircular second = new PointCircular(points[pointer]);
        first.setCW(second);
        second.setCW(first);

        if (first.getCollinearDisable() && second.getCollinearDisable()) convex.push(null);
        else if (first.getCollinearDisable()) convex.push(second);
        else convex.push(first);
    }

    /**
     * Disable all the points that are in the middle of a vertical collinear lines (same X)
     * @param startInd the starting index of the series of points with the same X
     */
    private void disableMiddle(int startInd) {
        int pointer = startInd+1;
        int sameX = points[pointer].getX();
        if (points[pointer].getColliearDisable()) return;
        points[pointer].collinearDisable();
        while(pointer + 2 < size && points[pointer+2].getX() == sameX)
        {
            pointer++;
            points[pointer].collinearDisable();
        }
    }

    /**
     * Merges two convex together
     * find the rightmost point of the left convex, and the rightmost point of the right convex
     * lift the upper tangent all the way to the top
     * drop the lower tangent all the way to the bottom
     * the points in the middle are no longer part of a convex
     * @param leftConvex the convex on the left side
     * @param rightConvex the convex on the right side
     */
    private void merge(PointCircular leftConvex, PointCircular rightConvex) {
        if (leftConvex == null) {
            convex.push(rightConvex);
            return;
        }
        if (rightConvex == null) {
            convex.push(leftConvex);
            return;
        }

        PointCircular rightMost = null;
        for (PointCircular temp : leftConvex) {
            if (rightMost == null || temp.getPoint().getX() > rightMost.getPoint().getX())
                rightMost = temp;
        }
        PointCircular leftMost = null;
        for (PointCircular temp: rightConvex) {
            if(leftMost == null || temp.getPoint().getX() < leftMost.getPoint().getX())
                leftMost = temp;
        }

        PointCircular lowerLeftmost = leftMost;
        PointCircular lowerRightmost = rightMost;
        PointCircular upperLeftmost = leftMost;
        PointCircular upperRightmost = rightMost;
        int dir;

        PointCircular oldLeftmost;
        PointCircular oldRighmost;
        // find the lower tangent line
        do {
            oldLeftmost = lowerLeftmost;
            oldRighmost = lowerRightmost;

            //leftmost has to move in a counterclockwise direction (right turn)
            dir = Point.direction(lowerRightmost.getPoint(), lowerLeftmost.getPoint(), lowerLeftmost.getCounterCW().getPoint());
            while (dir <= 0) {
                if (dir == 0) {
                    int distOrig = Point.distance(lowerRightmost.getPoint(), lowerLeftmost.getPoint());
                    int distNew = Point.distance(lowerRightmost.getPoint(), lowerLeftmost.getCounterCW().getPoint());
                    // if same x (vertical line), I need the closest
                    if (lowerRightmost.getPoint().getX() == lowerLeftmost.getPoint().getX() && distOrig > distNew)
                        lowerLeftmost = lowerLeftmost.getCounterCW();
                        // if same y(horizontal line), I need the farthest
                    else if (lowerRightmost.getPoint().getX() != lowerLeftmost.getPoint().getX() && distOrig < distNew)
                        lowerLeftmost = lowerLeftmost.getCounterCW();
                    else break;
                } else lowerLeftmost = lowerLeftmost.getCounterCW();
                dir = Point.direction(lowerRightmost.getPoint(), lowerLeftmost.getPoint(), lowerLeftmost.getCounterCW().getPoint());
            }

            //rightmost has to move in a clockwise direction (left turn)
            dir = Point.direction(lowerLeftmost.getPoint(), lowerRightmost.getPoint(), lowerRightmost.getCW().getPoint());
            while (dir >= 0) {
                if (dir == 0) {
                    int distOrig = Point.distance(lowerLeftmost.getPoint(), lowerRightmost.getPoint());
                    int distNew = Point.distance(lowerLeftmost.getPoint(), lowerRightmost.getCW().getPoint());
                    // if same x (vertical line), I need the closest
                    if (lowerLeftmost.getPoint().getX() == lowerRightmost.getPoint().getX() && distOrig > distNew)
                        lowerRightmost = lowerRightmost.getCW();
                        // if same y(horizontal line), I need the farthest
                    else if (lowerLeftmost.getPoint().getX() != lowerRightmost.getPoint().getX() && distOrig < distNew)
                        lowerRightmost = lowerRightmost.getCW();
                    else break;
                } else lowerRightmost = lowerRightmost.getCW();
                dir = Point.direction(lowerLeftmost.getPoint(), lowerRightmost.getPoint(), lowerRightmost.getCW().getPoint());
            }

        } while (!lowerLeftmost.equals(oldLeftmost) || !lowerRightmost.equals(oldRighmost));

        // find the upper tangent line
        do {
            oldLeftmost = upperLeftmost;
            oldRighmost = upperRightmost;

            // leftmost has to move in a clockwise direction (left turn)
            dir = Point.direction(upperRightmost.getPoint(), upperLeftmost.getPoint(), upperLeftmost.getCW().getPoint());
            while (dir >= 0) {
                if (dir == 0) {
                    int distOrig = Point.distance(upperRightmost.getPoint(), upperLeftmost.getPoint());
                    int distNew = Point.distance(upperRightmost.getPoint(), upperLeftmost.getCW().getPoint());
                    // if same x (vertical line), I need the closest
                    if (upperRightmost.getPoint().getX() == upperLeftmost.getPoint().getX() && distOrig > distNew)
                        upperLeftmost = upperLeftmost.getCW();
                        // if same y(horizontal line), I need the farthest
                    else if (upperRightmost.getPoint().getX() != upperLeftmost.getPoint().getX() && distOrig < distNew)
                        upperLeftmost = upperLeftmost.getCW();
                    else break;
                } else upperLeftmost = upperLeftmost.getCW();
                dir = Point.direction(upperRightmost.getPoint(), upperLeftmost.getPoint(), upperLeftmost.getCW().getPoint());
            }

            // rightmost has to move in a counterclockwise direction (right turn)
            dir = Point.direction(upperLeftmost.getPoint(), upperRightmost.getPoint(), upperRightmost.getCounterCW().getPoint());
            while (dir <= 0) {
                if (dir == 0) {
                    int distOrig = Point.distance(upperLeftmost.getPoint(), upperRightmost.getPoint());
                    int distNew = Point.distance(upperLeftmost.getPoint(), upperRightmost.getCounterCW().getPoint());
                    // if same x (vertical line), I need the closest
                    if (upperLeftmost.getPoint().getX() == upperRightmost.getPoint().getX() && distOrig > distNew)
                        upperRightmost = upperRightmost.getCounterCW();
                        // if same y(horizontal line), I need the farthest
                    else if (upperLeftmost.getPoint().getX() != upperRightmost.getPoint().getX() && distOrig < distNew)
                        upperRightmost = upperRightmost.getCounterCW();
                    else break;
                } else upperRightmost = upperRightmost.getCounterCW();
                dir = Point.direction(upperLeftmost.getPoint(), upperRightmost.getPoint(), upperRightmost.getCounterCW().getPoint());
            }
        } while (!upperLeftmost.equals(oldLeftmost) || !upperRightmost.equals(oldRighmost));

        upperRightmost.setCW(upperLeftmost);
        lowerLeftmost.setCW(lowerRightmost);
        convex.push(upperRightmost);
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 650;
    }
}
