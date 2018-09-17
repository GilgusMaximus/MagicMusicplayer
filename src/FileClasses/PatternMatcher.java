package FileClasses;

import java.io.File;
import java.io.RandomAccessFile;

public class PatternMatcher {


    public void findM4AData(File file){
        boolean found = false;
        int index = 0;
        RandomAccessFile r = null;
        byte[] byteArray = null;
        int[] pattern = {-87, 110, 97, 109};
        int value = -87 + 110 + 97 +109;
        try {
            r = new RandomAccessFile(file, "r");
            byteArray = new byte[(int)r.length()];
            r.readFully(byteArray);
        }catch(Exception e){

        }

        int[] currentPattern = {byteArray[0], byteArray[1],byteArray[2],byteArray[3]};
        int actualValue = currentPattern[0]+currentPattern[1]+currentPattern[2]+currentPattern[3];
        for(int i = 1; i < byteArray.length; i++){
                actualValue -= currentPattern[0];
                currentPattern[0]= currentPattern[1];
                currentPattern[1] = currentPattern[2];
                currentPattern[2] = currentPattern[3];
                currentPattern[3] = byteArray[i+3];
                actualValue += currentPattern[3];

                if(actualValue == value){
                    found = true;
                    for(int j = 0; j < 4; j++){
                        if(pattern[j] != currentPattern[j]){
                            found = false;
                            break;
                        }
                    }
                }
                if(found){
                    index = i;
                    break;
                }
        }
        System.out.println("INDEX: " + index);
    }

}
