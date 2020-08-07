package userinterface;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import setup.Point;

public class AnimationArea extends Canvas implements MouseListener
{
    public final Color BLACK = Color.BLACK;
    public final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public final Color RED = Color.RED;
    public final Color GREEN = Color.GREEN;
    public final Color ORANGE = Color.ORANGE;
    public final Color BLUE = Color.BLUE;
    public final Color MAGENTA = Color.MAGENTA;
    public final Color CYAN = Color.CYAN;

    private boolean isAnimationMode; // to disable the mouse clicking
    private HashSet<Point> points;
    private final int canvasSize = 600;

    // for synchronization
    private final Object mouseLock = new Object();

    // double buffered graphics
    private final BufferedImage offscreenImage;
    private final Graphics2D offscreen ;

    public AnimationArea(HashSet<Point> points)
    {
        isAnimationMode = false;
        this.points = points;
        addMouseListener(this);
        offscreenImage = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();

        clear();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        paint(offscreen);
    }


    public void display() {
        repaint();
    }
    @Override
    public void paint(Graphics g) { g.drawImage(offscreenImage,0,0, null); }
    public void clear() {
        setPenColor(Color.WHITE);
        offscreen.fillRect(0,0, canvasSize, canvasSize);
    }
    public void setPenColor(Color penColor) { offscreen.setColor(penColor); }


    // draw Points and Lines
    public void drawPoint(Point pt)
    {
        // if the point is red set the pen color to red first, else set it to black
        if (pt.isRed()) setPenColor(RED);
        else setPenColor(BLACK);
        offscreen.fillOval(computerX(pt.getX())-2, computerY(pt.getY())-2, 5,5);
            // minus 3 so the center would be in the middle
        paint(offscreen);
    }
    public void drawLine(Color color, Point pt1, Point pt2)
    {
        setPenColor(color);
        offscreen.drawLine(computerX(pt1.getX()), computerY(pt1.getY()),
                computerX(pt2.getX()), computerY(pt2.getY()));
        paint(offscreen);
    }

    // user coordinate is where y is 0 at the bottom, computer coordinate is where y is 0 at the top
    // converts from computer coordinates to user coordinates
    public int userX(int x){ return x;}
    public int userY(int y) { return canvasSize - y; }
    // converts to user coordinates from computer coordinates
    public int computerX(int x) { return x; }
    public int computerY(int y) { return canvasSize - y;}
    public int getCanvasSize() { return canvasSize; }

    public void random(HashSet<Point> points) {
        this.points= points;
        clear();
        int range = canvasSize- 50;
        int numPts = (int) (Math.random()*50) + 75;
        for (int i = 0; i < numPts; i++)
        {
            int x = (int) (Math.random()*range) + 25;
            int y = (int) (Math.random()*range) + 25;
            Point tempPt = new Point(x, y);
            drawPoint(tempPt);
            points.add(tempPt);
        }
        display();
    }
    public void reset(HashSet<Point> points) {
        clear();
        isAnimationMode = false;
        this.points = points;
        display();
    }


    /**
     * Disable all the mouse clicking
     * Get rid of the hashset
     */
    public void startAnimation()
    {
        isAnimationMode = true;
        points = null;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // System.out.println(userX(e.getX())+ " " + userY(e.getY())); // for debugging purpose
        if (isAnimationMode) return;
        synchronized (mouseLock){
            Point tempPt = new Point(userX(e.getX()), userY(e.getY()));
            drawPoint(tempPt);
            points.add(tempPt);
            display();
            // System.out.println(tempPt.getX()+ " " + tempPt.getY()); // for debugging purpose
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        // intentionally left blank
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        // intentionally left blank
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        // intentionally left blank
    }
    @Override
    public void mouseExited(MouseEvent e) {
        // intentionally left blank
    }
}
