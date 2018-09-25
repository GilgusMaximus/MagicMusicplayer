package FileClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputReader {
    private static ArrayList<String> allFiles = new ArrayList<>();
    private static ArrayList<Musicfile> musicFiles = new ArrayList<>();
    private static FileWriter fileWriter;
   public static void readInput(){
        boolean search = true;
        boolean append = false;
        int writeBegin = 0;
        FileClasses.FileReader reader = new FileReader();
        System.out.println("Willkommen beim MagicMusicPlayer");
        if(new File("files/Searchdirectories.txt").exists()) {
            if (reader.readFile("files/MusicFiles.txt")) {
                allFiles = reader.getAllFiles();
                writeBegin = allFiles.size();
                append = true;
            } else {
                allFiles = null;
            }
        }else{
            allFiles = null;
        }
        if(allFiles == null || writeBegin == 0){
            System.out.println("Bitte geben sie einen Ordner an in welchem selbst + allen Unterordnern nach Musik gesucht werden soll");
        }else{
            System.out.println("Möchten sie zu den bereits durchsuchten Directories weitere hinzufügen? y/n");
            Scanner s = new Scanner(System.in);
            if(!s.nextLine().toLowerCase().equals("y")){
                search = false;
            }
        }


        MusicFileCreator creator = new MusicFileCreator(allFiles);
      creator.start();
        ArrayList<String> directories;

       if(search){
            directories = readInputDirectories();
            allFiles = FileSearcher.findAllFiles(directories);
            PatternMatcher p = new PatternMatcher();
            Musicfile musicfile;
            long startTime = System.nanoTime();
           for(int i = 1; i < allFiles.size(); i++){
                String path = allFiles.get(i);
                //System.out.println(path);
                String[] tags;
                File file = new File(path);
                if(path.substring(path.length()-3).equals("m4a")){
                    tags = p.findM4AData(file);
                }else {
                    tags = p.findMp3Data(file);
                  System.out.println("HIER");
                }
             for(int k = 0; k < tags.length; k++){
               System.out.println(tags[k]);
             }
                String[] multArtists = null;
                if(tags[1] != null) {
                    multArtists = tags[1].split("/");
                }
                if(multArtists != null && multArtists.length > 1 && multArtists[0].toLowerCase().equals("axwell")){ //Small trick to avoid the bug by axwell /\ ingrosso ;)
                    multArtists = null;
                }
                if(multArtists == null){
                    musicfile = new Musicfile(path, tags[1], tags[2], tags[0], null);
                }else{
                    musicfile = new Musicfile(path, multArtists, tags[2], tags[0], null);
                }
                musicFiles.add(musicfile);
            }
            long endTime   = System.nanoTime();
            long totalTime = endTime - startTime;
            System.out.println(totalTime);

        }

        //FileWrite extends Thread -> we can put the writing of the files onto another Thread
        fileWriter = new FileWriter(musicFiles, append, "files/Musicfiles.txt", writeBegin);
        fileWriter.run();
        System.out.println("ANZAHL"  + musicFiles.size());
        try {
          creator.join();
        }catch (Exception e){
          System.err.println("ERROR: InputReader: MusicFileCreater - JOIN: " + e);
        }
        musicFiles.addAll(creator.getMusicFiles());
      // FileWriter.writeToFile("files/Searchdirectories.txt", allFiles, false);    //write all files into the Seachdirectories.txt with the following system: absolute file path \n file name \n
    }
    private static ArrayList<String> readInputDirectories(){
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> directories = new ArrayList<>();
        String directory = null;
        while(directory == null){
            directory = scanner.nextLine();
            File checkExistence = new File(directory);
            if(!checkExistence.isDirectory()) {
                directory = null;
                System.out.println("This is not a valid directory. please try again");
            }else{
                directories.add(directory);
                System.out.println("Möchten sie einen weiteren Ordner hinzufügen? y/n");
                String answer = scanner.nextLine().toLowerCase();
                if(answer.equals("y")) {
                    directory = null;
                    System.out.println("Bitte geben sie einen weiteren Ordnern an");
                }
            }
        }
        return directories;
    }
    public static ArrayList<Musicfile> getMusicFiles(){
        return musicFiles;
    }
}
