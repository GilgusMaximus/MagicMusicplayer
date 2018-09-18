package FileClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicFileCreator extends  Thread{

    int start, end, position;
    List<String> filesPaths;
    ArrayList<Musicfile> musicFiles;
    PatternMatcher p;
    MusicFileCreator(int Start, int End, List<String> Files){
        start = Start;
        end = End;
        filesPaths = Files;
        position = start;
        p = new PatternMatcher();
        musicFiles = new ArrayList<>();
    }

    public ArrayList<Musicfile> getMusicFiles(){
        return  musicFiles;
    }

    @Override
    public void run(){
        while(position != end){
         //  System.out.println(position + " " + end);
            String path = filesPaths.get(position);
            String[] tags;
            File file = new File(path);
            Musicfile musicfile;
            if(path.substring(path.length()-3).equals("m4a")){
                tags = p.findM4AData(file);
            }else /*if(path.substring(path.length()-3).equals("mp3"))*/{
              System.out.println("HIER");
                tags = p.findMp3Data(file);
            }
            for(int i = 0; i < tags.length; i++){
              System.out.println(tags[i]);
            }
            String[] multArtists = null;
            if(tags[1] != null) {
                multArtists = tags[1].split("/");
            }
            if(multArtists != null && multArtists.length > 1 && multArtists[0].toLowerCase().equals("axwell")){
                multArtists = null;
            }
            if(multArtists == null){
                musicfile = new Musicfile(path, tags[1], tags[2], tags[0], null);
            }else{
                musicfile = new Musicfile(path, multArtists, tags[2], tags[0], null);
            }
            musicFiles.add(musicfile);
            position++;
        }
    }

}
