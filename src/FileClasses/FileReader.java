package FileClasses;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

class FileReader {

    private ArrayList<String> allFiles = null;

    boolean readFile(String filepath){
        allFiles = new ArrayList<>();
        File file = new File(filepath);
        if(!file.exists())
            return false;

        try {
            BufferedReader reader;
            reader = new BufferedReader(new java.io.FileReader(file));
            String currentLine;
            while((currentLine = reader.readLine()) != null){//read all lines
                allFiles.add(currentLine);
            }
        }catch(Exception e){
           System.err.println("ERROR: FileClasses: FileReader: " + e);
           return false;
        }
        return true;
    }
    ArrayList<String> getAllFiles(){
        return  allFiles;
    }
}
