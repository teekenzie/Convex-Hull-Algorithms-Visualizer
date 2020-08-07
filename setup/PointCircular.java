package setup;

import userinterface.AnimationArea;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A circular linked list
 * Stores the point, the point at the clockwise direction of the point, and the point at the counterclockwise of the direction
 */
public class PointCircular implements Iterable<PointCircular> {
    private final Point point;
    private PointCircular cw; // the clockwise point/node of the point
    private PointCircular ccw; // the counterclockwise point/node of the point
    private final boolean collinearDisable; // used by DivideNConquer to deal with vertical collinear

    public PointCircular(Point point)
    {
        this.point = point;
        cw = this;
        ccw = this;
        collinearDisable = point.getColliearDisable();
    }

    /**
     * change the clockwise field of the node and the counterclockwise field of the other node
     * @param other the node that is on the clockwise of the point
     */
    public void setCW(PointCircular other) {
        if (collinearDisable || other.collinearDisable) return;
        cw = other;
        other.ccw = this;
    }

    /**
     * change the counter clockwise of the node and the clockwise field of the other node
     * @param other the node that is on the counterclockwise of the point
     */
    public void setCounterCW(PointCircular other) {
        if (collinearDisable || other.collinearDisable) return;
        ccw = other;
        other.cw = this;
    }

    /**
     * @return the point that this node stores
     */
    public Point getPoint() {
        return point;
    }

    /**
     * @return true if the point has been disabled
     */
    public boolean getCollinearDisable() {
        return collinearDisable;
    }

    /**
     * @return the node that is counter clockwise to the point
     */
    public PointCircular getCounterCW() {
        return ccw;
    }

    /**
     * @return the node that is clockwise to the point
     */
    public PointCircular getCW() {
        return cw;
    }

    /**
     * Connects the points to its neighbor (clockwise point and counterclockwise point)
     * @param animationArea the canvas to draw on
     */
    public void draw(AnimationArea animationArea) {
        PointCircular pointer = cw;
        PointCircular prev = this;
        prev.point.setRed(true);
        animationArea.drawPoint(prev.point);
        while (pointer != this)
        {
            pointer.point.setRed(true);
            animationArea.drawPoint(pointer.point);
            animationArea.drawLine(animationArea.BLACK, prev.point, pointer.point);
            prev = pointer;
            pointer = pointer.cw;
        }
        animationArea.drawLine(animationArea.BLACK, prev.point, point);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<PointCircular> iterator() {
        return new PointCircularIterator();
    }
    private class PointCircularIterator implements Iterator<PointCircular>{
        PointCircular current = PointCircular.this;
        boolean start = false;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return !start || current != PointCircular.this ;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public PointCircular next() {
            if (!hasNext()) throw new NoSuchElementException();
            start = true;
            PointCircular tempNode = current;
            current = current.cw;
            return tempNode;
        }
    }

    /**
     * Checks the equality of the points stored
     * @param o the other PointCircular
     * @return true if both objects have the same points
     *          otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointCircular that = (PointCircular) o;
        return Objects.equals(point, that.point);
    }

    /**
     * Generates the hash code using the point of this
     * @return a hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(point);
    }

    /**
     * Return in the form of "PointCircular{point=____}"
     * @return the string representation of this object
     */
    @Override
    public String toString() {
        return "PointCircular{" +
                "point=" + point +
                '}';
    }
}
