import java.io.BufferedWriter;
import java.util.ArrayList;

public class FileWriter {

    BufferedWriter bf;

    public static void writeToFile(String filePath, ArrayList<String> files){
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
