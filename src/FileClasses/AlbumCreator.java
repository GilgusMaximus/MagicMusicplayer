package FileClasses;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;


public class AlbumCreator extends Thread{   //Class, which goes through all files, and combines songs from one album into said album

   private ArrayList<Musicfile> musicfiles;  //musivfile from MusicManager
   private ArrayList<AlbumClass> albums;  //actul albums
   private Dictionary<String, Integer> albumNames; //for the registration of albums
   private final String filePath = "../files/Albums.txt";   //path for persistent album save file
   //Fileformat for the persistent .txt file
   // albumTitle\n
   //song1;song2;song3;song4;...
   //\n

   AlbumCreator(ArrayList<Musicfile> Musicfiles){
      musicfiles = Musicfiles;
      albums = new ArrayList<>();
      albumNames = new Hashtable<>();
   }

   @Override
   public void run() {
      if(musicfiles == null) {
         System.err.println("ERROR: AlbumCreator: run(): musicfiles list = null");
         return;
      }
      String currentFileAlbum;
      Musicfile file;
      Integer index;
      //for every musicfile:
      for (int i = 0; i < musicfiles.size(); i++) {
         file = musicfiles.get(i);
         currentFileAlbum = file.getAlbum();
         index = albumNames.get(currentFileAlbum);
         //is the album already registered?
         if(index != null)
            //yes -> add the song index to the album
            albums.get(index).addSongToAlbum(i);
         else{
            //no -> create a new album class and add it to the albums list
            albums.add(new AlbumClass(currentFileAlbum, i));
            //register the album in the hashtable
            albumNames.put(currentFileAlbum, albums.size()-1);
         }
      }
      FileWriter fileWriter = new FileWriter(albums, false, 2, filePath);
      fileWriter.start();
   }
}
