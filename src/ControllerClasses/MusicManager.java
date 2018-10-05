package ControllerClasses;

import FileClasses.InputReader;
import FileClasses.Musicfile;
import com.sun.istack.internal.NotNull;
import fxml.Controller;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import mp3magic.ID3v2;
import mp3magic.Mp3File;

public class MusicManager extends Application {

   private ArrayList<Integer> musicQueue;
   private ArrayList<Musicfile> musicFiles;
   private int currentSongInQueue = 0;
   private MediaPlayer currentSongmediaPlayer;


   private MediaView mediaView;
   private final int loopNothing = 0;
   private final int loopSong = 2;
   private int loopStatus = loopNothing;
   private Controller uiController;
   public static void main(String[] args) {
      InputReader.readInput();
      Application.launch();
   }

   public void start(Stage primaryStage) {
      musicFiles = InputReader.getMusicFiles();

      musicQueue = new ArrayList<>();

      for(int i = 0; i < musicFiles.size(); i++){
         addSongToEndOfQueue(i);
      }

      Parent root = null;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ui.fxml"));
      try {
         root = loader.load();
      } catch (Exception e) {
         System.err.println("ERROR: MusicManager: LOAD FXM " + e);
      }
      uiController = loader.getController();
      uiController.setManager(this);

      // Creating a scene object
      @NotNull Scene scene = new Scene(root, 600, 300);
      mediaView = new MediaView();
      ((Group) scene.getRoot()).getChildren().add(mediaView);
      //Setting title to the Stage
      primaryStage.setTitle("Event Filters Example");
      primaryStage.initStyle(StageStyle.UNDECORATED);

      //Adding scene to the stage
      primaryStage.setScene(scene);
      primaryStage.setWidth(640);
      primaryStage.setHeight(360);
      //primaryStage.setMaximized(true);

      //Displaying the contents of the stage
      primaryStage.show();
      // primaryStage.toFront();
      setMediaPlayerMedia();
      play();
      setDisplayedImage(currentSongInQueue);
      setDisplayedTexts(currentSongInQueue);
      uiController.buttonSetup();
   }

   private void addSongToEndOfQueue(int songIndex) {
      musicQueue.add(songIndex);        //add the MediaPlayer to the play queue
   }


   private void addSongNext(int songIndex) {  //play the song as next
      if (currentSongInQueue == musicQueue.size()) {  //is the current song the last one in queue?
         //yes -> add the song to the end of the queue
         musicQueue.add(songIndex);
      } else {
         //no -> move all songs in queue 1 position back, and add the song to the free position
         int value = songIndex;
         for (int i = currentSongInQueue + 1; i < musicQueue.size(); i++) {
            int help = musicQueue.get(i);
            musicQueue.set(i, value);
            value = help;
         }
         musicQueue.add(value);
      }
   }

   private void setMediaPlayerMedia() {
      if(currentSongmediaPlayer != null)
         currentSongmediaPlayer.stop();
      File f = new File(musicFiles.get(currentSongInQueue).getFilePath());
      Media m = createMedia(f);
      currentSongmediaPlayer = new MediaPlayer(m);
   }

   private void playNextSongInQueue() {
      System.out.println("'Play Next' pressed");
      currentSongInQueue = (currentSongInQueue + 1) % musicQueue.size();
      setMediaPlayerMedia();
      play();
      setDisplayedImage(currentSongInQueue);
      setDisplayedTexts(currentSongInQueue);
   }

   private void setDisplayedTexts(int index){
     uiController.setSongTitle(musicFiles.get(index).getTitle());
      uiController.setSongAlbum(musicFiles.get(index).getAlbum());
      uiController.setSongArtist(musicFiles.get(index).getArtists()[0]);
   }

   //checks what kind of musicfile is going to start, and accordingly to type is using different methods to try to read the cover image based on the encoding of the file
   private void setDisplayedImage(int index){
      Musicfile currentSong = musicFiles.get(index);
      if(currentSong.getImage().equals("Image")){ //mp3 files
         try {
            Mp3File mp3File = new Mp3File(currentSong.getFilePath());
            ID3v2 songIdv2Tags = mp3File.getId3v2Tag();
            byte[] imageByteArray = songIdv2Tags.getAlbumImage();
            if(imageByteArray != null) {
               InputStream inputStream = new ByteArrayInputStream(imageByteArray);
               uiController.setSongThumbnail(new Image(inputStream));  //set the image that is shown in the ui
            }else{
               setDisplayedImageToStandard();
            }
         }catch(Exception e){
            System.out.println("ERROR: MusicManager: setDisplayedImage: MP3 Cover: " + e);
         }
      }else{  //m4a files
         FileInputStream inputstream = null;
         try {
            inputstream = new FileInputStream(musicFiles.get(index).getImage()); //open the needed image as FileStream
         }catch(Exception e){
            System.err.println("ERROR: MusicManager: setDisplayedImage: inputStream: " + e);
         }
         if(inputstream != null)
           uiController.setSongThumbnail(new Image(inputstream));  //set the image that is shown in the ui
        else
          setDisplayedImageToStandard();
      }
   }

   //sets the image that is displayed to a filler picture
   private void setDisplayedImageToStandard(){
     FileInputStream inputstream = null;
     try {
       inputstream = new FileInputStream("src/fxml/pictures/standardcover.png"); //open the needed image as FileStream
     }catch(Exception e){
       System.err.println("ERROR: MusicManager: setDisplayedImage: inputStream: " + e);
     }
     if(inputstream != null)
       uiController.setSongThumbnail(new Image(inputstream));  //set the image that is shown in the ui
   }

   private void playPreviousSongInQueue() {
      System.out.println("'Play Previous' pressed");
      if (currentSongInQueue == 0 && musicQueue.size() > 0) {
         currentSongInQueue = musicQueue.size() - 1;
      } else {
         currentSongInQueue--;
      }
      setMediaPlayerMedia();
      setDisplayedTexts(currentSongInQueue);
      setDisplayedImage(currentSongInQueue);
      play();
   }
   public void playSongOnIndex(int index){
      File fiel = new File(musicFiles.get(index).getFilePath());
      Media a = createMedia(fiel);
      currentSongmediaPlayer.stop();
      currentSongmediaPlayer = new MediaPlayer(a);
      currentSongmediaPlayer.setOnEndOfMedia(() -> { //method called when the song ends
         if (loopStatus == loopSong) { //are we currently looping the song?
            //yes -> reset the player and play again
            currentSongmediaPlayer.seek(Duration.ZERO);
            currentSongmediaPlayer.play();
         } else {
            //no -> see method for explanation
            songAtEndCheckNextPlay();
         }
      });
      setDisplayedTexts(index);
      setDisplayedImage(index);
      currentSongmediaPlayer.play();
   }
   private void songAtEndCheckNextPlay() { //at the end of the queue check which looping type is active
      if (currentSongInQueue == musicQueue.size() - 1 && loopStatus == loopNothing) {  //not looping?
         //yes -> set the current song to the first one in queue, but do not start playing
         currentSongInQueue = 0;
      } else {
         //no -> play next song which is the first on queue
         playNextSongInQueue();
      }
   }

   //methods called from UI
   public void play() {
      if (currentSongmediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
         currentSongmediaPlayer.pause();
      } else {
         currentSongmediaPlayer.play();
      }
   }

   public void playNextButtonHit() {
      playNextSongInQueue();
   }

   public void playPreviousButtonHit() {
      playPreviousSongInQueue();
   }

   public void loop() {
      loopStatus = (loopStatus + 1) % 3;
      System.out.println("'Loop' pressed - Current Status: " + loopStatus);
   }

   //often used create methods
   private Media createMedia(File file) {
      final String source = file.toURI().toString();
      return new Media(source);
   }

   public int getNumberOfSongs(){
      return musicFiles.size()-1;
   }
   public Musicfile getMusicfileAtPosition(int position){
      return musicFiles.get(position);
   }
}