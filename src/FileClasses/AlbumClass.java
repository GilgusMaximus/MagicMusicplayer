package FileClasses;

import java.util.ArrayList;

public class AlbumClass {  //class which is used to store albums

   private String name;
   private ArrayList<Integer> songIndices; //song indices according to the main song list

   AlbumClass(String Name, int firstSongIndex){
      name = Name;
      songIndices = new ArrayList<>();
      songIndices.add(firstSongIndex);
   }

   void addSongToAlbum(int songID){
      songIndices.add(songID);
   }

   String getAlbumName(){
      return name;
   }

   ArrayList<Integer> getAlbum(){
      return songIndices;
   }
}
