package FileClasses;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Musicfile {

   private String filePath = "noData", album = "noData", title = "noData", image = "noData";
   private String[] artists;
    private int index;
   //constructor called when more than 1 artist are read from the file tags
   Musicfile(String path, String[] art, String alb, String tit, String img) {
      if (path != null) {
         filePath = path;
      }
      if (alb != null) {
         album = alb;
      }
      if (tit != null) {
         title = tit;
      }else{
        int lastSlash = 0;
        for(int i = 0; i < path.length(); i++) {
          if (path.charAt(i) == '/' || path.charAt(i) == '\\') {
            lastSlash = i;
          }
          title = path.substring(lastSlash+1);
        }
      }
      if (img != null) {
         if(img.equals("AlbumArtSmall.jpg")) {
            Path a = Paths.get(path);
            a = a.getParent();
            image = a.toString() + "\\AlbumArtSmall.jpg";
         }
         else
            image = img;
      }
      artists = art;
   }

   //constructor called when only one artist is read from the file tags
   Musicfile(String path, String art, String alb, String tit, String img) {
      if (path != null) {
         filePath = path;
      }
      if (alb != null) {
         if(alb.length() > 1 && alb.charAt(0) == 32)
            alb = alb.substring(1);
         album = alb;
      }
      if (tit != null) {
         if(tit.length() > 1 && tit.charAt(0) == 32)
            tit = tit.substring(1);
         title = tit;
      }else{
        int lastSlash = 0;
        for(int i = 0; i < path.length(); i++) {
          if (path.charAt(i) == '/' || path.charAt(i) == '\\') {
            lastSlash = i;
          }
          title = path.substring(lastSlash+1);
          if(title.length() > 1 && title.charAt(0) == 32)
             title = tit.substring(1);
        }
      }
      if (img != null) {
         if(img.equals("AlbumArtSmall.jpg")) {
            Path a = Paths.get(path);
            a = a.getParent();
            image = a.toString() + "\\AlbumArtSmall.jpg";
         }
         else
            image = img;
      }
      artists = new String[1];
      if (art != null) {
         artists[0] = art;
      } else {
         artists[0] = "noData";
      }
   }

   //constructor called when the line is read from a file
   Musicfile(String readLine) {
      String word = "";                                               //current tag
      int count = 0;                                                  //counter for tags array
      String[] tags = new String[4 + (int) readLine.charAt(0) - 48];  //we always have a minimum of 5 tags -> 4 + the amount of artists, indicated by the first char from the text file line
      for (int i = 1; i < readLine.length(); i++) {                   //reading the tags, and saving a tag when the marker is reached - TODO If a tag actually has the char sequence $% it cannot be read properly
         char actualChar = readLine.charAt(i);
         if (actualChar == '$' && readLine.charAt(i + 1) == '%') {
            tags[count] = word;
            word = "";
            count++;
            i++;
         } else {
            word += readLine.charAt(i);
         }
      }
      tags[count] = word;
      //save tags
      artists = new String[tags.length - 4];
      filePath = tags[0];
      title = tags[1];
      album = tags[2];
      image = tags[3];
      for (int i = 4; i < tags.length; i++) {
         artists[i - 4] = tags[i];
      }
   }

   @Override
   public String toString() {
      String artist = artists[0];
      for (int i = 1; i < artists.length; i++) {
         artist += artists[i] + "$%";
      }
      return "" + artists.length + filePath + "$%" + title + "$%" + album + "$%" + image + "$%" + artist;
   }

   public String getFilePath() {
      return filePath;
   }

   public String getAlbum() {
      return album;
   }

   public String getTitle() {
      return title;
   }

   public String getImage() {
      return image;
   }

   public String[] getArtists() {
      return artists;
   }

   public void setIndex(int i){
     if(i >= 0)
      index = i;
   }
   public int getIndex(){
     return index;
   }
   public int compare(String value, int type){
      String ownValue = null;
      switch (type){
         case  0: ownValue = title; break;
         case 1: ownValue = album; break;
         case 2: ownValue = artists[0]; break;
      }
      if(ownValue == null || ownValue.equals(""))  //all songs without specification in this category get tothe back of the list
         return 1;
     for(int i = 0; i < min(ownValue,value); i++){
       if(ownValue.toLowerCase().charAt(i) < value.toLowerCase().charAt(i))
         return -1;
       else if(ownValue.toLowerCase().charAt(i) > value.toLowerCase().charAt(i))
         return 1;
     }
     return 0;
   }
   int min(String a, String b){
     if(a.length() > b.length())
       return b.length();
     return a.length();
   }
}
