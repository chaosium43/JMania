//responsible for loading maps from files and creating their respective wrapper objects

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class MapData {
    public ArrayList<ArrayList<NoteData>> tracks = new ArrayList<ArrayList<NoteData>>();
    public String audioName;
    public int songLength;
    public MapData(String fileName) {
        for (int i = 0; i < 4; i++) {
            tracks.add(new ArrayList<NoteData>());
        }
        try {
            //read in file data
            Scanner s = new Scanner(new File(fileName));
            audioName = s.nextLine();
            songLength = s.nextInt();
            
            while (s.hasNextInt()) {
                int a = s.nextInt();
                int b = s.nextInt();
                int c = s.nextInt();

                tracks.get(c).add(new NoteData(a, b));
            }
            
            s.close();
        } catch (Exception e) {
            System.out.printf("UNABLE TO LOAD MAP: %s\n", e.getMessage());
        }
    }
}
