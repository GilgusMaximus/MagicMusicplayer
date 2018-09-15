import javafx.application.Application;

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
            String currentLine = reader.readLine();

            if(!(currentLine.substring(0, textfileMarkers.searchDirectoryMarker.length()).equals(textfileMarkers.searchDirectoryMarker))) { //Check whether file starts with correct marker
                System.err.println("Eine Appdatei wurde beschädigt. Korrektes Lesen nicht mehr möglich. Bitte zu durchsuchende Ordner erneut angeben");
                return false;
            }

            System.out.println("Die folgenden Directories mit allen subdirectories wurden bereits in einer frueheren Sitzung gescannt");
            System.out.println(currentLine.substring(textfileMarkers.searchDirectoryMarker.length()));

            while((currentLine = reader.readLine()) != null){//read all lines
                //is the current line a marker line or a file line?
                if(currentLine.length() > textfileMarkers.searchDirectoryMarker.length() && currentLine.substring(0, textfileMarkers.searchDirectoryMarker.length()).equals(textfileMarkers.searchDirectoryMarker)) {
                    //marker line -> print the search directory that was entered when first searc happeared
                    System.out.println(currentLine.substring(24));
                }else{
                    //file line -> save the file paths
                    allFiles.add(currentLine);
                }
            }
        }catch(Exception e){
           System.err.println("ERROR: FileReader: " + e);
        }
        return true;
    }
    public ArrayList<String> getAllFiles(){
        return  allFiles;
    }
}
