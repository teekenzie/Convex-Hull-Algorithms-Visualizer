package userinterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Objects;

import algorithms.*;
import setup.Point;

/**
 * Creates the Graphical User Interface that allows the user to interact with
 */

public class Visualizer extends JFrame {
    public static void main(String[] args) {
        new Visualizer();
    }

    JButton visualize, random, reset;
    JComboBox<String> options;
    AnimationArea animationArea;
    HashSet<Point> points;
    Timer timer;

    public Visualizer() {
        JPanel thePanel = new JPanel();
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        thePanel.setLayout(null);
        ListenForButton lForButton = new ListenForButton();
        this.setTitle("Convex Hull Algorithms Visualizer");

        visualize = new JButton("Visualize");
        thePanel.add(visualize);
        visualize.setBounds(300, 10, 100, 50);
        visualize.addActionListener(lForButton);

        String[] choices = { "Chan's Algorithm","Marriage before Conquest", "Incremental", "Monotone Chain", "Divide and Conquer" ,
                "Quick Hull" , "Graham Scan",  "Jarvis March"};
        options = new JComboBox<>(choices);
        thePanel.add(options);
        options.setBounds(105, 10, 180, 50);

        random = new JButton("Random Points");
        thePanel.add(random);
        random.setBounds(415,10,150,50);
        random.addActionListener(lForButton);

        reset = new JButton("Clear");
        thePanel.add(reset);
        reset.setBounds(15, 10, 75, 50);
        reset.addActionListener(lForButton);

        points = new HashSet<>();
        animationArea = new AnimationArea(points);
        thePanel.add(animationArea);
        animationArea.setBounds(-10, 65, animationArea.getCanvasSize(), animationArea.getCanvasSize());

        this.add(thePanel);
        this.setVisible(true);
    }

    private void errorMessage(String message) { JOptionPane.showMessageDialog(this, message, "ERROR", JOptionPane.ERROR_MESSAGE); }

    private class ListenForButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == reset) {
                points = new HashSet<>();
                animationArea.reset(points);
                reset.setBounds(15, 10, 75, 50);
                visualize.setVisible(true);
                options.setVisible(true);
                random.setVisible(true);
                visualize.setEnabled(true);
                options.setEnabled(true);
                random.setEnabled(true);
                reset.setText("Clear");
            }
            else if (e.getSource() == random) {
                points = new HashSet<>();
                animationArea.random(points);
            }
            else if( e.getSource()== visualize)
            {
                if (points.size() < 3) {
                    errorMessage("At least 3 points is needed");
                    return;
                }
                ConvexHullAlgorithm algorithm;
                String userChoice = (String) options.getSelectedItem();

                // convert from hashset to array
                Point[] pts = new Point[points.size()];
                pts = points.toArray(pts);
                points = null; // get rid of the hashset points since we already have the array
                animationArea.startAnimation(); // get rid of the hashset and disable clicking

                visualize.setEnabled(false); // disable the button once the animation starts
                options.setEnabled(false);
                random.setEnabled(false);
                reset.setEnabled(false);

                switch (Objects.requireNonNull(userChoice)) {
                    case "Marriage before Conquest":
                        algorithm = new MarriageBeforeConquest(pts);
                        break;
                    case "Chan's Algorithm":
                        algorithm = new ChanAlgorithm(pts);
                        break;
                    case "Incremental":
                        algorithm = new Incremental(pts);
                        break;
                    case "Monotone Chain":
                        algorithm = new MonotoneChain(pts);
                        break;
                    case "Divide and Conquer":
                        algorithm = new DivideNConquer(pts);
                        break;
                    case "Quick Hull":
                        algorithm = new QuickHull(pts);
                        break;
                    case "Graham Scan":
                        algorithm = new GrahamScan(pts);
                        break;
                    case "Jarvis March":
                        algorithm = new JarvisMarch(pts);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + Objects.requireNonNull(userChoice));
                }
                timer = new Timer(algorithm.getTime(), actionEvent -> runAnimation(algorithm));
                timer.start();
            }
        }
    }

    private void runAnimation(ConvexHullAlgorithm algorithm)
    {
        assert algorithm != null : "Algorithm shouldn't be null";
        animationArea.clear();
        algorithm.draw(animationArea);
        animationArea.display();
        if (algorithm.isComplete())
        {
            timer.stop();
            reset.setText("Reset");
            reset.setBounds(35, 10, 510, 50);
            reset.setEnabled(true);
            visualize.setVisible(false);
            options.setVisible(false);
            random.setVisible(false);
        }
    }
}
