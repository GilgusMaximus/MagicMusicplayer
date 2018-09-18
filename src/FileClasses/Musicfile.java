package FileClasses;

public class Musicfile {

    private String filePath = "noData", album = "noData", title = "noData", image = "noData";
    private String[] artists;
    public Musicfile(String path, String[] art, String alb, String tit, String img){
      if(path != null)
        filePath = path;
      if(alb != null)
        album = alb;
      if(tit != null)
        title = tit;
      if(img != null)
        image = img;
      artists = art;
    }
    public Musicfile(String path, String art, String alb, String tit, String img){
      if(path != null)
      filePath = path;
      if(alb != null)
        album = alb;
      if(tit != null)
        title = tit;
      if(img != null)
        image = img;
        artists = new String[1];
      if(art != null)
          artists[0] = art;
      else
          artists[0] = "noData";
    }
    public Musicfile(String readLine){
        String word = "";
        int count = 0;
        String[] tags = new String[4 + (int) readLine.charAt(0) - 48];
        for(int i = 1; i < readLine.length(); i++){
          char actualChar = readLine.charAt(i);
          if(actualChar == '$' && readLine.charAt(i+1) == '%'){
            tags[count] = word;
            word = "";
            count++;
            i++;
          }else{
            word += readLine.charAt(i);
          }
        }
      artists = new String[tags.length-4];
      filePath = tags[0];
    }

    @Override
    public String toString(){
        String artist = artists[0];
        for(int i = 1; i < artists.length; i++)
            artist += artists[i];
        String fileAsString = "" + artists.length + filePath  + "$%" + title + "§%" + album + "§%" + image + "$%" + artist;
        return  fileAsString;
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
}
