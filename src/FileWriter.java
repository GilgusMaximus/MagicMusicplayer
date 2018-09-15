import java.io.BufferedWriter;
import java.util.ArrayList;

public class FileWriter {

    public static void writeToFile(String filePath, ArrayList<String> files, boolean append){ //just write all Strings on a new line into the file
        try {
            BufferedWriter bf;
            if(append)
                bf = new BufferedWriter(new java.io.FileWriter(filePath, true));
            else
                bf = new BufferedWriter(new java.io.FileWriter(filePath));

            for(int i = 0; i < files.size(); i++){
                bf.write(files.get(i));
                bf.newLine();
            }
            bf.close();
        }catch(Exception e){
            System.err.println("EXCEPTION: FileWriter: " + e);
        }
    }
    public static void writeToFile(String filePath, String files, boolean append){ //just write all Strings on a new line into the file
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
            System.err.println("EXCEPTION: FileWriter: " + e);
        }
    }
}
