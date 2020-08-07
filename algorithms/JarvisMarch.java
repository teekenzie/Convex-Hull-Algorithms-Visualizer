/*
  Jarvis March Algorithm
       Time Complexity: O(nh)
 */

package algorithms;

import setup.Point;
import setup.PointStack;
import userinterface.AnimationArea;

public class JarvisMarch extends ConvexHullAlgorithm{

    /**
     * Finds the leftmost point first
     * Loop through the points given to find the point that is leftmost to first
     * set the newly found point to first
     * Repeat steps 2 and 3 until the newly found point is the leftmost point
     */

    private final Point[] points;
    private final PointStack convex;
    private boolean isComplete;
    private final int size;

    private boolean isStart; // indicates if the first step has been started
    private boolean foundAll; // indication to run the final step
    private int firstInd, secondInd, thirdInd, leftmostInd;

    public JarvisMarch(Point[] points)
    {
        this.points = points;
        isComplete = false;
        foundAll = false;
        size = points.length;
        isStart = false;
        leftmostInd = 0;

        convex = new PointStack();
    }

    /**
     * make the leftmost point the first item of the array
     */
    private void updateLeftmost(int i) {
        if (points[i].getX() < points[leftmostInd].getX() ||
                points[i].getX() == points[leftmostInd].getX() && points[i].getY() > points[leftmostInd].getY())
            leftmostInd = i;
    }

    /**
     * Draws the points and lines to the canvaas
     * @param animationArea the canvas
     */
    @Override
    public void draw(AnimationArea animationArea) {
        for (Point point: points)
            animationArea.drawPoint(point);
        convex.draw(animationArea);
        if (!isStart) firstStep();
        if (foundAll) finalStep(animationArea);
        else nextStep(animationArea);
    }

    private void firstStep() {
        isStart = true;
        for (int i = 1; i < size; i++)
            updateLeftmost(i);
        convex.push(points[leftmostInd]);

        firstInd = leftmostInd;
        secondInd = 0;
        thirdInd = 0;
    }

    /**
     * Runs the animation one step at a time
     * Draws onto the canvas every progress made
     * Everytime this method is called upon, only one point is being checked
     *
     * @param animationArea the canvas to draw on
     */
    private void nextStep(AnimationArea animationArea) {
        animationArea.drawLine(animationArea.RED, points[firstInd], points[secondInd]);
        animationArea.drawLine(animationArea.GREEN, points[firstInd], points[thirdInd]);
        int dir = Point.direction(points[firstInd], points[secondInd], points[thirdInd]);
        if (dir > 0 || dir==0 && Point.distance(points[firstInd], points[thirdInd]) > Point.distance(points[firstInd], points[secondInd]))
        {
            secondInd = thirdInd;
        }
        if (thirdInd == size-1)
        {
            if (secondInd == leftmostInd) {
                foundAll = true;
                return;
            }
            convex.push(points[secondInd]);
            firstInd = secondInd;
            secondInd = (firstInd+1)%size;
            thirdInd = 0;
        }
        else
            thirdInd ++;
    }

    /**
     * connects the last point to the first point
     * signal that the convex hull has been completed
     * @param animationArea the canvas to draw on
     */
    private void finalStep(AnimationArea animationArea) {
        isComplete = true;
        animationArea.drawLine(animationArea.BLACK, points[leftmostInd], convex.lastPt());
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
     * The direct implementation of the Jarvis March convex hull
     * Also serves as the rough draft for the draw feature
     * @return the points that forms the convex hull
     */
    private Point[] directImp() {
        for (int i = 1; i < size; i++)
            updateLeftmost(i);
        convex.push(points[leftmostInd]);

        int first = leftmostInd;
        while (true) {
            int second = (first + 1) % size;
            for (int third = 0; third < size; third++) {
                int dir = Point.direction(points[first], points[second], points[third]);
                if (dir > 0 || dir==0 && Point.distance(points[first], points[third]) > Point.distance(points[first], points[second]))
                    second = third;
            }
            if (second == leftmostInd) break;
            convex.push(points[second]);
            first = second;
        }
        return convex.getConvex();
    }

    /**
     * @return the time interval the animation should run at
     */
    @Override
    public int getTime() {
        return 250;
    }
}
