package FileClasses;

import java.io.File;
import java.io.RandomAccessFile;

public class PatternMatcher {



    /*----------------------------------------------------------------------------------------------------
                                                Information

                What this class is capable of:
                You give it a m4a file, and it will return you a String array of size 3 with the following tag value of that file:
                0: Song name
                1: Artist name
                2: Album name

                Usage:
                To use this class, make an instance of the class and call findM4AData(file).
                The parameter has to be a file of type m4a.

                I would be happy, if you credit the author (me), somewhere in your project, or link to the github page of this class,
                but if you do not want to do this, your are free to leave it out.

     ----------------------------------------------------------------------------------------------------*/

    //file has to be an m4a file
    public String[] findM4AData(File file) throws RuntimeException{
        int index = 0;
        final int value = -87 + 110 + 97 + 109;
        boolean found = false;

        final int[] pattern = {-87, 110, 97, 109};
        int[] tagKeyIndices = new int[3]; //array of indices pointing towards the beginning of the tag key
        byte[] byteArray = null;
        String[] Tags = new String[3];  //array of tag values

        RandomAccessFile randomFile = null;

        //read the bytes of the file
        try {
            randomFile = new RandomAccessFile(file, "r");
            byteArray = new byte[(int) randomFile.length()];
            randomFile.readFully(byteArray);
        } catch (Exception e) {
            System.err.println("ERROR: PatternMatcher: " + e);
            throw new RuntimeException();   //just to stop further processing, which could lead to app crash
        }

        int[] currentPattern = {byteArray[0], byteArray[1], byteArray[2], byteArray[3]};
        int actualValue = currentPattern[0] + currentPattern[1] + currentPattern[2] + currentPattern[3];

        //using the sum of the 4 bytes representing the desired first tagkey as test for pattern matching - first tag to find is always song name
        for (int i = 1; i < byteArray.length-3; i++) {
            actualValue -= currentPattern[0]; //subtract the value that is no longer withing the pattern
            currentPattern[0] = currentPattern[1];
            currentPattern[1] = currentPattern[2];
            currentPattern[2] = currentPattern[3];
            currentPattern[3] = byteArray[i + 3];
            actualValue += currentPattern[3]; //add the value that is new to the pattern

            if (actualValue == value) { //does the value equal the predefined value?
                //yes -> detailed check of all 4 bytes
                found = true;
                for (int j = 0; j < 4; j++) {
                    if (pattern[j] != currentPattern[j]) {
                        found = false;
                        break;
                    }
                }
            }
            if (found) {    //all bytes correct?
                //yes -> save index and leave loop
                index = i;
                break;
            }
        }

        tagKeyIndices[0] = index;
        int i = index + 1;
        int counter = 1;
        int in = 1;
        //search for the other 2 desired tag keys in following order : Artist, Album
        //because there is another tag between, which is often not filled, or irrelevant for normal users, it mus tbe skipped
        while (counter != 4) {
            if (byteArray[i] == -87 && (counter == 1 || counter == 3)) {
                tagKeyIndices[in] = i;
                in++;
                counter++;
            } else if (byteArray[i] == -87) {
                counter++;
            }
            i++;
        }
        //gather the actual tag values
        Tags[0] = readtag(tagKeyIndices[0], byteArray);
        Tags[1] = readtag(tagKeyIndices[1], byteArray);
        Tags[2] = readtag(tagKeyIndices[2], byteArray);
        return Tags;
    }
    private String readtag(int index, byte[] data){
        String Tag = "";
        index += 20;  //Offset of tag value to tag key - always 20 bytes
        while(data[index] != 0){
            Tag += (char)data[index];
            index++;
        }
        return Tag;
    }
}
