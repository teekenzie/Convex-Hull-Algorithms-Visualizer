/*
Marriage Before Conquest
    Time Complexity: O(n log h)
 */
package algorithms;

import setup.*;
import userinterface.AnimationArea;

import java.util.HashSet;

public class MarriageBeforeConquest extends ConvexHullAlgorithm{
    /**
     * Also known as Kirkpatrick–Seidel algorithm
     * It starts by dividing the list of points into halves by the median x-coordinate
     * Then it recursively creates bridges that would go through the median x-coordinate
     * The bridges are then connected to form the upper hull
     * the same process is being done for the lower hull
     *
     * Constructing the bridges takes O(n) time and uses the following process:
     *      Randomly pairs the points
     *      Find the median slope among the points
     *      Start pruning the points based on the median slope
     *      Repeat doing so until the bridge is found
     */
    private static class BridgeCall{
        /**
         * Represent the parameters of the bridgeDraw method
         */
        Point pt1;
        Point pt2;
        Point[] section;
        int midX;
        Point[] allPts;
        public BridgeCall(Point pt1, Point pt2, Point[] section, int midX, Point[] allPts) {
            this.pt1 = pt1;
            this.pt2 = pt2;
            this.section = section;
            this.midX = midX;
            this.allPts = allPts;
        }
    }
    private static class ConnectCall{
        /**
         * Represents the parameters of the connectDraw method
         */
        Point pt1;
        Point pt2;
        Point[] section;
        Line edge;
        public ConnectCall(Point pt1, Point pt2, Point[] section, Line edge) {
            this.pt1 = pt1;
            this.pt2 = pt2;
            this.section = section;
            this.edge = edge;
        }
    }

    private final Point[] points;
    private final HashSet<Point> finalConvex;
    private boolean isComplete;

    private final Stack<BridgeCall> calls;
    private final PointStack upperConvex;
    private final PointStack lowerConvex;
    private Line[] pairsDraw;
    private int middleXDraw;
    private boolean isUpperStart, isUpperDone;
    private boolean isLowerStart, isLowerDone;

    public MarriageBeforeConquest(Point[] points) {
        this.points = points;
        isComplete = false;

        calls = new Stack<>();
        upperConvex = new PointStack();
        lowerConvex = new PointStack();
        finalConvex = new HashSet<>();
        pairsDraw = new Line[0];
        middleXDraw = -1;

        isUpperStart = false;
        isUpperDone = false;
        isLowerStart = false;
        isLowerDone = false;
    }

    /**
     * Draws the points and lines to the canvas
     *
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point pt: points)
            animationArea.drawPoint(pt);
        upperConvex.sortXNDraw(animationArea); // sorting is only needed for the draw feature
        lowerConvex.sortXNDraw(animationArea);

        if(!isUpperDone) {
            if (!isUpperStart) upperHullDraw(animationArea);
            else if (calls.isEmpty()) isUpperDone = true;
            else {
                BridgeCall tempCall =  calls.pop();
                middleXDraw = tempCall.midX;
                upperBridgeDraw(tempCall, animationArea);
            }
        }
        else if (!isLowerDone) {
            if (!isLowerStart) lowerHullDraw(animationArea);
            else if (calls.isEmpty()) isLowerDone = true;
            else {
                BridgeCall tempCall =  calls.pop();
                middleXDraw = tempCall.midX;
                lowerBridgeDraw(tempCall, animationArea);
            }
        }
        else {
            finalStep(animationArea);
            return;
        }

        for (int i =0; i < pairsDraw.length; i++) {
            Line tempLine = pairsDraw[i];
            if(i%6==0)
                animationArea.drawLine(animationArea.MAGENTA, tempLine.getFirstPt(), tempLine.getSecondPt());
            else if (i%6==1)
                animationArea.drawLine(animationArea.BLUE, tempLine.getFirstPt(), tempLine.getSecondPt());
            else if(i%6==2)
                animationArea.drawLine(animationArea.GREEN, tempLine.getFirstPt(), tempLine.getSecondPt());
            else if (i%6==3)
                animationArea.drawLine(animationArea.CYAN, tempLine.getFirstPt(), tempLine.getSecondPt());
            else if(i%6==4)
                animationArea.drawLine(animationArea.RED, tempLine.getFirstPt(), tempLine.getSecondPt());
            else
                animationArea.drawLine(animationArea.ORANGE, tempLine.getFirstPt(), tempLine.getSecondPt());
        }
        animationArea.drawLine(animationArea.LIGHT_GRAY, new Point(middleXDraw, 0), new Point(middleXDraw, animationArea.getCanvasSize()));
    }

    /**
     * Kicks off the process of constructing the upper hull
     * Represents the upperHull() method
     * @param animationArea the canvas to draw on
     */
    private void upperHullDraw(AnimationArea animationArea) {
        isUpperStart = true;
        Point leftMost = null;
        Point rightMost = null;
        for (Point pt: points) {
            if (leftMost == null || pt.getX() < leftMost.getX())
                leftMost = pt;
            else if (pt.getX() == leftMost.getX() &&
                    pt.getY() > leftMost.getY())
                leftMost = pt;
            if (rightMost == null || pt.getX() > rightMost.getX())
                rightMost = pt;
            else if (pt.getX() == rightMost.getX() &&
                    pt.getY() > rightMost.getY())
                rightMost = pt;
        }

        Stack<Point> tempPoints = new Stack<>();
        tempPoints.push(leftMost);
        tempPoints.push(rightMost);
        for (Point pt: points) {
            if (pt.getX() != leftMost.getX() && pt.getX() != rightMost.getX())
                tempPoints.push(pt);
        }
        Point[] tempPtsArr = new Point[tempPoints.size()];
        tempPtsArr = tempPoints.toArray(tempPtsArr);
        int midX = median( tempPtsArr);
        upperBridgeDraw(new BridgeCall(leftMost, rightMost, tempPtsArr, midX, tempPtsArr), animationArea);
    }

    /**
     * Constructs the upper bridges of the upper hull
     * Represents the upperBridge() method
     * @param node BridgeCall object containing the necessary parameters
     * @param animationArea the canvas to draw on
     */
    private void upperBridgeDraw(BridgeCall node, AnimationArea animationArea) {
        Point[] tempPoints = node.section;
        middleXDraw = node.midX;

        if(tempPoints.length <=1) {
            return; }

        Stack<Point> candidates = new Stack<>();
        if (tempPoints.length ==2) {
            Line tempEdge = new Line(tempPoints[0], tempPoints[1]);
            connectDraw(new ConnectCall(node.pt1, node.pt2, node.allPts, tempEdge), animationArea);
            return;
        }
        Stack<Line> pairs = new Stack<>();
        for (int i = 0; i < tempPoints.length; i++) { // Pair up the points
            if (i + 1 < tempPoints.length) {
                if (tempPoints[i].getX()==tempPoints[i+1].getX()) {
                    if (tempPoints[i].getY() > tempPoints[i+1].getY()) // upper point for upper hull
                        candidates.push(tempPoints[i]);
                    else
                        candidates.push(tempPoints[i+1]);
                    i++;
                }
                else
                    pairs.push(new Line(tempPoints[i], tempPoints[++i]));
            }
            else
                candidates.push(tempPoints[i]);
        }

        pairsDraw = new Line[pairs.size()];
        pairsDraw = pairs.toArray(pairsDraw);
        double medSlope;
        if (pairsDraw.length < 5)
            medSlope = avg(pairsDraw);
        else
            medSlope = median(pairsDraw);
        // same code as the rough draft
        Stack<Line> small = new Stack<>();
        Stack<Line> equal = new Stack<>();
        Stack<Line> large = new Stack<>();
        for (Line pair : pairs) {
            if(pair.slope() < medSlope)
                small.push(pair);
            else if (pair.slope() == medSlope)
                equal.push(pair);
            else if (pair.slope() > medSlope)
                large.push(pair);
        }
        Stack<Point> max = null;
        Point minXPt = null;
        Point maxXPt = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Point pt: tempPoints) { // find the top point
            double val = pt.getY() - medSlope * pt.getX();
            if (val > maxVal) {
                max = new Stack<>();
                maxVal = val;
                max.push(pt);
                minXPt = pt;
                maxXPt = pt;
            }
            else if (val == maxVal) {
                max.push(pt);
                if (pt.getX() < minXPt.getX())
                    minXPt = pt;
                if (pt.getX() > maxXPt.getX())
                    maxXPt = pt;
            }
        }
        if (minXPt.getX() <= middleXDraw && maxXPt.getX() > middleXDraw) {
            Line tempEdge = new Line(minXPt, maxXPt);
            connectDraw(new ConnectCall(node.pt1, node.pt2, node.allPts, tempEdge), animationArea);
            return;
        }
        else if (maxXPt.getX() <= middleXDraw) {
            for(Line line: large)
                candidates.push(line.getSecondPt());
            for (Line line: equal) {
                candidates.push(line.getSecondPt());
            }
            for (Line line: small) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
        }
        else if (minXPt.getX() > middleXDraw) {
            for (Line line:small)
                candidates.push(line.getFirstPt());
            for (Line line: equal) {
                candidates.push(line.getFirstPt());
            }
            for (Line line: large) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
        }

        Point[] candidatesArr = new Point[candidates.size()];
        calls.push(new BridgeCall(node.pt1, node.pt2, candidates.toArray(candidatesArr), node.midX, node.allPts));
    }

    /**
     * Called upon to construct more bridges if needed
     * @param node ConnectCall object that contains the needed parameters
     * @param animationArea the canvas to draw on
     */
    private void connectDraw(ConnectCall node, AnimationArea animationArea) {
        Point pt1 = node.pt1;
        Point pt2 = node.pt2;
        Point[] tempPoints = node.section;
        Line edge = node.edge;

        Point leftPt = edge.getFirstPt();
        Point rightPt = edge.getSecondPt();
        animationArea.drawLine(animationArea.RED, leftPt, rightPt);
        pairsDraw = new Line[0];
        Stack<Point> left = new Stack<>();
        left.push(leftPt);
        Stack<Point> right = new Stack<>();
        right.push(rightPt);
        for(Point pt: tempPoints) {
            if (pt.getX() < leftPt.getX())
                left.push(pt);
            if(pt.getX() > rightPt.getX())
                right.push(pt);
        }

        if (leftPt.equals(pt1)) {
            boolean added = finalConvex.add(pt1);
            if(added && !isUpperDone)
                upperConvex.push(pt1);
            else if (added) // && isUpperDone
                lowerConvex.push(pt1);
        }
        else if (left.size() > 0) {
            Point[] leftArr = new Point[left.size()];
            leftArr = left.toArray(leftArr);
            int tempMid = median(leftArr);
            calls.push(new BridgeCall(pt1, leftPt, leftArr, tempMid, leftArr));
        }

        if (rightPt.equals(pt2)) {
            boolean added = finalConvex.add(pt2);
            if(added && !isUpperDone)
                upperConvex.push(pt2);
            else if (added) // && isUpperDone
                lowerConvex.push(pt2);
        }
        else if (right.size() > 0) {
            Point[] rightArr = new Point[right.size()];
            rightArr = right.toArray(rightArr);
            int tempMid = median(rightArr);
            calls.push(new BridgeCall(rightPt, pt2, rightArr, tempMid, rightArr));
        }
    }

    /**
     * Kicks off the process of constructing the lower hull
     * Represents the lowerHull() method
     * @param animationArea the canvas to draw on
     */
    private void lowerHullDraw(AnimationArea animationArea) {
        isLowerStart = true;
        middleXDraw = -1;
        Point leftMost = null;
        Point rightMost = null;
        for (Point pt: points) {
            if (leftMost == null || pt.getX() < leftMost.getX())
                leftMost = pt;
            else if (pt.getX() == leftMost.getX() &&
                    pt.getY() < leftMost.getY())
                leftMost = pt;
            if (rightMost == null || pt.getX() > rightMost.getX())
                rightMost = pt;
            else if (pt.getX() == rightMost.getX() &&
                    pt.getY() < rightMost.getY())
                rightMost = pt;
        }
        Stack<Point> tempPoints = new Stack<>();
        tempPoints.push(leftMost);
        tempPoints.push(rightMost);
        for (Point pt: points) {
            if (pt.getX() != leftMost.getX() && pt.getX() != rightMost.getX())
                tempPoints.push(pt);
        }
        Point[] tempPtsArr = new Point[tempPoints.size()];
        tempPtsArr = tempPoints.toArray(tempPtsArr);
        int midX = median( tempPtsArr);
        lowerBridgeDraw(new BridgeCall(leftMost, rightMost, tempPtsArr, midX, tempPtsArr), animationArea);
    }

    /**
     * Constructs the lower bridges of the lower hull
     * Represents the lowerBridge() method
     * @param node BridgeCall object containing the necessary paremeters
     * @param animationArea the canvas to draw on
     */
    private void lowerBridgeDraw(BridgeCall node, AnimationArea animationArea) {
        Point[] tempPoints = node.section;
        middleXDraw = node.midX;

        if(tempPoints.length <= 1) {
            return; }

        Stack<Point> candidates = new Stack<>();
        if (tempPoints.length ==2 ) {
            Line tempEdge = new Line(tempPoints[0], tempPoints[1]);
            connectDraw(new ConnectCall(node.pt1, node.pt2, node.allPts, tempEdge), animationArea);
            return;
        }
        Stack<Line> pairs = new Stack<>();
        for (int i = 0; i < tempPoints.length; i++) { // Pair up the points
            if (i + 1 < tempPoints.length) {
                if (tempPoints[i].getX()==tempPoints[i+1].getX()) {
                    if (tempPoints[i].getY() < tempPoints[i+1].getY()) // lower point for lower hull
                        candidates.push(tempPoints[i]);
                    else
                        candidates.push(tempPoints[i+1]);
                    i++;
                }
                else
                    pairs.push(new Line(tempPoints[i], tempPoints[++i]));
            }
            else
                candidates.push(tempPoints[i]);
        }

        pairsDraw = new Line[pairs.size()];
        pairsDraw = pairs.toArray(pairsDraw);
        double medSlope;
        if(pairs.size() < 5)
            medSlope = avg(pairsDraw);
        else
            medSlope = median(pairsDraw);

        Stack<Line> small = new Stack<>();
        Stack<Line> equal = new Stack<>();
        Stack<Line> large = new Stack<>();
        for (Line pair : pairs) {
            if(pair.slope() < medSlope)
                small.push(pair);
            else if (pair.slope() == medSlope)
                equal.push(pair);
            else if (pair.slope() > medSlope)
                large.push(pair);
        }

        Stack<Point> min = null;
        Point minXPt = null;
        Point maxXPt = null;
        double minVal = Double.POSITIVE_INFINITY;
        for (Point pt: tempPoints) { // find the bottom point
            double val = pt.getY() - medSlope * pt.getX();
            if (val < minVal) {
                min = new Stack<>();
                minVal = val;
                min.push(pt);
                minXPt = pt;
                maxXPt = pt;
            }
            else if (val == minVal) {
                min.push(pt);
                if (pt.getX() < minXPt.getX())
                    minXPt = pt;
                if (pt.getX() > maxXPt.getX())
                    maxXPt = pt;
            }
        }
        if (minXPt.getX() <= middleXDraw && maxXPt.getX() > middleXDraw) {
            Line tempEdge = new Line(minXPt, maxXPt);
            connectDraw(new ConnectCall(node.pt1, node.pt2, node.allPts, tempEdge), animationArea);
        }
        else if (maxXPt.getX() <= middleXDraw) {
            for(Line line: large) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
            for (Line line: equal) {
                candidates.push(line.getSecondPt());
            }
            for (Line line: small) {
                candidates.push(line.getSecondPt());
            }
        }
        else if (minXPt.getX() > middleXDraw) {
            for (Line line:small) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
            for (Line line: equal) {
                candidates.push(line.getFirstPt());
            }
            for (Line line: large) {
                candidates.push(line.getFirstPt());
            }
        }

        Point[] candidatesArr = new Point[candidates.size()];
        calls.push(new BridgeCall(node.pt1, node.pt2, candidates.toArray(candidatesArr), node.midX, node.allPts));
    }

    /**
     * Signal that this algorithm has been completed
     * Connect the upper hull and the lower hull together
     * @param animationArea the canvas to draw on
     */
    private void finalStep(AnimationArea animationArea) {
        isComplete = true;
        animationArea.drawLine(animationArea.BLACK, upperConvex.firstPt(), lowerConvex.firstPt());
        animationArea.drawLine(animationArea.BLACK, upperConvex.lastPt(), lowerConvex.lastPt());
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
     * The direct implementation of the Marriage Before Conquest algorithm
     * Also serves as the rough draft of the draw feature
     * @return an array of points representing the convex hull
     */
    private Point[] directImp() {
        upperHull();
        lowerHull();
        Point[] result = new Point[finalConvex.size()];
        return finalConvex.toArray(result);
    }

    /**
     * Construct the upper hull of the convex hull
     * Eliminates the collinear points on the leftmost and rightmost side
     * Initiate the process of connecting points in the lower hull
     */
    private void upperHull() {
        Point leftMost = null;
        Point rightMost = null;
        for (Point pt: points) {
            if (leftMost == null || pt.getX() < leftMost.getX())
                leftMost = pt;
            else if (pt.getX() == leftMost.getX() &&
                    pt.getY() > leftMost.getY())
                leftMost = pt;
            if (rightMost == null || pt.getX() > rightMost.getX())
                rightMost = pt;
            else if (pt.getX() == rightMost.getX() &&
                    pt.getY() > rightMost.getY())
                rightMost = pt;
        }

        Stack<Point> tempPoints = new Stack<>();
        tempPoints.push(leftMost);
        tempPoints.push(rightMost);
        for (Point pt: points) {
            if (pt.getX() != leftMost.getX() && pt.getX() != rightMost.getX())
                tempPoints.push(pt);
        }
        Point[] tempPtsArr = new Point[tempPoints.size()];
        connect(leftMost, rightMost, tempPoints.toArray(tempPtsArr), true);
    }

    /**
     * Constructs the bridge in the upper hull
     * @param tempPoints an array of points
     * @param middleX an integer representing the median x
     * @return a line constructing the upper bridge
     */
    private static Line upperBridge(Point[] tempPoints, int middleX) {
        Stack<Point> candidates = new Stack<>();
        if (tempPoints.length == 2) return new Line(tempPoints[0], tempPoints[1]);

        Stack<Line> pairs = new Stack<>();
        for (int i = 0; i < tempPoints.length; i++) { // Pair up the points
            if (i + 1 < tempPoints.length) {
                if (tempPoints[i].getX()==tempPoints[i+1].getX()) {
                    if (tempPoints[i].getY() > tempPoints[i+1].getY()) // upper point for upper hull
                        candidates.push(tempPoints[i]);
                    else
                        candidates.push(tempPoints[i+1]);
                    i++;
                }
                else
                    pairs.push(new Line(tempPoints[i], tempPoints[++i]));
            }
            else
                candidates.push(tempPoints[i]);
        }

        Line[] pairsArr = new Line[pairs.size()];
        double medSlope;
        if(pairs.size() < 5)
            medSlope = avg(pairs.toArray(pairsArr));
        else
            medSlope = median(pairs.toArray(pairsArr));

        Stack<Line> small = new Stack<>();
        Stack<Line> equal = new Stack<>();
        Stack<Line> large = new Stack<>();
        for (Line pair : pairs) {
            if(pair.slope() < medSlope)
                small.push(pair);
            else if (pair.slope() == medSlope)
                equal.push(pair);
            else if (pair.slope() > medSlope)
                large.push(pair);
        }

        Stack<Point> max = null;
        Point minXPt = null;
        Point maxXPt = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Point pt: tempPoints) { // find the top point
            double val = pt.getY() - medSlope * pt.getX();
            if (val > maxVal) {
                max = new Stack<>();
                maxVal = val;
                max.push(pt);
                minXPt = pt;
                maxXPt = pt;
            }
            else if (val == maxVal) {
                max.push(pt);
                if (pt.getX() < minXPt.getX())
                    minXPt = pt;
                if (pt.getX() > maxXPt.getX())
                    maxXPt = pt;
            }
        }

        if (minXPt.getX() <= middleX && maxXPt.getX() > middleX)
            return new Line(minXPt, maxXPt);
        else if (maxXPt.getX() <= middleX) {
            for(Line line: large)
                candidates.push(line.getSecondPt());
            for (Line line: equal) {
                candidates.push(line.getSecondPt());
            }
            for (Line line: small) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
        }
        else if (minXPt.getX() > middleX) {
            for (Line line:small)
                candidates.push(line.getFirstPt());
            for (Line line: equal) {
                candidates.push(line.getFirstPt());
            }
            for (Line line: large) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
        }

        Point[] candidatesArr = new Point[candidates.size()];
        return upperBridge(candidates.toArray(candidatesArr), middleX);
    }

    /**
     * Construct the lower hull of the convex hull
     * Eliminates the collinear points on the leftmost and rightmost side
     * Initiate the process of connecting points in the lower hull
     */
    private void lowerHull() {
        Point leftMost = null;
        Point rightMost = null;
        for (Point pt: points) {
            if (leftMost == null || pt.getX() < leftMost.getX())
                leftMost = pt;
            else if (pt.getX() == leftMost.getX() &&
                    pt.getY() < leftMost.getY())
                leftMost = pt;
            if (rightMost == null || pt.getX() > rightMost.getX())
                rightMost = pt;
            else if (pt.getX() == rightMost.getX() &&
                    pt.getY() < rightMost.getY())
                rightMost = pt;
        }
        Stack<Point> tempPoints = new Stack<>();
        tempPoints.push(leftMost);
        tempPoints.push(rightMost);
        for (Point pt: points) {
            if (pt.getX() != leftMost.getX() && pt.getX() != rightMost.getX())
                tempPoints.push(pt);
        }
        Point[] tempPtsArr = new Point[tempPoints.size()];
        connect(leftMost, rightMost, tempPoints.toArray(tempPtsArr), false);
    }

    /**
     * Construct the bridge in the lower hull
     * @param tempPoints an array of points
     * @param middleX an integer representing the median x
     * @return a line constructing the lower bridge
     */
    private static Line lowerBridge (Point[] tempPoints, int middleX) {
        Stack<Point> candidates = new Stack<>();
        if (tempPoints.length==2)
            return new Line(tempPoints[0],tempPoints[1]);
        Stack<Line> pairs = new Stack<>();

        for (int i = 0; i < tempPoints.length; i++) { // Pair up the points
            if (i + 1 < tempPoints.length) {
                if (tempPoints[i].getX()==tempPoints[i+1].getX()) {
                    if (tempPoints[i].getY() < tempPoints[i+1].getY()) // lower point for lower hull
                        candidates.push(tempPoints[i]);
                    else
                        candidates.push(tempPoints[i+1]);
                    i++;
                }
                else
                    pairs.push(new Line(tempPoints[i], tempPoints[++i]));
            }
            else
                candidates.push(tempPoints[i]);
        }

        Line[] pairsArr = new Line[pairs.size()];
        double medSlope;
        if(pairs.size() < 5)
            medSlope = avg(pairs.toArray(pairsArr));
        else
            medSlope = median(pairs.toArray(pairsArr));

        Stack<Line> small = new Stack<>();
        Stack<Line> equal = new Stack<>();
        Stack<Line> large = new Stack<>();
        for (Line pair : pairs) {
            if(pair.slope() < medSlope)
                small.push(pair);
            else if (pair.slope() == medSlope)
                equal.push(pair);
            else if (pair.slope() > medSlope)
                large.push(pair);
        }

        Stack<Point> min = null;
        Point minXPt = null;
        Point maxXPt = null;
        double minVal = Double.POSITIVE_INFINITY;
        for (Point pt: tempPoints) { // find the bottom point
            double val = pt.getY() - medSlope * pt.getX();
            if (val < minVal) {
                min = new Stack<>();
                minVal = val;
                min.push(pt);
                minXPt = pt;
                maxXPt = pt;
            }
            else if (val == minVal) {
                min.push(pt);
                if (pt.getX() < minXPt.getX())
                    minXPt = pt;
                if (pt.getX() > maxXPt.getX())
                    maxXPt = pt;
            }
        }

        if (minXPt.getX() <= middleX && maxXPt.getX() > middleX)
            return new Line(minXPt, maxXPt);
        else if (maxXPt.getX() <= middleX) {
            for(Line line: large) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
            for (Line line: equal) {
                candidates.push(line.getSecondPt());
            }
            for (Line line: small) {
                candidates.push(line.getSecondPt());
            }
        }
        else if (minXPt.getX() > middleX) {
            for (Line line:small) {
                candidates.push(line.getFirstPt());
                candidates.push(line.getSecondPt());
            }
            for (Line line: equal) {
                candidates.push(line.getFirstPt());
            }
            for (Line line: large) {
                candidates.push(line.getFirstPt());
            }
        }

        Point[] candidatesArr = new Point[candidates.size()];
        return lowerBridge(candidates.toArray(candidatesArr), middleX);

    }

    /**
     * Connects the hull together by recursively constructing bridges in betweeen
     * @param pt1 the leftmost point
     * @param pt2 the rightmost point
     * @param tempPoints an array of points
     * @param isUpper true if currently working on the upper hull, otherwise false
     */
    private void connect(Point pt1, Point pt2, Point[] tempPoints, boolean isUpper) {
        int middleX = median(tempPoints);
        Line edge;
        if (isUpper)
            edge = upperBridge(tempPoints, middleX);
        else
            edge = lowerBridge(tempPoints, middleX);

        Point leftPt = edge.getFirstPt();
        Point rightPt = edge.getSecondPt();
        Stack<Point> left = new Stack<>();
        left.push(leftPt);
        Stack<Point> right = new Stack<>();
        right.push(rightPt);
        for(Point pt: tempPoints) {
            if (pt.getX() < leftPt.getX())
                left.push(pt);
            if(pt.getX() > rightPt.getX())
                right.push(pt);
        }

        if (leftPt.equals(pt1)){
            finalConvex.add(pt1);
        }
        else if (left.size() > 0){
            Point[] leftArr = new Point[left.size()];
            connect(pt1, leftPt, left.toArray(leftArr), isUpper);
        }

        if (rightPt.equals(pt2)) {
            finalConvex.add(pt2);
        }
        else if (right.size() > 0 ){
            Point[] rightArr = new Point[right.size()];
            connect(rightPt, pt2, right.toArray(rightArr), isUpper);
        }
    }

    /**
     * Return the average slope among the lines given
     * @param tempLine an array of lines
     * @return a double representing the average slope
     */
    private static double avg(Line[] tempLine) {
        double total = 0;
        for (Line line: tempLine) {
            total += line.slope();
        }
        return total/tempLine.length;
    }

    /**
     * Return the median x-coordinate among all the points
     * @param tempPoints an array of points
     * @return an integer representing the median x-coordinate
     */
    private int median(Point[] tempPoints) {
        Point medPoint = new Median<Point>().median(tempPoints, Point.BYXORDER);
        return medPoint.getX();
    }

    /**
     * Return the median slope of the lines given
     * @param tempLine an array of lines
     * @return a double representing the median slope
     */
    private static double median(Line[] tempLine) {
        Line medLine = new Median<Line>().median(tempLine, Line.BYSLOPE);
        return medLine.slope();
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 500;
    }
}