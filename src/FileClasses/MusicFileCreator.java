package FileClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicFileCreator extends  Thread{

    List<String> encodedMusicFiles;
    ArrayList<Musicfile> musicFiles;
    PatternMatcher p;
    MusicFileCreator(ArrayList<String> EncodedMusicFiles){
        musicFiles = new ArrayList<>();
        encodedMusicFiles = EncodedMusicFiles;
    }

    public ArrayList<Musicfile> getMusicFiles(){
        return  musicFiles;
    }

    @Override
    public void run(){
      if(encodedMusicFiles != null) {
        for (int i = 0; i < encodedMusicFiles.size(); i++) {
          musicFiles.add(new Musicfile(encodedMusicFiles.get(i)));
        }
      }
    }

}
