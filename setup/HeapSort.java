/*
  Chose to use heapsort because it is the most optimized (guarantees nlogn performance and offers in place sort)
 */

package setup;

import java.util.Comparator;

// inspired by Algorithms course from Princeton
public class HeapSort {
    /**
     * Sorts the points using heapsort
     * Guarantees nlogn performance and in place sorting
     * Is not stable
     * @param points Array of points to be sorted
     * @param comparator the way to sort/order the array
     */
    public static void sort(Point[] points, Comparator<Point> comparator)
    {
        int length = points.length;
        sortRange(points, comparator,0, length);
    }

    /**
     * Sort the points using heapsort
     * Only sort up the specified size, the rest is ignored, could put null elements there
     * Is not stable
     * @param points Array of points to be sorted
     * @param comparator the way to sort/order the array
     * @param size how many elements to sort
     */
    public static void sort(Point[] points, Comparator<Point> comparator, int size)
    {
        sortRange(points, comparator, 0, size);
    }

    /**
     * Sort an array from the starting Index up to the ending Index
     * @param points the points to be sorted
     * @param comparator teh way to sort/order the array
     * @param startInd the index to start the sorting
     * @param endInd the index to sort up to
     */
    public static void sortRange(Point[] points, Comparator<Point> comparator, int startInd, int endInd) {
        // build max-heap
        int length = endInd - startInd;
        int offset = startInd -1;
        for (int i=length/2; i >= 1; i--)
        {
            sink(points, i, length, comparator, offset);
        }

        while (length > 0 )
        {
            exchange(points, 1, length, offset);
            sink (points, 1, --length, comparator, offset);
        }
    }

    /**
     * Used to maintain the max heap
     * @param points Array of points to work with
     * @param i the item currently on
     * @param length the length of the unsorted array
     * @param comparator the way to sort/order the array
     * @param offset how much the index should be offset by
     */
    private static void sink(Point[] points, int i, int length, Comparator<Point> comparator, int offset)
    {
        while (2*i <= length)
        {
            int child = 2*i;
            if (child < length && less(points, child, child+1, comparator, offset)) //checks which of the two child is bigger
                child++; // set child to the other child if the other child is greater
            if (less(points, i, child, comparator, offset))
                exchange(points, i , child, offset);
            else
                break;
            i = child;
        }
    }

    /**
     * Checks if point1 is less than point2
     * -1 from the index to offset the 1-index heapsort idea
     * @param points Array of points to work with
     * @param point1 the index of the first point
     * @param point2 the index of the second point
     * @param comparator the way to sort/order the array
     * @param offset how much the index should be offset by
     * @return true if point1 is less than point2
     */
    private static boolean less(Point[] points, int point1, int point2, Comparator<Point> comparator, int offset)
    {
        return comparator.compare(points[point1+offset],points[point2+offset]) < 0 ;
    }

    /**
     * exchange the two points in the array
     * -1 from the index to offset the 1-index heapsort idea
     * @param points array of points to work with
     * @param point1 the index of the first point
     * @param point2 the index of the second point
     */
    public static void exchange(Point[] points, int point1, int point2, int offset)
    {
        Point temp = points[point1+offset];
        points[point1+offset] = points[point2+offset];
        points[point2+offset] = temp;
    }
}