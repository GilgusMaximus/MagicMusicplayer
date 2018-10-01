package FileClasses;

import java.nio.file.Path;
import java.nio.file.Paths;
import mp3magic.ID3v1;
import mp3magic.ID3v2;
import mp3magic.Mp3File;

import java.io.File;
import java.io.RandomAccessFile;

class PatternMatcher {

   //file has to be an m4a file
   String[] findM4AData(File file) throws RuntimeException {
      int index = 0;
      final int value = -87 + 110 + 97 + 109;
      boolean found = false;

      final int[] pattern = {-87, 110, 97, 109};
      int[] tagKeyIndices = new int[3]; //array of indices pointing towards the beginning of the tag key
      byte[] byteArray;
      String[] Tags = new String[4];  //array of tag values (4)

      RandomAccessFile randomFile;

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
      for (int i = 1; i < byteArray.length - 3; i++) {
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
      Path a = Paths.get(file.getPath());
      a = a.getParent();
      String[] images = new File(a.toString()).list();
      for(String iFile : images)
         if (iFile.equals("AlbumArtSmall.jpg")) {
            Tags[3] = "AlbumArtSmall.jpg";
            break;
         }
      return Tags;
   }

   private String readtag(int index, byte[] data) {
      String Tag = "";
      index += 20;  //Offset of tag value to tag key - always 20 bytes
      while (data[index] != 0) {
         Tag += (char) data[index];
         index++;
      }
      return Tag;
   }

   String[] findMp3Data(File file) {

      String[] tags = new String[4];
      try {
         Mp3File mp3 = new Mp3File(file);
         if (mp3.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3.getId3v1Tag();
            tags[0] = id3v1Tag.getTitle();
            tags[1] = id3v1Tag.getArtist();
            tags[2] = id3v1Tag.getAlbum();
            tags[3] = "Image";
         } else if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            tags[0] = id3v2Tag.getTitle();
            tags[1] = id3v2Tag.getArtist();
            tags[2] = id3v2Tag.getAlbum();
            tags[3] = "Image";
         }

      } catch (Exception e) {
         System.err.println("ERROR: PatternMatcher: findMp3Data(): " + e);
      }
      return tags;
   }
}
