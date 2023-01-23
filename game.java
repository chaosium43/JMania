import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Scanner;
//import java.util.ArrayList;
import java.awt.event.*;

//main game logic occurs in this class
public class game {
    //text file streaming/storage of persistent data handled here
    //convenient container for player stats
    public static class PlayerData {
        public int[] keybinds = new int[4];
        public String name;
    }

    //reading in all the settings from gameSettings.txt
    public static PlayerData[] settings = new PlayerData[2];

    //io to userdata/gameSettings.txt
    public static void readSettings() {
        try {
            Scanner s = new Scanner(new File("userdata/gameSettings.txt"));
            
            //header data for how many players there are
            int pc = Integer.parseInt(s.nextLine());

            //read data for each player
            for (int i = 0; i < pc; i++) {
                //keybinds
                PlayerData pdata = new PlayerData();
                for (int j = 0; j < 4; j++) {
                    pdata.keybinds[j] = Integer.parseInt(s.nextLine());
                }
                pdata.name = s.nextLine();
                settings[i] = pdata;
            }
            s.close();
        }
        catch (Exception e) {
            System.out.printf("ERROR READING PLAYER DATA: %s\n", e.getMessage());
        }
    }
    public static void writeSettings() {
        try {
            PrintWriter p = new PrintWriter(new FileWriter("userdata/gameSettings.txt"));
            p.println(settings.length);
            for (PlayerData pdata: settings) {
                for (int kc: pdata.keybinds) {
                    p.println(kc);
                }
                p.println(pdata.name);
            }
            p.close();
        } catch (Exception e) {
            System.out.printf("ERROR WRITING SETTINGS: %s\n", e.getMessage());
        }
    }
    //gameState related variables
    //G.S. 0 = Title Screen
    //G.S. 1 = Settings Screen
    //G.S. 2 = Map Screen
    //G.S. 3 = Game Screen
    //G.S. 4 = Outcome Screen
    public static int gameState = 0;
    public static GuiElement.FrameElement[] titleScreen = new GuiElement.FrameElement[5];
    public static GuiElement.FrameElement[] settingsScreen = new GuiElement.FrameElement[17];
    public static GuiElement.FrameElement[] mapScreen = new GuiElement.FrameElement[16];
    public static GuiElement.FrameElement[] gameScreen = new GuiElement.FrameElement[13];
    public static GuiElement.FrameElement[] outcomeScreen = new GuiElement.FrameElement[10];
    public static GuiElement.FrameElement[][] stateMaster = {titleScreen, settingsScreen, mapScreen, gameScreen, outcomeScreen};

    //image variables for images used in the game
    public static BufferedImage gameLogo;
    public static BufferedImage[] rankingImages = new BufferedImage[5];

    //game stats
    public static String currentMapName = "";

    //G.S. 1 externals
    public static bindButton[] playerBinders = new bindButton[8];
    public static bindButton currentBindButton;
    public static boolean bindSelected = false;
    
    //G.S. 2 externals
    public static GuiElement.TextElement[] leaderboardElements = new GuiElement.TextElement[5];

    //G.S. 3 externals
    public static String audioName;
    public static long delta;
    public static RhythmTrack player1Track;
    public static RhythmTrack player2Track;
    public static GuiElement.TextElement scorep1;
    public static GuiElement.TextElement scorep2;
    public static GuiElement.TextElement combop1;
    public static GuiElement.TextElement combop2;
    public static GuiElement.TextElement accp1;
    public static GuiElement.TextElement accp2;
    public static GuiElement.TextElement handleTitle3;
    public static int mapLength;

    //G.S. 4 externals
    public static GuiElement.TextElement handleTitle4;
    public static GuiElement.ImageElement rankingImage;
    public static GuiElement.TextElement scoreFrame;
    public static GuiElement.TextElement perfectFrame;
    public static GuiElement.TextElement greatFrame;
    public static GuiElement.TextElement okayFrame;
    public static GuiElement.TextElement missFrame;
    public static GuiElement.TextElement comboFrame;


    //functions which will initialize every gui
    public static void createTitleScreen() {
        //main logo
        GuiElement.ImageElement mlg = new GuiElement.ImageElement();
        double[] mlgiap = {0, 0.5};
        mlg.image = gameLogo;
        double[] s = {0.5, 0, 0.5, 0};
        mlg.fixedAspectRatio = true;
        mlg.size = s;
        double[] p = {0.1, 0, 0.5, 0};
        mlg.position = p;
        double[] a = {0, 0.5};
        mlg.anchorPoint = a;
        mlg.backgroundVisible = false;
        mlg.displayOrder = 2;
        mlg.imageAnchorPoint = mlgiap;
        stateMaster[0][0] = mlg;

        //this text will simply display "Play"
        double[] a1 = {0, 0};
        double[] textAnchor = {0.5, 0.5};
        GuiElement.TextElement t1 = new GuiElement.TextElement();
        double[] p1 = {0.2, 0, 0.3, 5};
        double[] s1 = {0.7, 0, 0.2, -10};
        t1.text = "Play";
        t1.textColor = new Color(255, 255, 255);
        t1.backgroundVisible = false;
        t1.size = s1;
        t1.anchorPoint = a1;
        t1.position = p1;
        t1.displayOrder = 1;
        t1.textAnchorPoint = textAnchor;
        stateMaster[0][3] = t1;

        //this text will display "Settings"
        GuiElement.TextElement t2 = new GuiElement.TextElement();
        double[] p2 = {0.2, 0, 0.5, 5};
        t2.text = "Settings";
        t2.textColor = new Color(255, 255, 255);
        t2.backgroundVisible = false;
        t2.size = s1;
        t2.anchorPoint = a1;
        t2.position = p2;
        t2.displayOrder = 1;
        t2.textAnchorPoint = textAnchor;
        stateMaster[0][4] = t2;

        //first button
        gsButton sb = new gsButton(2);
        sb.anchorPoint = a1;
        sb.size = s1;
        sb.position = p1;
        sb.text = "";
        sb.backgroundColor = new Color(255, 128, 0);
        stateMaster[0][1] = sb;

        //second button
        gsButton b2 = new gsButton(1);
        b2.anchorPoint = a1;
        b2.size = s1;
        b2.position = p2;
        b2.text = "";
        b2.backgroundColor = new Color(255, 128, 0);
        stateMaster[0][2] = b2;
    }

    public static void createSettingsScreen() {
        GuiElement.FrameElement handleBar = new GuiElement.FrameElement();
        double[] handleSize = {1, 0, 0, 50};
        handleBar.backgroundColor = new Color(128,128,128);
        handleBar.size = handleSize;
        stateMaster[1][0] = handleBar;

        GuiElement.TextElement handleTitle = new GuiElement.TextElement();
        double[] textInset = {0, 10};
        double[] textAnchor = {0, 0.5};
        double[] titleSize = {1, 0, 0, 50};
        double[] titlePosition = {0, 10, 0, 0};
        handleTitle.text = "Settings";
        handleTitle.textColor = new Color(255, 255, 255);
        handleTitle.backgroundVisible = false;
        handleTitle.size = titleSize;
        handleTitle.position = titlePosition;
        handleTitle.displayOrder = 1;
        handleTitle.textBorderInset = textInset;
        handleTitle.textAnchorPoint = textAnchor;
        stateMaster[1][1] = handleTitle;

        gsButton mbutton = new gsButton(0);
        double[] mbsize = {0, 120, 0, 50};
        double[] mbanchor = {1, 0};
        double[] mbTextAnchor = {1, 0.5};
        double[] mbpos = {1, 0, 0, 0};
        mbutton.text = "Back";
        mbutton.size = mbsize;
        mbutton.anchorPoint = mbanchor;
        mbutton.position = mbpos;
        mbutton.backgroundVisible = false;
        mbutton.textAnchorPoint = mbTextAnchor;
        mbutton.textBorderInset = textInset;
        mbutton.displayOrder = 1;

        mbutton.textColor = new Color(255, 255, 255);
        stateMaster[1][2] = mbutton;

        GuiElement.FrameElement p1bg = new GuiElement.FrameElement();
        double[] p1bgs = {0.3, 0, 1, -50};
        double[] p1bgp = {0, 0, 0, 50};
        p1bg.backgroundColor = new Color(200, 0, 0);
        p1bg.size = p1bgs;
        p1bg.position = p1bgp;
        stateMaster[1][3] = p1bg;

        GuiElement.FrameElement p2bg = new GuiElement.FrameElement();
        double[] p2bgp = {0.3, 0, 0, 50};
        p2bg.backgroundColor = new Color(0, 0, 200);
        p2bg.size = p1bgs;
        p2bg.position = p2bgp;
        stateMaster[1][4] = p2bg;

        GuiElement.TextElement p1title = new GuiElement.TextElement();
        double[] p1titles = {0.3, 0, 0, 40};
        double[] p1titlep = {0, 0, 0, 50};
        double[] titleTextAnchor = {0.5, 0.5};
        p1title.backgroundVisible = false;
        p1title.textColor = new Color(255, 255, 255);
        p1title.size = p1titles;
        p1title.position = p1titlep;
        p1title.text = "Player 1";
        p1title.displayOrder = 1;
        p1title.textBorderInset = textInset;
        p1title.textAnchorPoint = titleTextAnchor;
        stateMaster[1][5] = p1title;

        GuiElement.TextElement p2title = new GuiElement.TextElement();
        double[] p2titlep = {0.3, 0, 0, 50};
        p2title.backgroundVisible = false;
        p2title.textColor = new Color(255, 255, 255);
        p2title.size = p1titles;
        p2title.position = p2titlep;
        p2title.text = "Player 2";
        p2title.displayOrder = 1;
        p2title.textBorderInset = textInset;
        p2title.textAnchorPoint = titleTextAnchor;
        stateMaster[1][6] = p2title;

        //creating the bind buttons for each player
        double[] bbAnchor = {0.5, 0};
        double[] bbSize = {0.05, 0, 0, 30};
        double[] bbTextAnchor = {0.5, 0.5};
        for (int i = 0; i < 4; i++) {
            bindButton player1Binder = new bindButton(0, i);
            double[] p1bindPos = {0.06 * (i + 1), 0, 0, 100};
            player1Binder.size = bbSize;
            player1Binder.textAnchorPoint = bbTextAnchor;
            player1Binder.textBorderInset = textInset;
            player1Binder.anchorPoint = bbAnchor;
            player1Binder.backgroundColor = new Color(255, 0, 0);
            player1Binder.cacheColor = new Color(255, 0, 0);
            player1Binder.text = "A";
            player1Binder.textColor = new Color(255, 255, 255);
            player1Binder.position = p1bindPos;
            player1Binder.displayOrder = 2;
            player1Binder.backgroundVisible = true;
            stateMaster[1][7 + i * 2] = player1Binder;
            playerBinders[i * 2] = player1Binder;

            bindButton player2Binder = new bindButton(1, i);
            double[] p2bindPos = {0.3 + 0.06 * (i + 1), 0, 0, 100};
            player2Binder.size = bbSize;
            player2Binder.textAnchorPoint = bbTextAnchor;
            player2Binder.textBorderInset = textInset;
            player2Binder.anchorPoint = bbAnchor;
            player2Binder.backgroundColor = new Color(0, 0, 255);
            player2Binder.cacheColor = new Color(0, 0, 255);
            player2Binder.text = "A";
            player2Binder.textColor = new Color(255, 255, 255);
            player2Binder.position = p2bindPos;
            player2Binder.displayOrder = 2;
            stateMaster[1][8 + i * 2] = player2Binder;
            playerBinders[1 + i * 2] = player2Binder;
        }

        GuiElement.TextElement p1keyLabel = new GuiElement.TextElement();
        double[] p1klp = {0, 0, 0, 130};
        p1keyLabel.backgroundVisible = false;
        p1keyLabel.textColor = new Color(255, 255, 255);
        p1keyLabel.size = p1titles;
        p1keyLabel.position = p1klp;
        p1keyLabel.text = "Keybinds";
        p1keyLabel.displayOrder = 1;
        p1keyLabel.textBorderInset = textInset;
        p1keyLabel.textAnchorPoint = titleTextAnchor;
        stateMaster[1][15] = p1keyLabel;

        GuiElement.TextElement p2keyLabel = new GuiElement.TextElement();
        double[] p2klp = {0.3, 0, 0, 130};
        p2keyLabel.backgroundVisible = false;
        p2keyLabel.textColor = new Color(255, 255, 255);
        p2keyLabel.size = p1titles;
        p2keyLabel.position = p2klp;
        p2keyLabel.text = "Keybinds";
        p2keyLabel.displayOrder = 1;
        p2keyLabel.textBorderInset = textInset;
        p2keyLabel.textAnchorPoint = titleTextAnchor;
        stateMaster[1][16] = p2keyLabel;
    }

    public static void createMapScreen() {
        double[] textInset = {0, 10};
        double[] textAnchorPoint = {0, 0.5};

        GuiElement.FrameElement handleBar = new GuiElement.FrameElement();
        double[] handleSize = {1, 0, 0, 50};
        handleBar.backgroundColor = new Color(128,128,128);
        handleBar.size = handleSize;
        stateMaster[2][0] = handleBar;

        GuiElement.TextElement handleTitle = new GuiElement.TextElement();
        double[] titleSize = {1, 0, 0, 50};
        double[] titlePosition = {0, 10, 0, 0};
        handleTitle.text = "Map Selection";
        handleTitle.textColor = new Color(255, 255, 255);
        handleTitle.backgroundVisible = false;
        handleTitle.size = titleSize;
        handleTitle.position = titlePosition;
        handleTitle.displayOrder = 1;
        handleTitle.textAnchorPoint = textAnchorPoint;
        handleTitle.textBorderInset = textInset;
        stateMaster[2][1] = handleTitle;

        gsButton mbutton = new gsButton(0);
        double[] mbsize = {0, 120, 0, 50};
        double[] mbanchor = {1, 0};
        double[] mbTextAnchor = {1, 0.5};
        double[] mbpos = {1, 0, 0, 0};
        mbutton.text = "Back";
        mbutton.size = mbsize;
        mbutton.anchorPoint = mbanchor;
        mbutton.position = mbpos;
        mbutton.backgroundVisible = false;
        mbutton.displayOrder = 1;
        mbutton.textColor = new Color(255, 255, 255);
        mbutton.textAnchorPoint = mbTextAnchor;
        mbutton.textBorderInset = textInset;
        stateMaster[2][2] = mbutton;

        mapButton fotsButton = new mapButton("Fury of the Storm.txt");
        double[] mapButtonSize = {0.7, 0, 0.1, 0};
        double[] fotsPosition = {0.3, 0, 0, 50};
        fotsButton.text = "Fury of the Storm";
        fotsButton.size = mapButtonSize;
        fotsButton.position = fotsPosition;
        fotsButton.backgroundColor = new Color(255, 0, 255);
        stateMaster[2][3] = fotsButton;

        //creating the leaderboard screens
        double[] leSize = {0.3, -10, 0, 50};
        for (int i = 0; i < 5; i++) {
            double[] elementPosition = {0, 10, 0, 50 * (i + 2)};
            GuiElement.TextElement placeLabel = new GuiElement.TextElement();
            placeLabel.size = leSize;
            placeLabel.position = elementPosition;
            placeLabel.backgroundVisible = false;
            placeLabel.textBorderInset = textInset;
            placeLabel.textAnchorPoint = textAnchorPoint;
            placeLabel.text = Integer.toString(i + 1) + ".";
            placeLabel.textColor = new Color(255, 255, 255);
            stateMaster[2][4 + i * 2] = placeLabel;

            GuiElement.TextElement scoreLabel = new GuiElement.TextElement();
            scoreLabel.size = leSize;
            scoreLabel.position = elementPosition;
            scoreLabel.backgroundVisible = false;
            scoreLabel.textBorderInset = textInset;
            scoreLabel.textAnchorPoint = mbTextAnchor;
            scoreLabel.text = "0";
            scoreLabel.textColor = new Color(255, 255, 255);
            stateMaster[2][5 + i * 2] = scoreLabel;
            leaderboardElements[i] = scoreLabel;
        }

        GuiElement.TextElement leaderLabel = new GuiElement.TextElement();
        double[] lpos = {0, 10, 0, 50};
        leaderLabel.backgroundVisible = false;
        leaderLabel.size = leSize;
        leaderLabel.position = lpos;
        leaderLabel.text = "Leaderboard";
        leaderLabel.textBorderInset = textInset;
        leaderLabel.textAnchorPoint = textAnchorPoint;
        leaderLabel.textColor = new Color(255, 255, 255);
        stateMaster[2][14] = leaderLabel;

        mapButton fnfButton = new mapButton("FNF Tutorial.txt");
        double[] fnfPosition = {0.3, 0, 0.1, 50};
        fnfButton.text = "FNF Tutorial";
        fnfButton.size = mapButtonSize;
        fnfButton.position = fnfPosition;
        fnfButton.backgroundColor = new Color(0, 255, 0);
        stateMaster[2][15] = fnfButton;
    }

    public static void createGameScreen() {
        double[] textInset = {0, 10};
        double[] textAnchorPoint = {0, 0.5};

        GuiElement.FrameElement handleBar = new GuiElement.FrameElement();
        double[] handleSize = {1, 0, 0, 50};
        handleBar.backgroundColor = new Color(128,128,128);
        handleBar.size = handleSize;
        stateMaster[3][0] = handleBar;

        handleTitle3 = new GuiElement.TextElement();
        double[] titleSize = {1, 0, 0, 50};
        double[] titlePosition = {0, 10, 0, 0};
        handleTitle3.text = "placeholder";
        handleTitle3.textColor = new Color(255, 255, 255);
        handleTitle3.backgroundVisible = false;
        handleTitle3.size = titleSize;
        handleTitle3.position = titlePosition;
        handleTitle3.displayOrder = 1;
        handleTitle3.textAnchorPoint = textAnchorPoint;
        handleTitle3.textBorderInset = textInset;
        stateMaster[3][1] = handleTitle3;

        gbackButton mbutton = new gbackButton(2);
        double[] mbsize = {0, 120, 0, 50};
        double[] mbanchor = {1, 0};
        double[] mbTextAnchor = {1, 0.5};
        double[] mbpos = {1, 0, 0, 0};
        mbutton.text = "Back";
        mbutton.size = mbsize;
        mbutton.anchorPoint = mbanchor;
        mbutton.position = mbpos;
        mbutton.backgroundVisible = false;
        mbutton.displayOrder = 1;
        mbutton.textColor = new Color(255, 255, 255);
        mbutton.textAnchorPoint = mbTextAnchor;
        mbutton.textBorderInset = textInset;
        stateMaster[3][2] = mbutton;

        //creating the frame that will behind the score counters
        GuiElement.FrameElement sbg = new GuiElement.FrameElement();
        double[] sbgpos = {0, 0, 0, 50};
        sbg.backgroundColor = new Color(96, 96, 96);
        sbg.size = handleSize;
        sbg.position = sbgpos;
        stateMaster[3][3] = sbg;

        //creating the score displays for both player 1 and player 2
        scorep1 = new GuiElement.TextElement();
        double[] stsize = {0.5, 0, 0, 50};
        double[] sp1pos = {0, 10, 0, 50};
        scorep1.text = "000000";
        scorep1.position = sp1pos;
        scorep1.size = stsize;
        scorep1.backgroundVisible = false;
        scorep1.textColor = new Color(255, 255, 255);
        scorep1.textAnchorPoint = textAnchorPoint;
        scorep1.textBorderInset = textInset;
        scorep1.displayOrder = 1;
        stateMaster[3][4] = scorep1;

        scorep2 = new GuiElement.TextElement();
        double[] sp2pos = {1, 0, 0, 50};
        double[] sp2anchor = {1, 0};
        double[] sp2textAnchor = {1, 0.5};
        scorep2.anchorPoint = sp2anchor;
        scorep2.size = stsize;
        scorep2.position = sp2pos;
        scorep2.backgroundVisible = false;
        scorep2.textColor = new Color(255, 255, 255);
        scorep2.text = "000000";
        scorep2.displayOrder = 1;
        scorep2.textAnchorPoint = sp2textAnchor;
        scorep2.textBorderInset = textInset;
        stateMaster[3][5] = scorep2;

        //the tutorial message text frame
        GuiElement.FrameElement tutorialbg = new GuiElement.FrameElement();
        double[] tbgsize = {1, 0, 0, 100};
        double[] tbgpos = {0, 0, 1, -100};
        tutorialbg.size = tbgsize;
        tutorialbg.position = tbgpos;
        tutorialbg.backgroundColor = new Color(96, 96, 96);
        stateMaster[3][6] = tutorialbg;

        player1Track = new RhythmTrack(1, settings[0].keybinds);
        double[] trackSize = {0.3, 0, 1, -200};
        double[] trackAnchor = {0.5, 0};
        double[] track1Position = {0.25, 0, 0, 100};
        player1Track.position = track1Position;
        player1Track.size = trackSize;
        player1Track.backgroundVisible = false;
        player1Track.anchorPoint = trackAnchor;
        player1Track.keybinds = settings[0].keybinds;
        stateMaster[3][7] = player1Track;

        player2Track = new RhythmTrack(2, settings[1].keybinds);
        double[] track2Position = {0.75, 0, 0, 100};
        player2Track.position = track2Position;
        player2Track.size = trackSize;
        player2Track.anchorPoint = trackAnchor;
        player2Track.backgroundVisible = false;
        player2Track.keybinds = settings[1].keybinds;
        player2Track.noteColor = new Color(0, 128, 255);
        stateMaster[3][8] = player2Track;

        combop1 = new GuiElement.TextElement();
        double[] cp1pos = {0, 0, 1, -100};
        combop1.size = stsize;
        combop1.position = cp1pos;
        combop1.backgroundVisible = false;
        combop1.textAnchorPoint = textAnchorPoint;
        combop1.textBorderInset = textInset;
        combop1.textColor = new Color(255, 0, 0);
        combop1.displayOrder = 2;
        stateMaster[3][9] = combop1;

        combop2 = new GuiElement.TextElement();
        double[] cp2pos = {0.5, 0, 1, -100};
        combop2.size = stsize;
        combop2.position = cp2pos;
        combop2.backgroundVisible = false;
        combop2.textAnchorPoint = sp2textAnchor;
        combop2.textBorderInset = textInset;
        combop2.displayOrder = 2;
        combop2.textColor = new Color(0, 128, 255);
        stateMaster[3][10] = combop2;

        accp1 = new GuiElement.TextElement();
        double[] acc1pos = {0, 0, 1, -50};
        accp1.size = stsize;
        accp1.position = acc1pos;
        accp1.backgroundVisible = false;
        accp1.textAnchorPoint = textAnchorPoint;
        accp1.textBorderInset = textInset;
        accp1.displayOrder = 2;
        accp1.textColor = new Color(255, 0, 0);
        gameScreen[11] = accp1;

        accp2 = new GuiElement.TextElement();
        double[] acc2pos = {0.5, 0, 1, -50};
        accp2.size = stsize;
        accp2.position = acc2pos;
        accp2.backgroundVisible = false;
        accp2.textAnchorPoint = sp2textAnchor;
        accp2.textBorderInset = textInset;
        accp2.displayOrder = 2;
        accp2.textColor = new Color(0, 128, 255);
        gameScreen[12] = accp2;
    }

    public static void createResultScreen() {
        double[] textInset = {0, 10};
        double[] textAnchorPoint = {0, 0.5};

        GuiElement.FrameElement handleBar = new GuiElement.FrameElement();
        double[] handleSize = {1, 0, 0, 50};
        handleBar.backgroundColor = new Color(128,128,128);
        handleBar.size = handleSize;
        stateMaster[4][0] = handleBar;

        handleTitle4 = new GuiElement.TextElement();
        double[] titleSize = {1, 0, 0, 50};
        double[] titlePosition = {0, 10, 0, 0};
        handleTitle4.text = "someone won yay";
        handleTitle4.textColor = new Color(255, 255, 255);
        handleTitle4.backgroundVisible = false;
        handleTitle4.size = titleSize;
        handleTitle4.position = titlePosition;
        handleTitle4.displayOrder = 1;
        handleTitle4.textAnchorPoint = textAnchorPoint;
        handleTitle4.textBorderInset = textInset;
        stateMaster[4][1] = handleTitle4;

        gsButton mbutton = new gsButton(2);
        double[] mbsize = {0, 120, 0, 50};
        double[] mbanchor = {1, 0};
        double[] mbTextAnchor = {1, 0.5};
        double[] mbpos = {1, 0, 0, 0};
        mbutton.text = "Back";
        mbutton.size = mbsize;
        mbutton.anchorPoint = mbanchor;
        mbutton.position = mbpos;
        mbutton.backgroundVisible = false;
        mbutton.displayOrder = 1;
        mbutton.textColor = new Color(255, 255, 255);
        mbutton.textAnchorPoint = mbTextAnchor;
        mbutton.textBorderInset = textInset;
        stateMaster[4][2] = mbutton;

        //shows the rank you got when you complete a map
        rankingImage = new GuiElement.ImageElement();
        double[] riAnchor = {0.5, 0.5};
        double[] riSize = {0.3, 0, 1, -120};
        double[] riPosition = {0, 10, 0, 60};
        rankingImage.backgroundVisible = false;
        rankingImage.imageAnchorPoint = riAnchor;
        rankingImage.position = riPosition;
        rankingImage.image = rankingImages[0];
        rankingImage.size = riSize;
        rankingImage.fixedAspectRatio = true;
        stateMaster[4][3] = rankingImage;

        //text elements that show stats related to your game
        scoreFrame = new GuiElement.TextElement();
        double[] sfSize = {0.3, -10, 0, 50};
        double[] sfPosition = {0, 10, 1, -60};
        scoreFrame.backgroundVisible = false;
        scoreFrame.size = sfSize;
        scoreFrame.position = sfPosition;
        scoreFrame.textColor = new Color(255, 255, 255);
        scoreFrame.text = "Final Score: 42069";
        scoreFrame.textAnchorPoint = textAnchorPoint;
        scoreFrame.textBorderInset = textInset;
        stateMaster[4][4] = scoreFrame;
        
        perfectFrame = new GuiElement.TextElement();
        double[] pfSize = {0.3, 0, 0.1, 0};
        double[] pfPosition = {0.3, 0, 0, 170};
        perfectFrame.backgroundVisible = false;
        perfectFrame.size = pfSize;
        perfectFrame.position = pfPosition;
        perfectFrame.textColor = new Color(0, 255, 255);
        perfectFrame.text = "Perfect: 420";
        perfectFrame.textAnchorPoint = textAnchorPoint;
        perfectFrame.textBorderInset = textInset;
        stateMaster[4][5] = perfectFrame;

        greatFrame = new GuiElement.TextElement();
        double[] gfPosition = {0.65, 0, 0, 170};
        greatFrame.backgroundVisible = false;
        greatFrame.size = pfSize;
        greatFrame.position = gfPosition;
        greatFrame.textColor = new Color(0, 255, 0);
        greatFrame.text = "Great: 69";
        greatFrame.textAnchorPoint = textAnchorPoint;
        greatFrame.textBorderInset = textInset;
        stateMaster[4][6] = greatFrame;

        okayFrame = new GuiElement.TextElement();
        double[] ofPosition = {0.3, 0, 0.3, 170};
        okayFrame.backgroundVisible = false;
        okayFrame.size = pfSize;
        okayFrame.position = ofPosition;
        okayFrame.textColor = new Color(255, 255, 0);
        okayFrame.text = "Okay: 42";
        okayFrame.textAnchorPoint = textAnchorPoint;
        okayFrame.textBorderInset = textInset;
        stateMaster[4][7] = okayFrame;

        missFrame = new GuiElement.TextElement();
        double[] mfPosition = {0.65, 0, 0.3, 170};
        missFrame.backgroundVisible = false;
        missFrame.size = pfSize;
        missFrame.position = mfPosition;
        missFrame.textColor = new Color(255, 0, 0);
        missFrame.text = "Miss: 20";
        missFrame.textAnchorPoint = textAnchorPoint;
        missFrame.textBorderInset = textInset;
        stateMaster[4][8] = missFrame;

        comboFrame = new GuiElement.TextElement();
        double[] cfPosition = {0.3, 0, 1, -60};
        double[] cfSize = {0.7, -10, 0, 50};
        comboFrame.backgroundVisible = false;
        comboFrame.size = cfSize;
        comboFrame.position = cfPosition;
        comboFrame.textColor = new Color(255, 255, 255);
        comboFrame.text = "Max Combo: 420";
        comboFrame.textAnchorPoint = textAnchorPoint;
        comboFrame.textBorderInset = textInset;
        stateMaster[4][9] = comboFrame; 
    }
    //makes all elements from the old state invisible and makes the new state items visible
    public static void changeState(int newState) {
        for (GuiElement.FrameElement f: stateMaster[gameState]) {
            f.visible = false;
        }
        gameState = newState;
        for (GuiElement.FrameElement f: stateMaster[gameState]) {
            f.visible = true;
        }
    }

    //all possible buttons that exist within the context of the game
    public static class gsButton extends GuiElement.TextButton {
        public int state;
        public gsButton(int gs) {
            state = gs;
        }
        public void onClick() {
            changeState(state);
        }
    }

    public static class gbackButton extends gsButton {
        public gbackButton(int gs) {
            super(gs);
        }
        public void onClick() {
            super.onClick();
            audioPlayer.stopAudio(audioName);
        }
    }

    //button which assists in changing keybinds
    public static class bindButton extends GuiElement.TextButton {
        public int playerId;
        public int keybindId;
        public Color cacheColor;
        public void onClick() {
            if (bindSelected) {
                currentBindButton.backgroundColor = currentBindButton.cacheColor;
            }
            bindSelected = true;
            backgroundColor = new Color(128, 128, 128);
            currentBindButton = this;
        }
        public bindButton(int pid, int kid) {
            playerId = pid;
            keybindId = kid;
        }
    }

    //all this does is load a map from an associated file
    public static class mapButton extends GuiElement.TextButton {
        public MapData loadedData;
        private String name;

        //directly loading the map
        public void onClick() {
            player1Track.loadMap(loadedData.tracks);
            player2Track.loadMap(loadedData.tracks);

            mapLength = loadedData.songLength;
            audioName = loadedData.audioName;
            System.out.println(audioName);
            audioPlayer.startAudio(audioName);
            delta = System.currentTimeMillis();

            handleTitle3.text = name;
            changeState(3);
        }

        public mapButton(String mapName) {
            name = mapName;
            loadedData = new MapData("maps\\" + mapName);
        }
    }

    //hardware modules
    public static InputMaster inputReader = new InputMaster();
    public static AudioMaster audioPlayer = new AudioMaster();

    //displays the keybinds that each player is currentl yusing
    public static void displayKeybinds() {
        for (bindButton binder: playerBinders) {
            binder.text = KeyEvent.getKeyText(settings[binder.playerId].keybinds[binder.keybindId]);
        }
    }

    //testing out the rendering for the rhythm track
    public static void testingRoom() {
        int[] testbinds = {68, 70, 74, 75};
        RhythmTrack track = new RhythmTrack(2, testbinds);
        //testTrack.get(0).add(new NoteData(870, 0));
        double[] trackSize = {0.5, 0, 0.75, 0};
        double[] trackPos = {0, 0, 0, 0};
        track.size = trackSize;
        track.position = trackPos;
        track.backgroundVisible = false;
        track.keybinds = testbinds;
        track.visible = true;


        MapData testMap = new MapData("maps\\Fury of the Storm.txt");
        track.loadMap(testMap.tracks);
        audioPlayer.startAudio(testMap.audioName);
        long delta = System.currentTimeMillis();

        while (true) {
            track.pressedKeys = inputReader.pressedKeys;
            track.gameTime = (int)(System.currentTimeMillis() - delta);
            //track.scoringAlgorithm(inputReader.pressedKeys);
        }
    }

    //reads scores.txt
    public static String[] getScores() {
        String[] fscores = new String[5];
        try {
            Scanner s = new Scanner(new File("userdata\\scores.txt"));
            for (int i = 0; i < 5; i++) {
                fscores[i] = s.nextLine();
            }
            s.close();
        } catch (Exception e) {
            System.out.printf("ERROR READING SCORES: %s\n", e.getMessage());
        }
        return fscores;
    }

    //displays scores.txt items on the leaderboard
    public static void displayLeaderboard() {
        String[] fscores = getScores();
        for (int i = 0; i < 5; i++) {
            leaderboardElements[i].text = fscores[i];
        }
    }

    //appends a score to score.txt
    public static void submitScore(int score) {
        try {
            String[] fscores = getScores();
            PrintWriter p = new PrintWriter(new FileWriter("userdata\\scores.txt"));

            int lp = 0;
            boolean pusher = true;

            for (int i = 0; i < 5; i++) {
                if (lp == 5) {
                    break;
                }

                if (score > Integer.parseInt(fscores[i]) && pusher) {
                    lp++;
                    pusher = false;
                    p.println(score);
                }

                p.println(fscores[i]);
                lp++;
            }
            p.close();
        } catch (Exception e) {
            System.out.printf("ERROR SUBMITTING SCORE: %s\n", e.getMessage());
        }
    }

    //setting up the window
    public static void main(String[] args) {
        //stream all data from text files
        readSettings();

        //loading all the images for the game
        try {
            gameLogo = ImageIO.read(new File("images/JMania.png"));
            rankingImages[0] = ImageIO.read(new File("images/ranking-S.png"));
            rankingImages[1] = ImageIO.read(new File("images/ranking-A.png"));
            rankingImages[2] = ImageIO.read(new File("images/ranking-B.png"));
            rankingImages[3] = ImageIO.read(new File("images/ranking-C.png"));
            rankingImages[4] = ImageIO.read(new File("images/ranking-D.png"));
        } catch (Exception e) {
            System.out.printf("ERROR READING IMAGES: %s\n", e.getMessage());
            return;
        }

        JFrame window = new JFrame();
        LunaEngine frame = new LunaEngine();
        frame.addKeyListener(inputReader);
        window.add(frame);
        window.pack();
        window.setTitle("JMania");
        window.setVisible(true);

        //create the gui for each screen
        createTitleScreen();
        createSettingsScreen();
        createMapScreen();
        createGameScreen();
        createResultScreen();
        displayLeaderboard();
        displayKeybinds();

        //load the starting screen
        changeState(0);
        //testingRoom();

        Thread thread = new Thread(new mainLoop());
        thread.start();
    }


    public static class mainLoop implements Runnable {
        public void run() {
            //main game loop
            while (true) {
                try {
                    switch (gameState) {
                        case 1:
                        //checking if any keys are pressed and if any bind buttons are selected
                        if (bindSelected) {
                            for (int keyCode: inputReader.pressedKeys) {
                                settings[currentBindButton.playerId].keybinds[currentBindButton.keybindId] = keyCode;
                                bindSelected = false;
                                currentBindButton.backgroundColor = currentBindButton.cacheColor;
                                writeSettings();
                                displayKeybinds();
                                break;
                            }
                        }
                        break;
                        case 3:
                        //moving the track forwards
                        int elapsedTime = (int)(System.currentTimeMillis() - delta);
                        //System.out.println(elapsedTime);
                        player1Track.gameTime = elapsedTime;
                        player2Track.gameTime = elapsedTime;
                        player1Track.pressedKeys = inputReader.pressedKeys;
                        player2Track.pressedKeys = inputReader.pressedKeys;


                        //updating the score display to show player scores
                        scorep1.text = Integer.toString(player1Track.score);
                        scorep2.text = Integer.toString(player2Track.score);

                        //updating combo displays
                        combop1.text = "Combo: " + Integer.toString(player1Track.combo);
                        combop2.text = "Combo: " + Integer.toString(player2Track.combo);

                        //updating the accuracy displays
                        accp1.text = "Accuracy: " + Double.toString(player1Track.getAccuracy()) + "%";
                        accp2.text = "Accuracy: " + Double.toString(player2Track.getAccuracy()) + "%";

                        //checking if the game is over
                        if (elapsedTime > mapLength) {
                            //setting the ending screen gui texts
                            RhythmTrack winner;
                            if (player1Track.score >= player2Track.score) {
                                winner = player1Track;
                                handleTitle4.text = "Player 1 wins!";
                            } else {
                                winner = player2Track;
                                handleTitle4.text = "Player 2 wins!";
                            }

                            double playAccuracy = winner.getAccuracy();
                            if (playAccuracy < 70) {
                                rankingImage.image = rankingImages[4];
                            } else if (playAccuracy < 80) {
                                rankingImage.image = rankingImages[3];
                            } else if (playAccuracy < 90) {
                                rankingImage.image = rankingImages[2];
                            } else if (playAccuracy < 95) {
                                rankingImage.image = rankingImages[1];
                            } else {
                                rankingImage.image = rankingImages[0];
                            }

                            scoreFrame.text = "Final Score: " + Integer.toString(winner.score);
                            perfectFrame.text = "Perfect: " + Integer.toString(winner.perfect);
                            greatFrame.text = "Great: " + Integer.toString(winner.great);
                            okayFrame.text = "Okay: " + Integer.toString(winner.okay);
                            missFrame.text = "Miss: " + Integer.toString(winner.miss);
                            comboFrame.text = "Max Combo: " + Integer.toString(winner.maxCombo);

                            submitScore(player1Track.score);
                            submitScore(player2Track.score);
                            displayLeaderboard();
                            changeState(4);
                        }

                        break;
                        default:
                        break;
                    }
                    Thread.sleep(15);
                } catch (Exception e) {

                }
            }
        }

        public mainLoop() {

        }
    }
}