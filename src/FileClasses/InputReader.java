package FileClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;

public class InputReader{


   private static ArrayList<Musicfile> musicFiles = new ArrayList<>();
   private static boolean newMusic = true;

   public static void readInput(ArrayList<String> newDirectories) {
      double timeStart = System.nanoTime();
      boolean search = true;
      boolean append = true;
      FileWriter fileWriter;
      ArrayList<String> allFiles;

      FileClasses.FileReader reader = new FileReader();
      System.out.println("Willkommen beim MagicMusicPlayer");

      if (reader.readFile("files/MusicFiles.txt")) {   //return true if the file exists and could be read
         allFiles = reader.getAllFiles();                      //get all data
         append = true;                                        //set flag
      } else {
         allFiles = null;
      }


      //these two ifs cannot be true at the same time. if allFiles = new, the nwe didnt read any files prevously -> teh user has to provide at least one search directory
      if (allFiles == null) {
         search = true;
         newMusic = true;
      }
      //TODO Check if the directory has no music files, whetehr the two ifs are both true
      if(newDirectories == null){
         search = false;
         newMusic = false;
      }

      MusicFileCreator creator = new MusicFileCreator(allFiles);
      creator.start();  //starts thread to create music file objects from the encoded string music files

      if (search) {  //do we hav new directories to scan?
         //yes -> search for all music files
         ArrayList<String> newSearchedFiles = FileSearcher.findAllFiles(newDirectories);
         PatternMatcher p = new PatternMatcher();
         Musicfile musicfile;

         for (int i = 0; i < newSearchedFiles.size(); i++) {   //create new musicfile objects for the found music files
            String[] tags;
            String[] multArtists = null;
            String path = newSearchedFiles.get(i);
            File file = new File(path);

            if (path.substring(path.length() - 3).equals("m4a")) {   //m4a file?
               //yes -> use custom tag reader
               tags = p.findM4AData(file);

            } else {
               //no -> mp3 or wav file using same tag reader TODO write WAV Tag reader
               tags = p.findMp3Data(file);
            }

            if (tags[1] != null) {  //does the music file have an artist value?
               //yes -> try to split the file into multiple artists (they are split by an '/')
               multArtists = tags[1].split("/");
            }
            //does the song contain 2 artists and the first one is axwell?
            if (multArtists != null && multArtists.length == 2 && multArtists[0].toLowerCase().equals("axwell")) { //Small trick to avoid the bug by axwell /\ ingrosso ;)
               //yes -> it probably will be Axwell /\ Ingrosso withput any more artists -> little edge case -> reset artists  TODO find better solution
               multArtists = null;
            }
            if (multArtists == null || multArtists.length == 1) {
               //not multiple artists
               musicfile = new Musicfile(path, tags[1], tags[2], tags[0], tags[3]);
            } else {
               //multiple artists
               musicfile = new Musicfile(path, multArtists, tags[2], tags[0], tags[3]);
            }
            musicFiles.add(musicfile);
         }
      }

      //create custom FileWriter
      fileWriter = new FileWriter(musicFiles, append, 0, "files/Musicfiles.txt");
      //start the writer on another thread, and let it write all new musicfiles to it
      fileWriter.run();

      //wait for the creator thread to finish creating all the musicfile objects
      try {
         creator.join();
      } catch (Exception e) {
         System.err.println("ERROR: InputReader: MusicFileCreater - JOIN: " + e);
      }
      int neueZahl = musicFiles.size();
      musicFiles.addAll(creator.getMusicFiles());
      double timeEnd = System.nanoTime();
      System.out.println("read Input hat " + ((timeEnd-timeStart)/1000000000) + "s für " + neueZahl + " neu zu suchende Files und " + (musicFiles.size()-neueZahl) + " bereits gespeicherte Files gebraucht");
   }

   public static boolean getnewMusic(){
    return newMusic;
   }

   public static ArrayList<Musicfile> getMusicFiles() {
      return musicFiles;
   }
}
