package FileClasses;

import java.util.ArrayList;

public class ArtistClass {

   private String name;
   private ArrayList<Integer> songIndices; //song indices according to the main song list

   ArtistClass(String Name, int firstSongIndex){
      name = Name;
      songIndices = new ArrayList<>();
      songIndices.add(firstSongIndex);
   }

   void addSongToArtist(int songID){
      songIndices.add(songID);
   }

   String getArtistName(){
      return name;
   }

   ArrayList<Integer> getArtistSongs(){
      return songIndices;
   }

   @Override
   public String toString() {
      String indices = "";
      for(int index : songIndices)
         indices += index+";" ;
      return name + ";" + indices;
   }
}
