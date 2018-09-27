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
   private boolean append;
   private String filepath;

   FileWriter(ArrayList<Musicfile> Musicfiles, boolean Append, String Filepath) {
      append = Append;
      musicfiles = Musicfiles;
      filepath = Filepath;
   }

   @Override
   public void run() {
      writeToFile();
   }

   private void writeToFile() { //just write all Strings on a new line into the file
      File file = new File(filepath);
      if (!file.exists()) {
         try {
            PrintWriter writer = new PrintWriter(filepath, "UTF-8");
            writer.println("");
            writer.close();
         }catch(Exception e){

         }
      }
      try {
         BufferedWriter bf;
         if (append) {
            bf = new BufferedWriter(new java.io.FileWriter(filepath, true));
         } else {
            bf = new BufferedWriter(new java.io.FileWriter(filepath));
         }
         bf.flush();
         //if (musicfiles.size() > writeBegin) {  //We do not have to write if there is no new data
         System.out.println("WRITING ");
            for (int i = 0; i < musicfiles.size(); i++) {
               bf.write(musicfiles.get(i).toString());
               bf.newLine();
            }
         //}
         bf.close();
      } catch (Exception e) {
         System.err.println("EXCEPTION: FileClasses.FileWriter: " + e);
      }
   }
}
