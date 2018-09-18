package FileClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;

public class FileWriter extends Thread{


  private ArrayList<Musicfile> musicfiles;
  private boolean append;
  private  String filepath;
  private int writeBegin;
  FileWriter(ArrayList<Musicfile> Musicfiles, boolean Append, String Filepath, int WriteBegin){
    append = Append;
    musicfiles = Musicfiles;
    filepath = Filepath;
    writeBegin = WriteBegin;
  }

   @Override
   public void run(){
    writeToFile();
   }

    private void writeToFile(){ //just write all Strings on a new line into the file
        try {
            BufferedWriter bf;
            if(append)
                bf = new BufferedWriter(new java.io.FileWriter(filepath, true));
            else
                bf = new BufferedWriter(new java.io.FileWriter(filepath));
            if(musicfiles.size() > writeBegin) {  //We do not have to write if tehre is no new data
              for (int i = writeBegin; i < musicfiles.size(); i++) {
                bf.write(musicfiles.get(i).toString());
                bf.newLine();
              }
            }
            bf.close();
        }catch(Exception e){
            System.err.println("EXCEPTION: FileClasses.FileWriter: " + e);
        }
    }
    private  void writeToFile(String filePath, String files, boolean append){ //just write all Strings on a new line into the file
        try {
            BufferedWriter bf;
            if(append)
                bf = new BufferedWriter(new java.io.FileWriter(filePath, true));
            else
                bf = new BufferedWriter(new java.io.FileWriter(filePath));
            bf.write(files);
            bf.newLine();
            bf.close();
        }catch(Exception e){
            System.err.println("EXCEPTION: FileClasses.FileWriter: " + e);
        }
    }
}
