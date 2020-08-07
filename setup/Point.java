package setup;
import java.util.Comparator;
import java.util.Objects;

public class Point implements Comparable<Point> {

    public final Comparator<Point> BYSLOPE = new BySlope();
    public static final Comparator<Point> BYXORDER = new ByXOrder();
    private final int x;
    private final int y;
    private boolean isRed; // whether a point is being used in the largest perimeter
    private boolean collinearDisable; // used by DivideNConquer to deal with vertical colliner points

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        isRed = false;
        collinearDisable = false;
    }

    /**
     *
     * @return the x coordinate of the point
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return the y coordinate of the point
     */
    public int getY() {
        return y;
    }

    /**
     * whether the point is used to construct the largest perimeter
     * @return true if it part of the largest perimeter, otherwise false
     */
    public boolean isRed() {
        return isRed;
    }

    /**
     * Change whether the point is part of largest perimeter
     * @param inConvex true if it is part of the largest perimeter, otherwise false
     */
    public void setRed(boolean inConvex) {
        // if the point is part of the largest perimeter, then it should be red
        isRed = inConvex;
    }

    /**
     * Signal that this point is unusable
     */
    public void collinearDisable() {
        this.collinearDisable = true;
    }

    /**
     * @return whether this point has been disabled
     */
    public boolean getColliearDisable() {
        return collinearDisable;
    }

    /**
     * Checks the y first, then the x
     * @param o the other point
     * @return 1 if self is greater, -1 if self is smaller, 0 if self is equal
     */
    @Override
    public int compareTo(Point o) {
        if (y > o.y) return 1;
        else if (y < o.y) return -1;
        else if (x > o.x) return 1;
        else if (x < o.x) return -1;
        else return 0;
    }

    /**
     * The slope between self and the other point
     * @param o the other point
     * @return slope
     */
    public double slopeTo(Point o) {
        double dy = o.y - y;
        double dx = o.x - x;
        return dy / dx;
    }

    private class BySlope implements Comparator<Point> {
        /**
         * When compare the order of the slope goes as follow
         * [ 0, 1, 2, Infinity, -2, -1, 0]
         * The first 0 is the point directly to the right of self
         * The second 0 is the point directly to the left of self
         * Infinity is the point directly above self
         * slope > 0 is the point in the first quadrant in relation to self
         * slope < 0 is the point in the second quadrant in relation to self
         * @param o1 the first point
         * @param o2 the second point
         * @return whether first point is greater (1), less(-1) to second point in terms of slope.
         *      If two points have the same slope, use natural order
         */
        @Override
        public int compare(Point o1, Point o2) {
            // put self in the front when sorting
            if (Point.this.equals(o1)) return -1;
            else if (Point.this.equals(o2)) return 1;
            // need to deal with the two 0s first
            if (o1.y == y && o2.y == y)
            {
                if (o1.x > x && o2.x > x || o1.x < x && o2.x < x)
                    return o1.compareTo(o2);
                else return o1.compareTo(o2) * -1; // need to flip because the bigger x should be less in terms of slope
            }
            else if (o1.y == y) return (o1.x > x) ? -1 : 1;
            else if (o2.y == y) return (o2.x > x) ? 1 : -1;
            double slope1 = slopeTo(o1);
            double slope2 = slopeTo(o2);
            assert slope1 != 0 && slope2 != 0 : "The slope shouldn't be 0";
            if (slope1 == slope2) return o1.compareTo(o2);
            else if (slope1 == Double.POSITIVE_INFINITY) {
                if (slope2 > 0) return 1;
                else return -1;
            } else if (slope2 == Double.POSITIVE_INFINITY) {
                if (slope1 > 0) return -1;
                else return 1;
            }
            // both not infinity nor are they 0
            else if (slope1 > 0 && slope2 > 0 || slope1 < 0 && slope2 < 0) // both slope have the same sign
                return Double.compare(slope1, slope2);
            else // two slopes have different size
            {
                slope1 *= -1; // positive slopes are "less" so need to multiply by -1
                slope2 *= -1;
                return Double.compare(slope1, slope2);
            }
        }
    }

    private static class ByXOrder implements Comparator<Point> {
        /**
         * Compare the points by the x coordinate
         * If two points have the same x, the point with the bigger y is "smaller"
         * Determine whether the first point is "greater" than the second point
         * @param o1 the first point
         * @param o2 the second point
         * @return 1 if the first point is greater
         *         -1 if the second point is greater
         *         0 if the points are equal(not possible in convex hull visualizer)
         */
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.getX() > o2.getX()) return 1;
            else if(o1.getX() < o2.getX()) return -1;
            else if (o1.getY() > o2.getY()) return 1;
            else if(o1.getY() < o2.getY()) return -1;
            return 0;
        }
    }

    /**
     * compares whether the two points have the same x and y
     * @param o the other point
     * @return true if the two points are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;
    }

    /**
     * Generate the hash code of the object using the x and y coordinate
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * string format is as follow:
     *      "Point {x, y}"
     * @return the object in string format
     */
    @Override
    public String toString() {
        return "new Point (" + x +
                ", " + y +
                "), ";
    }

    /**
     * Using the cross products of the vectors between v1 v2 and v1 v3
     * We can determine the direction of the second point going to the third point
     * @param pt1 the first point
     * @param pt2 the second point
     * @param pt3 the third point
     * @return 0 if pt2 and pt3 are collinear,
     *      positive if pt2 and pt3 is a left turn,
     *      negative if pt2 and pt3 is a right turn
     */
    public static int direction(Point pt1, Point pt2, Point pt3) {
        int firstVal = pt2.getX()-pt1.getX(); // (x2-x1)
        int secondVal = pt3.getY()-pt1.getY(); // (y3-y1)
        int thirdVal = pt3.getX()-pt1.getX(); // (x3-x1)
        int fourthVal = pt2.getY()-pt1.getY(); // (y2-y1)
        return (firstVal*secondVal) - (thirdVal*fourthVal);
    }

    /**
     * calculate the euclidean distance without taking the square root of it
     *      Reason for not square root: square root is too expensive and unnecessary
     * @param pt1 the first point
     * @param pt2 the second point
     * @return the squared distance between the two points
     */
    public static int distance(Point pt1, Point pt2) {
        int x = pt1.getX()-pt2.getX();
        int y = pt1.getY()-pt2.getY();
        return x*x + y*y;
    }

}