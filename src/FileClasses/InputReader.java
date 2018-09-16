package FileClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class InputReader {
    private static ArrayList<String> allFiles = new ArrayList<>();
   public static void readInput(){
        boolean search = true;
        FileClasses.FileReader reader = new FileReader();
        System.out.println("Willkommen beim MagicMusicPlayer");

        if(reader.readFile("files/Searchdirectories.txt")) {
            allFiles = reader.getAllFiles();
        }else{
            allFiles = null;
        }

        if(allFiles == null){
            System.out.println("Bitte geben sie einen Ordner an in welchem selbst + allen Unterordnern nach Musik gesucht werden soll");
        }else{
            System.out.println("Möchten sie zu den bereits durchsuchten Directories weitere hinzufügen? y/n");
            Scanner s = new Scanner(System.in);
            if(!s.nextLine().toLowerCase().equals("y")){
                search = false;
            }
        }
        ArrayList<String> directories;
        if(search) { //did teh user add more directories / had no existing ones?
            //yes -> search for files
            directories = readInputDirectories();                   //user inputs directories to search for music files
            System.out.println("Now searching for music files. This may take a while...");
            if(allFiles == null) {  //Do we already have files saved?
                //no -> allFiles = the directories that have been searched
                allFiles                    = FileSearcher.findAllFiles(directories);   //search through all directories and subdirectories
            }else{
                //yes -> we append the new files to the old ones
                ArrayList<String> newFiles = FileSearcher.findAllFiles(directories);
                allFiles.addAll(newFiles);
            }
            System.out.println("File search has been completed. " + (allFiles.size()/2) + " mp3 files have been found.");
            FileWriter.writeToFile("files/Searchdirectories.txt", allFiles, false);    //write all files into the Seachdirectories.txt with the following system: absolute file path \n file name \n
        }
    }
    static ArrayList<String> readInputDirectories(){
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
    public static ArrayList<String> getAllFiles(){
        return allFiles;
    }
}
