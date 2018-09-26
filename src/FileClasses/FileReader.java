package FileClasses;

import ControllerClasses.textfileMarkers;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class FileReader {

    private ArrayList<String> allFiles = null;

    public boolean readFile(String filepath){
        allFiles = new ArrayList<>();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new java.io.FileReader(new File(filepath)));
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
    public ArrayList<String> getAllFiles(){
        return  allFiles;
    }
}
