import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;

//custom jpanel which streamlines the rendering of all gui elements
//luna engine will also ensure all buttons function properly
public class LunaEngine extends JPanel implements Runnable, MouseListener {
    //stats required for rendering the panel
    public static int fps = 60;
    public static Graphics gr;

    //mouse listener stats
    public static int xPos = 0;
    public static int yPos = 0;

    //helps when sorting the gui elements by display order
    public static class cmp implements Comparator<GuiElement.FrameElement> {
        public int compare(GuiElement.FrameElement a, GuiElement.FrameElement b) {
            return a.displayOrder - b.displayOrder;
        }
    }

    //do basic window things and get the thread running
    public LunaEngine() {
        setPreferredSize(new Dimension(500, 500));
        setBackground(new Color(64, 64, 64));
        addMouseListener(this);
        this.setFocusable(true);
        Thread thread = new Thread(this);
        thread.start();
    }

    //renders all the gui elements
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gr = g;
        ArrayList<GuiElement.FrameElement> a = new ArrayList<>();
        for (GuiElement.FrameElement e: GuiElement.gui) {
            a.add(e);
        }
    
        a.sort(new cmp());
        for (GuiElement.FrameElement e: a) {
            e.render(g);
        }
    }

    //helper method for checking the boundaries of a mouse button click
    public static boolean boundHelper(double min, double max, double value) {
        return value >= min && value <= max;
    }

    //draws the screen fps times a second
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 / fps);
            } catch (Exception e) {}
            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {}

    //check all buttons to see which one was clicked
    public void mousePressed(MouseEvent e) {
        xPos = e.getX();
        yPos = e.getY();

        //going through each type of button
        for (GuiElement.TextButton button: GuiElement.textButtons) {
            //checking if instance is visible
            if (button.visible) {
                double[] aPos = button.absolutePosition(gr);
                double[] aSize = button.absoluteSize(gr);
                if (boundHelper(0, aSize[0], xPos - aPos[0]) && boundHelper(0, aSize[1], yPos - aPos[1])) {
                    button.onClick();
                    return;
                }
            }
        }

        for (GuiElement.ImageButton button: GuiElement.imageButtons) {
            //checking if instance is visible
            if (button.visible) {
                double[] aPos = button.absolutePosition(gr);
                double[] aSize = button.absoluteSize(gr);
                if (boundHelper(0, aSize[0], xPos - aPos[0]) && boundHelper(0, aSize[1], yPos - aPos[1])) {
                    button.onClick();
                    return;
                }
            }
        }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
