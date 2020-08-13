import java.util.*;

import setup.*;
import algorithms.*;
import setup.Stack;

// enable assertion when running this class
public class Test {
    public static void main(String[] args) {
        testAll();
        try {
            assert false : "All Test Passed and assertion is enabled";
            System.out.println("Assertion is not enabled, none of the tests ran");
        }
        catch (AssertionError e) {
            System.out.println("All Test Passed and assertion is enabled");
        }
    }

    private static void testAll() {
        TestPoint.testGetter();
        TestPoint.testNaturalCompare();
        TestPoint.testEqual();
        TestPoint.testSlopeTo();
        TestPoint.testBySlope();
        TestPoint.testByXOrder();
        TestSort.testHeap();
        TestSort.testNull();
        TestStack.testPtStack();
        TestStack.testStack();
        TestLine.testLine();
        TestLine.testBySide();
        TestLine.testNegativeSlope();
        TestCircularPoint.testCircularPoint();
        TestMedian.testMedian();
        TestConvex.testConvex();
    }

    private static class TestConvex {
        public static void testConvex() {
                HashSet<Point> pts = new HashSet<>();
                int num = (int) (Math.random() * 5) + 50;
                for (int i = 0; i < num; i++) {
                    int x = (int) (Math.random() * 500);
                    int y = (int) (Math.random() * 500);
                    pts.add(new Point(x, y));
                }
                Point[] insertPts = new Point[pts.size()];
                insertPts = pts.toArray(insertPts);
                // for (Point pt : insertPts) System.out.print(pt);

                Point[] copy;
                copy = insertPts.clone();
                QuickHull quickHull = new QuickHull(copy);
                Point[] quick = quickHull.getResult();
                HeapSort.sort(quick, Comparator.naturalOrder());

                copy = insertPts.clone();
                JarvisMarch jarvisMarch = new JarvisMarch(copy);
                Point[] jarvis = jarvisMarch.getResult();
                HeapSort.sort(jarvis, Comparator.naturalOrder());

                copy = insertPts.clone();
                GrahamScan grahamScan = new GrahamScan(copy);
                Point[] graham = grahamScan.getResult();
                HeapSort.sort(graham, Comparator.naturalOrder());

                 copy = insertPts.clone();
                 DivideNConquer divideNConquer = new DivideNConquer(copy);
                 Point[] divide = divideNConquer.getResult();
                 HeapSort.sort(divide, Comparator.naturalOrder());

                copy = insertPts.clone();
                MonotoneChain monotoneChain = new MonotoneChain(copy);
                Point[] monotone = monotoneChain.getResult();
                HeapSort.sort(monotone, Comparator.naturalOrder());

                copy = insertPts.clone();
                ChanAlgorithm chanAlgorithm = new ChanAlgorithm(copy);
                Point[] chan = chanAlgorithm.getResult();
                HeapSort.sort(chan, Comparator.naturalOrder());

                copy = insertPts.clone();
                MarriageBeforeConquest marriageBeforeConquest = new MarriageBeforeConquest(copy);
                Point[] marriage =marriageBeforeConquest.getResult();
                HeapSort.sort(marriage, Comparator.naturalOrder());

                assert Arrays.equals(jarvis, graham);
                assert Arrays.equals(graham, quick);
                assert Arrays.equals(quick, divide);
                assert Arrays.equals(divide, monotone);
                assert Arrays.equals(monotone, chan);


                StringBuilder result = new StringBuilder();
                for (Point pt : insertPts) result.append(pt);
            }
        }

    private static class TestPoint {
        public static void testGetter() {
            int x = (int) (Math.random() * 500);
            int y = (int) (Math.random() * 500);
            Point pt1 = new Point(x, y);
            assert pt1.getX() == x : x + " " + pt1.getX();
            assert pt1.getY() == y : y + " " + pt1.getY();
            Point pt = new Point(0, 0);
            assert pt.getX() == 0 : pt.getX();
            assert pt.getY() == 0 : pt.getY();
            assert pt.isRed() == false : pt.isRed();
            pt.setRed(true);
            assert pt.isRed() == true : pt.isRed();
            pt.setRed(false);
            assert pt.isRed() == false : pt.isRed();
        }
        public static void testNaturalCompare() {
            Point pt = new Point(200,200);
            int cmp;
            cmp = pt.compareTo(new Point(200,200)); // equal
            assert cmp == 0 : cmp;
            cmp = pt.compareTo(new Point(200,100)); // greater y than other y
            assert cmp == 1 : cmp;
            cmp = pt.compareTo(new Point(200, 400)); // smaller y than other y
            assert cmp == -1 : cmp;
            cmp = pt.compareTo(new Point(100, 200)); // greater x than other x
            assert cmp == 1 : cmp;
            cmp = pt.compareTo(new Point(400, 200)); // smaller x than other x
            assert cmp == -1 : cmp;
        }
        public static void testEqual() {
            for (int i = 0; i < 10; i++)
            {
                int x = (int) (Math.random()*500);
                int y = (int) (Math.random()*500);
                Point pt1 = new Point(x, y);
                Point pt2 = new Point(x, y);
                assert pt1.equals(pt2);
            }
            for (int i = 0; i < 10; i++)
            {
                int x = (int) (Math.random()*500);
                int y = (int) (Math.random()*500);
                Point pt1 = new Point(x, y);
                Point pt2 = new Point(y, x);
                assert !pt1.equals(pt2);
            }
        }
        public static void testSlopeTo() {
            double slope;
            Point pt = new Point(0,0);
            slope = pt.slopeTo(new Point(1,1)); // basic check quadrant1
            assert slope == 1 : slope;
            slope = pt.slopeTo(new Point(1,2)); // steeper slope
            assert slope == 2 : slope;
            slope = pt.slopeTo(new Point(-1,5)); // basic check quadrant2
            assert slope == -5: slope;
            slope = pt.slopeTo(new Point(0,4)); // check vertical line
            assert slope == Double.POSITIVE_INFINITY : slope;
            slope = pt.slopeTo(new Point(20,0)); // check horizontal line to right
            assert slope == 0 : slope;
            slope = pt.slopeTo(new Point(-10, 0)); // check horizontal line to left
            assert slope == 0: slope;
            pt = new Point(460, 25); // random new point
            slope = pt.slopeTo(new Point(226, 378)); // random other point
            assert slope == -353.0/234 : slope;
            // don't need to check when two points are equal, impossible as hashset prevents that
        }
        public static void testBySlope() {
            Point pt = new Point(0,0);
            Comparator<Point> bySlope = pt.BYSLOPE;
            int cmp;
            cmp = bySlope.compare(new Point(1,1), new Point(1,2)); // basic check positive
            assert cmp == -1 : cmp;
            cmp = bySlope.compare(new Point(1,2), new Point(1,1)); // check that the opposite works
            assert cmp == 1: cmp;
            cmp = bySlope.compare(new Point(-1, 1), new Point(-2, 1)); // basic check negative
            assert cmp == -1 : cmp;
            cmp = bySlope.compare(new Point(-2,1), new Point(-1,1)); // check that the opposite works
            assert cmp == 1: cmp;
            cmp = bySlope.compare(new Point(1,1), new Point(2,2));  // check when two points have equal slope
            assert cmp == -1 : cmp;
            cmp = bySlope.compare(new Point(7,7), new Point(-20,0));
            assert cmp == -1 : cmp;
            cmp = bySlope.compare(new Point(2,0), new Point(-4,0)); // check when it makes a horizontal line but one on left one on right
            assert cmp == -1 : cmp;
            cmp = bySlope.compare(new Point(1,0), new Point(2,0)); // check when both points appear right of the horizontal line
            assert cmp == -1: cmp;
            cmp = bySlope.compare (new Point(0,4), new Point(-1,1)); // check vertical line against negative slope
            assert cmp == -1: cmp;
            cmp = bySlope.compare(new Point(1,1), new Point(0,4)); // check vertical line aginst positive slope
            assert cmp == -1 : cmp;
            cmp = bySlope.compare(new Point(4,0), new Point(0,4)); // check horizontal line against verticla line
            assert cmp == -1 : cmp;

            Point[] pts = {new Point(10,0), new Point(7,7), new Point(87, 174),
                new Point(0, 394), new Point(-12, 24), new Point(-191, 191),
                new Point(-242, 0)}; // check with Array sorting
            Point[] answer = pts.clone();

            List<Point> intList = Arrays.asList(pts); // shuffle the array
            Collections.shuffle(intList);
            intList.toArray(pts);

            Arrays.sort(pts, bySlope);
            assert Arrays.equals(answer, pts);
            // don't need to check when two points are equal, impossible as hashset prevents that
        }
        public static void testByXOrder() {
            Comparator comparator = Point.BYXORDER;
            int cmp;

            cmp = comparator.compare(new Point(0,13), new Point(1,0));
            assert cmp < 0 : cmp;
            cmp = comparator.compare(new Point(5,0), new Point(1,2));
            assert cmp > 0 : cmp;
            cmp = comparator.compare(new Point(2, 1), new Point(2, 5));
            assert cmp < 0: cmp;
            cmp = comparator.compare(new Point (5, 13), new Point(5, 1));
            assert cmp > 0: cmp;

            Point[] pts = {new Point(3,3), new Point(1,0), new Point(7,0), new Point(4,3 ),
                new Point(10,0), new Point(9,6), new Point(0,10), new Point(7,3), new Point(2,10),
                new Point(10,2), new Point(1,7), new Point(10,6)};
            HeapSort.sort(pts, comparator);
            Point[] answer = {new Point(0,10), new Point(1,0), new Point(1,7),
                    new Point(2,10),new Point(3,3), new Point(4,3 ),new Point(7,0), new Point(7,3),
                    new Point(9,6), new Point(10,0),new Point(10,2), new Point(10,6)};
            assert Arrays.equals(pts, answer);
        }
    }

    private static class TestSort {
        public static void testHeap() {
            Point pt = new Point(0,0);
            Comparator<Point> bySlope = pt.BYSLOPE;

            Point[] pts = {new Point(0,0), new Point(10,0), new Point(7,7), new Point(87, 174),
                    new Point(0, 394), new Point(-12, 24), new Point(-191, 191),
                    new Point(-242, 0)};
            Point[] answer = pts.clone();

            List<Point> intList = Arrays.asList(pts); // shuffle the array
            Collections.shuffle(intList);
            intList.toArray(pts);

            HeapSort.sort(pts, bySlope);
            assert Arrays.equals(answer, pts);
        }
        public static void testNull() {
            Point pt = new Point(0,0);
            Comparator<Point> bySlope = pt.BYSLOPE;
            // the array contains null elements at the end but should be ignored
            Point[] pts = {  new Point(87, 174), new Point(-12, 24), new Point(-191, 191),
                    new Point(7,7), new Point(0, 394), new Point(10,0), new Point(-242, 0),
                     null, null, null, null, null};
            Point[] answer = { new Point(10,0), new Point(7,7), new Point(87, 174),
                    new Point(0, 394), new Point(-12, 24), new Point(-191, 191),
                    new Point(-242, 0), null, null, null, null, null};
            HeapSort.sort(pts, bySlope,7 );
            assert Arrays.equals(answer, pts);
        }

    }

    private static class TestStack {
        public static void testPtStack() {
            PointStack stack = new PointStack();
            stack.push(new Point(100,100));
            Point tempPt = stack.pop(); // try popping when there is only one element left
            assert tempPt.equals(new Point(100,100));
            assert stack.isEmpty();
            assert stack.size() == 0: stack.size();
            for (int i=0; i< 10; i++)
                stack.push(new Point(i, i));
            assert !stack.isEmpty();
            assert stack.size() == 10: stack.size();
            tempPt = stack.pop(); // try popping with many elements left
            assert tempPt.equals(new Point(9,9));
            tempPt = stack.pop(); // try popping twice in a row
            assert tempPt.equals(new Point(8,8));
            Point[] answer = {new Point(0,0), new Point(1,1),
                    new Point(2,2), new Point(3,3), new Point(4,4),
                    new Point(5,5), new Point(6,6), new Point(7,7)};
            Point[] twoPtAnswer = {new Point(7,7), new Point(6,6)};
            assert Arrays.equals(stack.lastTwo(), twoPtAnswer );
            assert Arrays.equals(answer, stack.getConvex());
            stack.clearStack(); // check if clear stack works
            assert stack.isEmpty();
            assert stack.size() == 0 : stack.size();
        }
        public static void testStack() {
            Stack<Integer> stack = new Stack<>();
            stack.push(30);
            Integer tempInt = stack.pop(); // try popping when there is only one element
            assert tempInt.equals(30);
            assert stack.isEmpty();
            assert stack.size()==0 : stack.size();
            for (int i = 0; i < 10; i++)
                stack.push(i);
            assert !stack.isEmpty();
            assert stack.size() == 10: stack.size();
            tempInt = stack.pop(); // try popping with many elements left
            assert tempInt.equals(9): tempInt;
            tempInt = stack.pop();
            assert tempInt.equals(8); // try popping twice in a row
            Integer[] nums = new Integer[8];
            int i =0;
            for (Integer num: stack) nums[i++] = num;
            Integer[] answer = {7,6,5,4,3,2,1, 0};
            assert Arrays.equals(nums, answer);
        }
    }

    private static class TestLine {
        public static void testLine() {
            Point pt1 = new Point(2,2);
            Point pt2 = new Point(4,2);
            Line line = new Line(pt1, pt2);
            boolean temp = line.isEndpoint(new Point(1,1));
            assert !temp;
            temp = line.isEndpoint(new Point(2,2));
            assert temp;
            int val = line.findSide(new Point(0,1));
            assert val > 0 : val;
            val = line.findSide(new Point(2,5));
            assert val < 0 : val;
            val = line.findSide(new Point(3,2));
            assert val == 0: val;

            pt1 = new Point(0,0);
            pt2 = new Point(5,5);
            line = new Line(pt1, pt2); // not horizontal line
            val = line.findSide(new Point(2,1));
            assert val > 0 : val;
            val = line.findSide(new Point(2,4));
            assert val < 0 : val;
            val = line.findSide(new Point(3,3));
            assert val == 0: val;
        }
        public static void testBySide() {
            Line line = new Line (new Point(0,0), new Point(10,10));
            Point pt1 = new Point(4,3);
            Point pt2 = new Point(7, 4);
            Comparator bySide = line.BYSIDE;
            int cmp = bySide.compare(pt1, pt2); // both points on right of line
            assert cmp == 0: cmp;
            pt1 = new Point(5,6);
            pt2 = new Point (7,10);
            cmp = bySide.compare(pt1, pt2); // both points on left of the line
            assert cmp == 0: cmp;
            pt1 = new Point(2,2);
            pt2 = new Point(5,5);
            cmp = bySide.compare(pt1,pt2); // both points on the line
            assert cmp == 0: cmp;
            pt1 = new Point(5,5);
            pt2 = new Point(4,2);
            cmp = bySide.compare(pt1, pt2); // one point on the line, one on right
            assert cmp < 0: cmp;
            cmp = bySide.compare(pt2, pt1);
            assert cmp > 0: cmp;
            pt1 = new Point(4,4);
            pt2 = new Point(4, 7);
            cmp = bySide.compare(pt1, pt2); // one point on the line, one point on the left of line
            assert cmp < 0: cmp;
            cmp = bySide.compare(pt2, pt1);
            assert cmp > 0 : cmp;
            pt1 = new Point(0,0);
            pt2 = new Point(10,10);
            cmp = bySide.compare(pt1, pt2); // both points are endpoints
            assert cmp < 0: cmp;
            cmp = bySide.compare(pt2, pt1);
            assert cmp > 0: cmp;
            pt1 = new Point(0, 10);
            pt2 = new Point (20, 2);
            cmp = bySide.compare(pt1, pt2);
            assert cmp < 0: cmp;
            cmp = bySide.compare(pt2,pt1);
            assert cmp > 0: cmp;

            Point[] points = {new Point(8, 0), new Point(0,0), new Point(3, 10), new Point(6,6), new Point(2, 8),
                    new Point(2, 10), new Point(3,3), new Point(4, 0), new Point(10,10), new Point(1, 10),
                    new Point(0,6), new Point(9, 10), new Point(2,1), new Point(6,3), new Point(7,5)};
            Point[] answer = {new Point(6,6), new Point(3,3), new Point(9, 10), new Point(2, 8), new Point(3, 10),
                new Point(2, 10), new Point(0,6), new Point(1,10), new Point(0,0), new Point(10,10),
                new Point(6,3), new Point(2,1), new Point(4, 0), new Point(7,5), new Point(8,0)};
            HeapSort.sort(points, bySide);
            assert Arrays.equals(points, answer);
        }
        public static void testNegativeSlope() {
            Line line = new Line(new Point(0,10), new Point(10,0));
            int val = line.findSide(new Point(0,0));
            assert val > 0: val;
        }
    }

    private static class TestCircularPoint {
        public static void testCircularPoint() {
            PointCircular node1 = new PointCircular(new Point(0,0));
            PointCircular node2 = new PointCircular(new Point(1,1));
            PointCircular node3 = new PointCircular(new Point(2,0));

            assert node1.getCounterCW() == node1 : node1.getCounterCW();
            assert node1.getCW() == node1 : node1.getCW();
            assert node1.getPoint().equals(new Point(0,0)): node1.getPoint();
            StringBuilder result = new StringBuilder();
            for (PointCircular temp: node1)
                result.append(temp.getPoint());
            String answer = node1.getPoint().toString();
            assert result.toString().equals(answer): result.toString();

            node1.setCW(node2);
            assert node1.getCW() == node2 : node1.getCW();
            assert node2.getCounterCW() == node1 : node2.getCounterCW();
            node2.setCW(node1);
            assert node1.getCounterCW() == node2 : node1.getCounterCW();
            assert node2.getCW() == node1 : node1.getCounterCW();

            node3.setCW(node1);
            node3.setCounterCW(node2);
            assert node1.getCounterCW() == node3 : node1.getCounterCW();
            assert node2.getCW() == node3 : node2.getCW();
            result = new StringBuilder();
            for (PointCircular temp: node1)
                result.append(temp.getPoint());
            answer = node1.getPoint().toString() + node2.getPoint().toString() + node3.getPoint().toString();
            assert result.toString().equals(answer) : result.toString();
        }
    }

    private static class TestMedian {
        public static void testMedian() {
            Median median = new Median();
            Integer[] nums = new Integer[(int) (Math.random()*10)+40];
            for (int i = 0; i < nums.length; i++)
                nums[i] = (int) (Math.random()*100);
            Integer result1 = (Integer) median.median(nums, Comparator.naturalOrder());
            Arrays.sort(nums);
            int result2 = nums[nums.length/2];
            assert result1 == result2;
        }
    }

}
