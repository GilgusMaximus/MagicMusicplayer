package FileClasses;

public class Musicfile {

    private String filePath, album, title, image;
    private String[] artists;
    public Musicfile(String path, String[] art, String alb, String tit, String img){
        filePath = path;
        artists = art;
        album = alb;
        title = tit;
        image = img;
    }
    public Musicfile(String path, String art, String alb, String tit, String img){
        filePath = path;
        album = alb;
        title = tit;
        image = img;
        artists = new String[1];
        if(art == null) {
            artists[0] = "noData";
        }else{
            artists[0] = art;
        }
    }

    @Override
    public String toString(){
        String artist = artists[0];
        for(int i = 1; i < artists.length; i++)
            artist += artists[i];
        String fileAsString = filePath + "\n" + artist + "\n" + title + "\n" + album + "\n" + image;
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
