package setup;

import java.util.Comparator;

public class Line {

    public final Comparator<Point> BYSIDE = new BySide();
    public static final Comparator<Line> BYSLOPE = new BySlope();

    private final Point pt1;
    private final Point pt2;
    private final double slope;

    public Line(Point pt1, Point pt2)
    {
        if (pt1.getX() < pt2.getX()) {
            this.pt1 = pt1;
            this.pt2 = pt2;
        }
        else
            {
            this.pt1 = pt2;
            this.pt2 = pt1;
        }
        slope = pt1.slopeTo(pt2);
    }

    /**
     * This point should have the smaller x compare to the other point
     *
     * @return the first point of the line
     */
    public Point getFirstPt() {
        return pt1;
    }

    /**
     * This point should have greater x compare to the other point
     *
     * @return the second point of the line
     */
    public Point getSecondPt() {
        return pt2;
    }

    /**
     * Help findSide and approxLineDist to calculate the appropriate calculations
     * @param pt the point to be determined
     * @return the relationship between the point and the line
     */
    private int distSideHelper(Point pt) {
        int first = pt.getY() - pt1.getY();
        int second = pt2.getX() - pt1.getX();
        int third = pt2.getY() - pt1.getY();
        int fourth = pt.getX() - pt1.getX();
        return (first*second) - (third*fourth);
    }

    /**
     * @return the slope of the line
     */
    public double slope() {
        return slope;
    }

    /**
     * determines if the point is on the left side or on the right side of the line
     * @param pt the point to be determined
     * @return negative if the point is on the left side of the line
     *      positive if the point is on the right side of the line
     *      0 if the point is on the line
     */
    public int findSide(Point pt) {
        int temp = distSideHelper(pt);
        if (temp > 0) return -1;
        else if (temp < 0) return 1;
        return 0;
    }

    /**
     * The distance between the line and the point is not accurate but is proportional
     *      Inexpensive to calculate compare to the real formula
     * @param pt the point that decides the distance between itself and the line
     * @return the proportional line distance between the line and the point
     */
    public int approxLineDist (Point pt) {
        return Math.abs(distSideHelper(pt));
    }

    /**
     * Checks whether the given point is one of the endpoints that makes up the line
     *      return false if the point is just a point on the line
     * @param pt the point to be checked upon
     * @return true if the point is one of the endpoints
     */
    public boolean isEndpoint(Point pt) { return pt.equals(pt1) || pt.equals(pt2);}

    /**
     * Comparator that helps to sort the points based on which side of the line they are on
     */
    private class BySide implements Comparator<Point> {

        @Override
        public int compare(Point o1, Point o2) {
            int val1 = findSide(o1);
            int val2 = findSide(o2);
            if (val1 > 0 && val2 > 0) return 0;
            else if (val1 < 0 && val2 < 0) return 0;
            else if (val1 != 0 && val2 != 0) return Integer.compare(val1, val2);

            if ( val1 == 0 && val2 == 0)
            {
                if (isEndpoint(o1) && isEndpoint(o2))
                {
                    if (o1.equals(pt1)) return -1;
                    return 1;
                }
                else if(isEndpoint(o1)) return 1;
                else if(isEndpoint(o2)) return -1;
                else if (!isEndpoint(o1) && !isEndpoint(o2)) return 0;
            }
            if (val1 == 0) {
                if (!isEndpoint(o1)) return -1; // push the collinear points to the front of the array
                else if (val2 > 0) return -1;
                else // if (val2 < 0)
                    return 1;
            }
            // val2 is 0
            if (!isEndpoint(o2)) return 1;
            else if (val1 > 0) return 1;
            else // if (val1 < 0)
                return -1;
        }
    }

    /**
     * Comparator that helps to compare the slopes between the lines
     */
    private static class BySlope implements Comparator<Line> {

        /**
         * Compare the slope of the two lines
         * Determine whether line1 has a greater slope than the other line
         * @param line1 the first line
         * @param line2 the second line
         * @return -1 if the first line has a less slope than the second line
         *          1 if the first line has a greater slope than the second line
         *          0 if both lines have the same slope
         */
        @Override
        public int compare(Line line1, Line line2) {
            double slope1 = line1.slope();
            double slope2 = line2.slope();
            return Double.compare(slope1, slope2);
        }
    }

}
