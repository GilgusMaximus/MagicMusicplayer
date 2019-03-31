package FileClasses;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ArtistCreator extends Thread{

   ArrayList<Musicfile> musicfiles;
   ArrayList<Integer> musicfilesIndices;
   private ArrayList<AlbumClass> artists;  //actul albums
   private Dictionary<String, Integer> artistNames; //for the registration of artists
   private final String filePath = "files/Artists.txt";   //path for persistent artist save file
   //Fileformat for the persistent .txt file
   // artistname; song1;song2;song3;song4;...\n

   public ArtistCreator(ArrayList<Musicfile> Musicfiles, ArrayList<Integer> MusicfilesIndices){
      musicfiles = Musicfiles;
      artists = new ArrayList<>();
      artistNames = new Hashtable<>();
      musicfilesIndices = MusicfilesIndices;
   }

   @Override
   public void run() {
      if(musicfiles == null) {
         System.err.println("ERROR: ArtistCreator: run(): musicfiles list = null");
         return;
      }
      String currentFileArtist;
      Musicfile file;
      Integer index;
      //for every musicfile:
      for (int i = 0; i < musicfiles.size(); i++) {
         file = musicfiles.get(musicfilesIndices.get(i));
         currentFileArtist= file.getArtists()[0];
         index = artistNames.get(currentFileArtist);
         //is the album already registered?
         if(index != null)
            //yes -> add the song index to the album
            artists.get(index).addSongToAlbum(i);
         else{
            //no -> create a new album class and add it to the albums list
            artists.add(new AlbumClass(currentFileArtist, i));
            //register the album in the hashtable
            artistNames.put(currentFileArtist, artists.size()-1);
         }
      }
      FileWriter fileWriter = new FileWriter(artists, false, 2, filePath);
      fileWriter.start();
   }
}
