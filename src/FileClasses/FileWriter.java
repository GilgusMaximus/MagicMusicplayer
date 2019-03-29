package FileClasses;

import java.io.File;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileWriter extends Thread {


   private ArrayList fileList;
   private ArrayList<Integer>[] sortedLists = new ArrayList[3]; //TODO Check whether this can work
   private boolean append;
   int fileType;
   private String filepath;

   FileWriter(ArrayList Musicfiles, boolean Append, int file, String Filepath) {
      append = Append;
      fileList = Musicfiles;
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
     else
        System.err.println("ERROR: FileWriter: incorrect fileType number");
   }

   private void writeAlbumsToFile(){ //writes the list of albums with the corresponding songs to a file

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
      if(fileList == null || !(fileList.get(0) instanceof  Musicfile)){
         System.err.println("ERROR: FileWriter: fileList = null OR fileList not of type MUSICFILE");
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
      } catch (Exception e) {
         System.err.println("EXCEPTION: FileClasses.FileWriter: " + e);
      }
   }
}
