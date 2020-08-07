/*
  Chan's Algorithm
       Time Complexity: O (n log h)
 */
package algorithms;

import setup.HeapSort;
import setup.Point;
import setup.PointStack;
import userinterface.AnimationArea;

public class ChanAlgorithm extends ConvexHullAlgorithm{

    /**
     * Split the points into n/m sections
     * for each section, use graham scan to find its convex hull
     * Then find the right tangent point from a known point(extreme point) to the sections
     * choose the tangent that would form the greatest angle
     * if the number of points found is greater than m, "start over" with m^2
     *      (merge the convex hull when separating into sections instead of recalculating them)
     */

    private final Point[] points;
    private PointStack finalConvex;
    private boolean isComplete;
    private final int size;

    private int mDraw, index;
    private PointStack[] allMiniConvexes;
    private Point[] tangents;
    private Point lowestPt;
    private boolean isGrahamDone;
    private boolean isFoundTangents;
    private boolean isJarvisDone;
    private boolean foundAll;

    public ChanAlgorithm(Point[] points)
    {
        this.points = points;
        isComplete = false;
        size = points.length;

        mDraw = 4;
        isGrahamDone = false;
        foundAll = false;
        index = 0;
    }

    /**
     * Draws the points and lines to the canvas
     *
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point pt: points) {
            animationArea.drawPoint(pt);
            pt.setRed(false);
        }
        if (!foundAll && allMiniConvexes != null && tangents != null) {
            for (int i = 0; i < allMiniConvexes.length; i++) {
                if (i % 6 == 0)
                    allMiniConvexes[i].drawSmallConvex(animationArea, animationArea.MAGENTA);
                else if (i % 6 == 1)
                    allMiniConvexes[i].drawSmallConvex(animationArea, animationArea.BLUE);
                else if (i % 6 == 2)
                    allMiniConvexes[i].drawSmallConvex(animationArea, animationArea.GREEN);
                else if (i % 6 == 3)
                    allMiniConvexes[i].drawSmallConvex(animationArea, animationArea.CYAN);
                else if (i%  6 ==4 )
                    allMiniConvexes[i].drawSmallConvex(animationArea, animationArea.RED);
                else
                    allMiniConvexes[i].drawSmallConvex(animationArea, animationArea.ORANGE);
            }
            for (Point pt : tangents) {
                animationArea.drawLine(animationArea.LIGHT_GRAY, pt, finalConvex.lastPt());
            }
        }
        if (finalConvex != null)
            finalConvex.draw(animationArea);

        if (!isGrahamDone) grahamStep();
        else if (!isFoundTangents) findTangents();
        else if (!isJarvisDone) jarvisStep();
        else finalStep(animationArea);

        lowestPt.setRed(true);
        animationArea.drawPoint(lowestPt);
    }

    /**
     * Runs the graham step of the algorithm when the draw feature is being called upon
     */
    private void grahamStep() {
        allMiniConvexes = new PointStack[0];
        tangents = new Point[0];
        finalConvex = new PointStack();
        isGrahamDone = true;
        isFoundTangents = false;
        isJarvisDone = false;
        PointStack[] tempMiniConvexes = new PointStack[size/mDraw];
        if(tempMiniConvexes.length <= 1 ) {
            finalConvex = modifiedGraham(points, 0, points.length);
            isGrahamDone = true;
            isFoundTangents = true;
            isJarvisDone = true;
            return;
        }
        int pointer = 0;
        for (int i = 0; i < tempMiniConvexes.length; i++)
        {
            if (i== tempMiniConvexes.length-1)
                tempMiniConvexes[i] = modifiedGraham(points, pointer, size);
            else
                tempMiniConvexes[i] = modifiedGraham(points, pointer, pointer+mDraw);
            pointer += mDraw;
        }
        allMiniConvexes = tempMiniConvexes;
        finalConvex.push(lowestPt);
    }

    /**
     * Finds the right tangents to the sub convex halls and store them into an array
     */
    private void findTangents() {
        isFoundTangents = true;
        tangents = new Point[allMiniConvexes.length];
        for (int tang = 0; tang < tangents.length; tang++) {
            tangents[tang] = findRightTang(finalConvex.lastPt(), allMiniConvexes[tang]);
        }
    }

    /**
     * Run jarvis steps on the tangents found and determine which tangent is the most appropriate to connect to
     */
    private void jarvisStep() {
        isFoundTangents = false;
        Point nextPt = findNextPt(finalConvex, tangents);
        index++;
        if (nextPt.equals(lowestPt)) {
            foundAll = true;
            isJarvisDone = true;
            return;
        }
        else
            finalConvex.push(nextPt);
        if (index == mDraw) {
            mDraw = mDraw*2; // use doubling instead of squaring for visualization purpose
            isGrahamDone = false;
        }
    }

    /**
     * Connects the last point to the starting point
     * Update isComplete
     * @param animationArea the canvas to draw on
     */
    private void finalStep(AnimationArea animationArea) {
        isComplete = true;
        animationArea.drawLine(animationArea.BLACK, lowestPt, finalConvex.lastPt());
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
        return directImp(4);
    }

    /**
     * the direct implementation of Chan's Algorithm
     * Also serves as the rough draft for the draw feature
     * @param m the guess on how many outputs there are
     * @return the points that forms the convex hull
     */
    private Point[] directImp(int m) {
        PointStack[] tempMiniConvexes = new PointStack[size/m];
        if (tempMiniConvexes.length == 0 || tempMiniConvexes.length==1) // no point running chan's algorithm when only one sub convex
            return modifiedGraham(points, 0, points.length).getConvex();
        if(m==4){
            int pointer = 0;
            for (int i = 0; i < tempMiniConvexes.length; i++)
            {
                if (i== tempMiniConvexes.length-1)
                    tempMiniConvexes[i] = modifiedGraham(points, pointer, size);
                else
                    tempMiniConvexes[i] = modifiedGraham(points, pointer, pointer+m);
                pointer += m;
            }
        }
        else {
            int i = 0;
            for (PointStack allMiniConvex : allMiniConvexes) {
                tempMiniConvexes[i] = merge(tempMiniConvexes[i], allMiniConvex);
                i = (i + 1) % tempMiniConvexes.length;
            }
        }
        allMiniConvexes = tempMiniConvexes;

        finalConvex = new PointStack();
        finalConvex.push(lowestPt);
        for (int i = 0; i < m; i++) {
            tangents = new Point[allMiniConvexes.length];
            for (int tang = 0; tang < tangents.length; tang++) {
                tangents[tang] = findRightTang(finalConvex.lastPt(), allMiniConvexes[tang]);
            }

            Point nextPt = findNextPt(finalConvex, tangents);
            if (nextPt.equals(lowestPt)) return finalConvex.getConvex();
            else
                finalConvex.push(nextPt);
        }
        return directImp(m*m);
    }

    /**
     * Runs the graham scan algorithm on the specified range of points
     * the range is specified instead of copying the array and running graham scan on it to save the time complexity of copying
     * @param tempPoints the array that contains the input points
     * @param startInd the starting index of the specified range
     * @param endInd the index the specified range is up to
     * @return the convex hull that is formed from the specified range of points
     */
    private PointStack modifiedGraham(Point[] tempPoints, int startInd, int endInd) {
        if (startInd == endInd) return null;
        Point tempLowestPt = null;
        int miniSize = endInd - startInd;
        for (int i = startInd; i < endInd; i++) {
            if (tempLowestPt == null || tempPoints[i].compareTo(tempLowestPt) < 0)
                tempLowestPt = tempPoints[i];
        }
        if (lowestPt == null || tempLowestPt.compareTo(lowestPt) < 0)
            lowestPt = tempLowestPt;
        HeapSort.sortRange(tempPoints, tempLowestPt.BYSLOPE, startInd, endInd);

        int tested = startInd;
        PointStack miniConvex = new PointStack();
        miniConvex.push(tempPoints[tested++]); // this is the lowest point
        if (miniSize < 2) return miniConvex;
        miniConvex.push(tempPoints[tested++]);

        int dir;
        while (tested < miniSize + startInd) {
            Point[] recentlyAdded = miniConvex.lastTwo();
            dir = Point.direction(recentlyAdded[1], recentlyAdded[0], tempPoints[tested]);
            while (dir <= 0) {
                miniConvex.pop();
                if (miniConvex.size() == 1) break;
                recentlyAdded = miniConvex.lastTwo();
                dir = Point.direction(recentlyAdded[1], recentlyAdded[0], tempPoints[tested]);
            }
            miniConvex.push(tempPoints[tested++]);
        }
        return miniConvex;
    }

    /**
     * merges two convex hulls together
     * @param stack1 the first convex hull
     * @param stack2 the second convex hull
     * @return the points of the merged convex hull
     */
    private PointStack merge(PointStack stack1, PointStack stack2){
        if (stack1 == null) return stack2;

        int size1 = stack1.size();
        Point[] convex1 = stack1.getStack();
        int size2 = stack2.size();
        Point[] convex2 = stack2.getStack();
        Point sectionLowest;

        // the first point on the convex will always have the lowest point of its corresponding convex
        if (convex1[0].compareTo(convex2[0]) < 0) {
            sectionLowest = convex1[0];
            HeapSort.sort(convex2, sectionLowest.BYSLOPE, size2);
        }
        else {
            sectionLowest = convex2[0];
            HeapSort.sort(convex1, sectionLowest.BYSLOPE, size1);
        }

        Point[] combineConvex = new Point[size1 + size2];
        int pointer1 = 0;
        int pointer2 = 0;
        for (int i=0; i< combineConvex.length; i++) {
            if (pointer1 >= size1) combineConvex[i] = convex2[pointer2++];
            else if (pointer2 >= size2) combineConvex[i] = convex1[pointer1++];
            else if(sectionLowest.BYSLOPE.compare(convex1[pointer1], convex2[pointer2]) < 0)
                combineConvex[i] = convex1[pointer1++];
            else combineConvex[i] = convex2[pointer2++];
        }

        return modifiedGraham(combineConvex, 0, combineConvex.length);
    }

    /**
     * Finds the right tangent from the point to the specified convex hull
     * @param p the point that is focused on
     * @param miniStack the convex hull to find the right tangent to
     * @return the point from the convex hull that forms a right tangent from the point focused on
     */
    private static Point findRightTang(Point p, PointStack miniStack) {
        Point[] miniConvex = miniStack.getStack(); // could change to get stack?
        int miniSize = miniStack.size();
        int r = miniStack.size();
        int l = 0;
        int lBefore = direction(p, miniConvex[0], miniConvex[r-1]);
        int lAfter = direction(p, miniConvex[0], miniConvex[(l+1)%r]);
        while (l < r) {
            int c = (l+r) /2;
            // if it is the point, just return the point at the counterclockwise of it
            if (miniConvex[c].equals(p))
                return miniConvex[(c+1)%miniSize];
            else if (miniConvex[l%miniSize].equals(p))
                return miniConvex[(l+1)%miniSize];

            int cBefore = direction(p, miniConvex[c], miniConvex[repeat(c-1, miniSize)]);
            int cNext = direction(p, miniConvex[c], miniConvex[(c+1) % miniSize]);
            int cSide = direction(p, miniConvex[l], miniConvex[c]);
            if (cBefore != -1 && cNext != -1) {
                return miniConvex[c];
            }
            else if (cSide > 0 && (lAfter < 0 || lBefore == lAfter)
                    || cSide == -1 && cBefore == -1) {
                r = c;
            }
            else {
                l= c+1;
                lBefore = -cNext;
                lAfter = direction(p, miniConvex[l%miniSize], miniConvex[(l+1)%miniSize]);
            }
        }
        return miniConvex[l%miniSize];
    }

    /**
     * Deal with negative numbers
     * @param num the index of the value in the array
     * @param miniSize the size of the array
     * @return if num is negative, then it would return a new index that is num away from the size
     *      ex. miniSize = 3, num -1 -> 2
     */
    private static int repeat(int num, int miniSize) {
        if (num < 0) {
            num = miniSize + num;
        }
        return num;
    }

    /**
     * the direction the third point is in respect to the first and second point
     * Used in place of Point.direction() to make implementing findRightTang() more convenience
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @return 1 if it is a left turn
     *      -1 if it is a right turn
     *      0 if it is collinear
     */
    private static int direction(Point p1, Point p2, Point p3) {
        int num = Point.direction(p1, p2, p3);
        return Integer.compare(num, 0);
    }

    /**
     * Find the next point of the tangent to connect to
     * Represents the jarvis step of the algorithm
     * @param finalConvex the final convex that is forming
     * @param tangents the right tangents to the sub convex hull
     * @return the point to connect to
     */
    private Point findNextPt(PointStack finalConvex, Point[] tangents) {
        Point first = finalConvex.lastPt();
        Point second = tangents[0];

        for (Point pt: tangents)
        {
            int dir = Point.direction(first, second, pt);
            if (dir < 0 || dir==0 && Point.distance(first, pt) > Point.distance(first, second)) // it has to be a right turn or collinear
            {
                second = pt;
            }
        }
        return second;
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 500;
    }
}
