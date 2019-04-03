package ControllerClasses;

import FileClasses.AlbumCreator;
import FileClasses.ArtistCreator;
import FileClasses.FileReader;
import FileClasses.FileWriter;
import FileClasses.InputReader;
import FileClasses.Musicfile;
import com.sun.istack.internal.NotNull;
import fxml.Controller;
import fxml.DirectoryUIController;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

   private final int loopNothing = 0;
   private final int loopSong = 2;
   private int loopStatus = loopNothing;
   private Controller uiController;
   private Stage windowStage;
   private DirectoryUIController directoryUIController;
   private boolean newMusicFiles = false;
   private ArrayList<Integer> activeSortedList;
   private Media songMedia;
   private Scene scene;
   public static void main(String[] args) {
     InputReader.readInput();
      Application.launch();
   }

   public void start(Stage primaryStage) {
      MediaView mediaView;

      FXMLLoader loader = new FXMLLoader(getClass().getResource("directoryChoose.fxml"));
      Parent root = null;
      try {
         root = loader.load();
      } catch (IOException e) {
         System.err.println("ERROR: MusicManager: directoryUI FXML Load: " + e);
      }
      directoryUIController = loader.getController();
      //Creating a scene object
      scene = new Scene(root, 600, 300);
      mediaView = new MediaView();
      ((Group) scene.getRoot()).getChildren().add(mediaView);
      //Setting title to the Stage
      primaryStage.setTitle("Magic Musicplayer");
      scene.setFill(null);
      //Adding scene to the stage
      primaryStage.setScene(scene);
      primaryStage.setWidth(648);
      primaryStage.setHeight(457);
      primaryStage.setMaxHeight(457);
      primaryStage.setMaxWidth(648);
      primaryStage.setMinHeight(457);
      primaryStage.setMinWidth(648);

      directoryUIController.setMusicManager(this);
      FileReader reader = new FileReader();
      if(reader.readFile("files/directories.txt")) {
         directoryUIController.setAlreadyScannedDirectories(reader.getAllFiles());
      }else {
         directoryUIController.setAlreadyScannedDirectories(null);
      }

      //Displaying the contents of the stage
      primaryStage.show();
      windowStage = primaryStage;

   }

   public void activateMusicWindows(ArrayList<String> newDirectories, ArrayList<String> alreadyScannedDirectories){
      FileWriter writer = new FileWriter(newDirectories, true, 3, "files/directories.txt");
      writer.start();
      MediaView mediaView;
      musicFiles = InputReader.getMusicFiles();
      newMusicFiles = InputReader.getnewMusic();
      musicQueue = new ArrayList<>();

      for(int i = 0; i < musicFiles.size(); i++){
         addSongToEndOfQueue(i);
      }
      Sorter sorter = new Sorter(musicFiles);
      sorter.start();

      Parent root = null;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
      try {
         root = loader.load();
      } catch (Exception e) {
         System.err.println("ERROR: MusicManager: LOAD FXM " + e);
         System.exit(-1);
      }
      uiController = loader.getController();
      uiController.setManager(this);

      //Creating a scene object
      @NotNull Scene scene = new Scene(root, 600, 300);
      mediaView = new MediaView();
      ((Group) scene.getRoot()).getChildren().add(mediaView);
      //Setting title to the Stage
      //Adding scene to the stage
      windowStage.setScene(scene);

      //Displaying the contents of the stage
      windowStage.show();

      try {
         sorter.join();
         activeSortedList = sorter.getArtistSortedList();
      }catch(Exception e){
         System.err.println("MusicManager: Start: sorter.join: " + e);
      }
      AlbumCreator albumCreator = new AlbumCreator(musicFiles, sorter.getAlbumSortedList());
      albumCreator.start();
      ArtistCreator artistcreator = new ArtistCreator(musicFiles, sorter.getArtistSortedList());
      artistcreator.start();

      setMediaPlayerMedia();
      play();
      // System.out.println("SONG DURATION?: " + currentSongmediaPlayer.getTotalDuration().toString());

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
      if(currentSongmediaPlayer != null) {
         currentSongmediaPlayer.stop();
         currentSongmediaPlayer.dispose();
      }
      currentSongmediaPlayer = null;
      File f = new File(musicFiles.get(activeSortedList.get(currentSongInQueue)).getFilePath());
      songMedia = createMedia(f);
      currentSongmediaPlayer = new MediaPlayer(songMedia);
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
      currentSongmediaPlayer.setOnReady(new Runnable() {

         @Override
         public void run() {
            System.out.println("Duration: "+currentSongmediaPlayer.getMedia().getDuration().toSeconds());
         }
      });
      System.gc();
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
     uiController.setSongTitle(musicFiles.get(activeSortedList.get(index)).getTitle());
      uiController.setSongAlbum(musicFiles.get(activeSortedList.get(index)).getAlbum());
      uiController.setSongArtist(musicFiles.get(activeSortedList.get(index)).getArtists()[0]);
   }

   //checks what kind of musicfile is going to start, and accordingly to type is using different methods to try to read the cover image based on the encoding of the file
   private void setDisplayedImage(int index){
      Musicfile currentSong = musicFiles.get(activeSortedList.get(index));
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
            System.err.println("ERROR: MusicManager: setDisplayedImage: MP3 Cover: " + e);
         }
      }else{  //m4a files
         FileInputStream inputstream = null;
         try {
            inputstream = new FileInputStream(musicFiles.get(activeSortedList.get(index)).getImage()); //open the needed image as FileStream
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
      currentSongInQueue = index;
      setMediaPlayerMedia();
      setDisplayedTexts(index);
      setDisplayedImage(index);
      currentSongmediaPlayer.play();
      currentSongInQueue = index;
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
      return new Media(file.toURI().toString());
   }

   public int getNumberOfSongs(){
      return musicFiles.size()-1;
   }
   public Musicfile getMusicfileAtPosition(int position){
      return musicFiles.get(activeSortedList.get(position));
   }
}