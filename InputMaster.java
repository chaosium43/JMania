import java.awt.event.*;
import java.util.*;

public class InputMaster implements KeyListener {
    public InputMaster() {}
    public HashSet<Integer> pressedKeys = new HashSet<Integer>(); //stores what keys are currently pressed

    //currently redundant for the purposes of this class
    public void keyTyped(KeyEvent e) {}

    //add a key to the currently pressed set
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());
        pressedKeys.add(e.getKeyCode());
    }

    //remove a key to the currently pressed set
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }
}
