package FileClasses;

import java.io.File;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileWriter extends Thread {


   private ArrayList<Musicfile> musicfiles;
   private ArrayList<Integer>[] sortedLists = new ArrayList[3]; //TODO Check whether this can work
   private boolean append, categories;
   private String filepath;

   FileWriter(ArrayList<Musicfile> Musicfiles, boolean Append, boolean Categories, String Filepath) {
      append = Append;
      musicfiles = Musicfiles;
      filepath = Filepath;
      categories = Categories;
   }

   @Override
   public void run() {
     if(!categories)
        writeMusicFilesToFile();
     else
       writeCategoryListsToFile();
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
         writer.println("");
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
