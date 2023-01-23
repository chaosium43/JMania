//creates the track object which will 
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class RhythmTrack extends GuiElement.FrameElement {
    public int playerId;
    public int score = 0;
    public int[] keybinds;
    public boolean[] keybindCache = new boolean[4]; //stores whether a note was held down for the previous cycle
    public boolean[] holderCache = new boolean[4];
    public ArrayList<ArrayList<NoteData>> currentTrack = new ArrayList<ArrayList<NoteData>>();
    public ArrayList<Integer> backlog = new ArrayList<Integer>();
    public HashSet<Integer> pressedKeys = new HashSet<>();
    public int gameTime = 0;
    public Color noteColor = new Color(255, 0, 0);
    private int scoreIndicatorBuffer = 0;
    private int scoreIndicatorCache = 0;

    //stats related to the game
    public int perfect = 0;
    public int great = 0;
    public int okay = 0;
    public int miss = 0;
    public int combo = 0;
    public int maxCombo = 0;
    public int noteSpeed = 500;
    public int rawAccuracy = 0;
    public int maxAccuracy = 0;
    public int lastScore = 0;

    public RhythmTrack(int pid, int[] kb) {
        playerId = pid;
        keybinds = kb;
        
        for (int i = 0; i < 4; i++) {
            backlog.add(0);
            currentTrack.add(new ArrayList<NoteData>());
        }
    }

    public void loadMap(ArrayList<ArrayList<NoteData>> track) {
        for (int i = 0; i < 4; i++) {
            backlog.set(i, 0);
        }
        currentTrack = track;

        perfect = 0;
        great = 0;
        okay = 0;
        miss = 0;
        combo = 0;
        maxCombo = 0;
        rawAccuracy = 0;
        maxAccuracy = 0;
        gameTime = 0;
        score = 0;
    }

    //draws all the notes on the screen
    public void render(Graphics g) {
        super.render(g);
        int scoring = scoringAlgorithm();

        if (scoring == 0) {
            scoreIndicatorBuffer--;
            if (scoreIndicatorBuffer > 0) {
                scoring = scoreIndicatorCache;
            }
        } else {
            scoreIndicatorBuffer = 10;
            scoreIndicatorCache = scoring;
        }

        if (visible) {
            try {
                g.setColor(noteColor);
                //getting the positioning data for the game
                double[] aSize = super.absoluteSize(g);
                double[] aPos = super.absolutePosition(g);
                double normalSize = (aSize[0] / 4);
                double reserve = aSize[1] - 25;


                for (int i = 0; i < 4; i++) {
                    int counter = backlog.get(i); //current backlog for a track
                    int xPos = (int)(aPos[0] + normalSize * i);
                    ArrayList<NoteData> track = currentTrack.get(i);
                    while (counter != track.size()) {
                        NoteData note = track.get(counter);
                        int screenRelative = note.position - gameTime;
                        if (screenRelative > noteSpeed) { //if the note is offscreen already, stop rendering notes on the following track
                            break;
                        }

                        if (note.holdTime == 0) { //rendering a regular note
                            g.fillRect(xPos, (int)(aPos[1] + reserve * Math.min(1 - (double)screenRelative / noteSpeed, 1.0)), (int)normalSize, 25);
                        } else { //rendering a hold note
                            double renderMinimum = (1 - (screenRelative + note.holdTime) / (double)noteSpeed) * reserve;

                            //handle size occlusion
                            double renderMaximum = Math.min(note.holdTime / (double)noteSpeed * reserve + 25 + Math.min(renderMinimum, 0), aSize[1]);
                            double offscreening = renderMinimum + renderMaximum - aSize[1];
                            if (offscreening > 0) {
                                renderMaximum -= offscreening;
                            }
                            g.fillRect(xPos, (int)(aPos[1] + Math.max(renderMinimum, 0)), (int)normalSize, (int)(renderMaximum));
                        }
                        
                        counter++;
                    }
                }
                
                g.setColor(new Color(255, 255, 255));
                g.fillRect((int)aPos[0], (int)(aPos[1] + reserve), (int)aSize[0], 25);


                //draw a different coloured circle based on the score
                switch (scoring) {
                    case 1:
                    g.setColor(new Color(255, 0, 0));
                    break;
                    case 2:
                    g.setColor(new Color(255, 255, 0));
                    break;
                    case 3:
                    g.setColor(new Color(0, 255, 0));
                    break;
                    case 4:
                    g.setColor(new Color(0, 0, 255));
                    break;
                }

                if (scoreIndicatorBuffer > 0) {
                    g.fillOval((int)(aPos[0] + aSize[0] / 2 - 10), (int)(aPos[1] + 40), 20, 20);
                }
            } catch (Exception e) {
                System.out.printf("RENDER ERROR: %s\n", e.getMessage());
            }
        }
    }

    //handles note scoring yada yada
    public int scoringAlgorithm() {
        //perform the function for each track
        int scoreReturn = 0;
        for (int i = 0; i < 4; i++) {
            int noteIdx = backlog.get(i);
            boolean keyPressed = pressedKeys.contains(keybinds[i]); //check if a key is pressed
            ArrayList<NoteData> selectedTrack = currentTrack.get(i);

            if (noteIdx < selectedTrack.size()) {
                NoteData note = selectedTrack.get(noteIdx); //current note to be considered
                int noteDistance = note.holdTime + note.position - gameTime;
                
                //if the note goes way offscreen, count it as a miss
                if (noteDistance <= -200) {
                    maxCombo = Math.max(combo, maxCombo);
                    miss++;
                    combo = 0;
                    backlog.set(i, backlog.get(i) + 1);
                    holderCache[i] = false;
                    maxAccuracy += 4;
                    scoreReturn = 1;
                }
                //if the note is on screen, do the due dilligence of checking things related to it
                else if (!keyPressed && holderCache[i]) { //consider when a held note has been released
                    scoreReturn = scoreNote(Math.abs(noteDistance));
                    holderCache[i] = false;
                    //System.out.println(Math.abs(note.position + note.holdTime - gameTime));
                    backlog.set(i, backlog.get(i) + 1);
                } else if (noteDistance - note.holdTime <= noteSpeed) {
                    if (keyPressed && !keybindCache[i]) {//key pressed
                        //System.out.println(Math.abs(noteDistance));
                        scoreReturn = scoreNote(Math.abs(noteDistance - note.holdTime));

                        if (note.holdTime == 0) {
                            backlog.set(i, backlog.get(i) + 1); //note has already been considered if it isn't a held note
                        } else {
                            holderCache[i] = true;
                        }
                    }
                }
                //System.out.println(score);
            }

            keybindCache[i] = keyPressed;
        }
        return scoreReturn;
    }

    //returns the current multiplier based on the combo
    public double getComboMultiplier() {
        if (combo < 25) {
            return 1;
        } else if (combo < 50) {
            return 1.25;
        } else if (combo < 75) {
            return 1.5;
        } else if (combo < 100) {
            return 1.75;
        }
        return 2;
    }

    //simple helper method to calculate current accuracy of a play
    public double getAccuracy() {
        if (maxAccuracy > 0) {
            return Math.round((double)(rawAccuracy * 10000) / maxAccuracy) / 100.0;
        }
        return 0;
    }



    //helper method to avoid re-using note scoring code
    public int scoreNote(int absTiming) {
        int noteScore = 0;
        int noteReturn = 0;
        if (absTiming <= 60) {
            perfect++;
            combo++;
            rawAccuracy += 4;
            noteScore = 200;
            noteReturn = 4;
        } else if (absTiming <= 150) {
            great++;
            combo++;
            rawAccuracy += 3;
            noteScore = 100;
            noteReturn = 3;
        } else if (absTiming < 200) {
            okay++;
            rawAccuracy += 2;
            noteScore = 50;
            noteReturn = 2;
        } else {
            miss++;
            noteReturn = 1;
        }

        maxCombo = Math.max(combo, maxCombo);
        double comboMultiplier = getComboMultiplier();
        score += comboMultiplier * noteScore;
        maxAccuracy += 4;
        return noteReturn;
    }
}
