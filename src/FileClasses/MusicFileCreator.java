package FileClasses;

import java.util.ArrayList;
import java.util.List;

public class MusicFileCreator extends Thread {

   private List<String> encodedMusicFiles;
   private ArrayList<Musicfile> musicFiles;

   MusicFileCreator(ArrayList<String> EncodedMusicFiles) {
      musicFiles = new ArrayList<>();
      encodedMusicFiles = EncodedMusicFiles;
   }

   ArrayList<Musicfile> getMusicFiles() {
      return musicFiles;
   }

   @Override
   public void run() {
      if (encodedMusicFiles != null) {
         for (int i = 0; i < encodedMusicFiles.size(); i++) {
            musicFiles.add(new Musicfile(encodedMusicFiles.get(i)));
         }
      }
   }

}
