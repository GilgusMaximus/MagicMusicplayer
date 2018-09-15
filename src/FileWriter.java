import java.io.BufferedWriter;
import java.util.ArrayList;

public class FileWriter {

    public static void writeToFile(String filePath, ArrayList<String> files){ //just write all Strings on a new line into the file
        try {
            BufferedWriter bf = new BufferedWriter(new java.io.FileWriter(filePath));
            for(int i = 0; i < files.size(); i++){
                bf.write(files.get(i));
                bf.newLine();
            }
            bf.close();
        }catch(Exception e){
            System.err.println("EXCEPTION: FileWriter: " + e);
        }
    }
}
