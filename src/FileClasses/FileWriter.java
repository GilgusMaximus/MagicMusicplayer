package FileClasses;

import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileWriter extends Thread {


   private ArrayList fileList;
   private ArrayList<Integer>[] sortedLists = new ArrayList[3]; //TODO Check whether this can work
   private boolean append;
   private int fileType;
   private String filepath;

   public FileWriter(ArrayList FileList, boolean Append, int file, String Filepath) {
      append = Append;
      fileList = FileList;
      filepath = Filepath;
      fileType = file;
   }

   @Override
   public void run() {
     if(fileType == 0)
        writeMusicFilesToFile();
     else if(fileType == 1)
       writeCategoryListsToFile();
     else if(fileType == 2)
        writeAlbumsToFile();
     else if(fileType == 3)
        writeSearchedDirectoriesToFile();
     else
        System.err.println("ERROR: FileWriter: incorrect fileType number");
   }

   private void writeSearchedDirectoriesToFile(){
      checkFile();
      if(fileList == null || !(fileList.get(0) instanceof  String)){
         System.err.println("ERROR: FileWriter: writeDirectoriesToFile: fileList = NULL OR fileList not of type ALBUMCLASS");
         return;
      }
      ArrayList<String> directories = fileList; //this is fine, as it is checked before, that it is the correct filelist
      BufferedWriter bf = createBufferedWriter();
      try{
         System.out.println("WRITING album files");
         for(String directory : directories){
            bf.write(directory);
            bf.newLine();
         }
         bf.close();
      }catch(IOException e){
         System.err.println("IO ERROR: FileWriter: writeDirectoriesToFile: " + e);
      }

   }

   private void writeAlbumsToFile(){ //writes the list of albums with the corresponding songs to a file
      checkFile();
      if(fileList == null || !(fileList.get(0) instanceof  AlbumClass)){
         System.err.println("ERROR: FileWriter: writeAlbumsToFile: fileList = NULL OR fileList not of type ALBUMCLASS");
         return;
      }
      ArrayList<AlbumClass> albumfiles = fileList; //this is fine, as it is checked before, that it is the correct filelist
      BufferedWriter bf = createBufferedWriter();
      try{
         System.out.println("WRITING album files");
         for(AlbumClass album : albumfiles){
            bf.write(album.toString());
            bf.newLine();
         }
         bf.close();
      }catch(IOException e){
         System.err.println("IO ERROR: FileWriter: writeAlbumsToFile: " + e);
      }
   }

   private void writeCategoryListsToFile(){
        checkFile();
        BufferedWriter bf = createBufferedWriter();
        try{
          for(ArrayList<Integer> a : sortedLists){
            for(int s : a){
              bf.write(s);
              bf.newLine();
            }
          }
        }catch (Exception e){
          System.out.println("ERROR: FileWriter: writeCategory(): " + e);
        }
   }

   private void checkFile(){
      try {
         if (!Files.exists(Paths.get("./files"))) {
            Files.createDirectory(Paths.get("./files"));
            System.out.println("Directory fiels erzeugt");
         }
      }catch(IOException e){
         System.err.println("ERROR: FileWriter: checkFile: createDirectory: " + e);
      }
     File file = new File(filepath);
     if (!file.exists()) {
       try {
         PrintWriter writer = new PrintWriter(filepath, "UTF-8");
         //writer.println("");
         writer.close();
       } catch (Exception e) {
         System.out.println("ERROR: FileWriter: checkFile(): " + e);
       }
     }
   }

   private BufferedWriter createBufferedWriter(){
     BufferedWriter bf = null;
     try{
       if (append) {
         bf = new BufferedWriter(new java.io.FileWriter(filepath, true));
       } else {
         bf = new BufferedWriter(new java.io.FileWriter(filepath));
       }
       bf.flush();
     }catch(Exception e){
       System.out.println("ERROR: FileWriter: createBW(): " + e);
     }
     return bf;
   }

   private void writeMusicFilesToFile() { //just write all Strings on a new line into the file
      checkFile();
      if(fileList == null || fileList.size() == 0 || !(fileList.get(0) instanceof  Musicfile)){
         System.err.println("ERROR: FileWriter: wirteMusicFilesToFile: fileList = null OR fileList not of type MUSICFILE");
         return;
      }

      ArrayList<Musicfile> musicfiles = fileList;  //this is fine
      BufferedWriter bf = createBufferedWriter();
      try {
        System.out.println("WRITING ");
        for(Musicfile m : musicfiles){
              bf.write(m.toString());
              bf.newLine();
        }
        bf.close();
      } catch (IOException e) {
         System.err.println("IO EXCEPTION: FileClasses.FileWriter: " + e);
      }
   }
}
