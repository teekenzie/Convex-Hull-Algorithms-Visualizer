package algorithms;

import setup.Point;
import userinterface.AnimationArea;

public abstract class ConvexHullAlgorithm {
    /**
     * Draws the points and lines to the canvas
     * @param animationArea the canvas
     */
    public abstract void draw(AnimationArea animationArea);

    /**
     * whether the convex hull is complete
     * @return true if it is completed, otherwise false
     */
    public abstract boolean isComplete();

    /**
     *
     * @return the points that forms the convex hull
     */
    public abstract Point[] getResult();

    /**
     *
     * @return the time interval the animation should run at
     */
    public abstract int getTime();
}
