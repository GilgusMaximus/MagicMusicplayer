package ControllerClasses;

import FileClasses.AlbumCreator;
import FileClasses.ArtistCreator;
import FileClasses.FileReader;
import FileClasses.FileWriter;
import FileClasses.InputReader;
import FileClasses.Musicfile;
import fxml.Controller;
import fxml.DirectoryUIController;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import com.sun.istack.internal.NotNull;
import java.util.ArrayList;

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
   private ArrayList<Integer> activeSortedList; //the list, which decides, how the queue of all songs is sorted


   public static void main(String[] args) {
      Application.launch();
   }

   //starts the application
   public void start(Stage primaryStage) {
      Parent root = null;
      Scene scene;
      MediaView mediaView = new MediaView();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("directoryChoose.fxml"));
      FileReader reader = new FileReader();
      DirectoryUIController directoryUIController;

      //try to get the root form the UI
      try {
         root = loader.load();
      } catch (IOException e) {
         System.err.println("ERROR: MusicManager: directoryUI FXML Load: " + e);
         System.exit(-1);
      }

      //Creating a scene object and adding the base objects to it
      scene = new Scene(root, 600, 300);
      ((Group) scene.getRoot()).getChildren().add(mediaView);
      scene.setFill(null);

      primaryStage.setScene(scene);       //Adding scene to the stage

      //adjust stage properties
      primaryStage.setTitle("Magic Musicplayer");
      primaryStage.setWidth(648);
      primaryStage.setHeight(457);
      primaryStage.setMaxHeight(457);
      primaryStage.setMaxWidth(648);
      primaryStage.setMinHeight(457);
      primaryStage.setMinWidth(648);

      //access the controller from the fxml loader
      directoryUIController = loader.getController();
      directoryUIController.setMusicManager(this);

      //does this fiel exist?
      if(reader.readFile("files/directories.txt")) {
         //yes -> read all the  directories from it and submit them to the UI controller
         directoryUIController.setAlreadyScannedDirectories(reader.getAllFiles());
      }else {
         //no -> submit null to the UI controller
         directoryUIController.setAlreadyScannedDirectories(null);
      }

      primaryStage.show();          //Displaying the contents of the stage
      windowStage = primaryStage;   //making the stage global
   }

   //called when the directory selection is finished - starts the actual music player
   public void activateMusicWindows(ArrayList<String> newDirectories, ArrayList<String> alreadyScannedDirectories){
      //FileWriter for the selected directories
      FileWriter writer = new FileWriter(newDirectories, true, 3, "files/directories.txt");
      //Sorter for the crawled music files
      Sorter sorter;

      Parent root = null;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
      MediaView mediaView = new MediaView();

      //crawl all new directories
      InputReader.readInput(newDirectories);

      //start the writer
      writer.start();

      //acquire all music files
      musicFiles = InputReader.getMusicFiles();
      musicQueue = new ArrayList<>();

      //start the sorting of the musicfiles according to artist, album, song title
      sorter = new Sorter(musicFiles);
      sorter.start();

      //add all indices of the musicfile containers to the music queue
      for(int i = 0; i < musicFiles.size(); i++){
         addSongToEndOfQueue(i);
      }

      //try to acquire the roto object from the fxml loader with music player ui
      try {
         root = loader.load();
      } catch (Exception e) {
         System.err.println("ERROR: MusicManager: LOAD FXM " + e);
         System.exit(-1);
      }

      //setup the new controller
      uiController = loader.getController();
      uiController.setManager(this);

      //Creating a scene object
      @NotNull Scene scene = new Scene(root, 600, 300);
      ((Group) scene.getRoot()).getChildren().add(mediaView);

      //Adding scene to the stage
      windowStage.setScene(scene);

      //Displaying the contents of the stage
      windowStage.show();

      //wait for the sorter to finish (usually it is finished before)
      try {
         sorter.join();
         activeSortedList = sorter.getSongTitleSortedList();   //set the order of songs according to the song title
      }catch(Exception e){
         System.err.println("MusicManager: Start: sorter.join: " + e);
         System.exit(-1);
      }
      //create album and artist creator, which combine songs into artist and album pages
      AlbumCreator albumCreator = new AlbumCreator(musicFiles, sorter.getAlbumSortedList());
      albumCreator.start();
      ArtistCreator artistcreator = new ArtistCreator(musicFiles, sorter.getArtistSortedList());
      artistcreator.start();

      //set the mediaplayer to the first song and play it
      setMediaPlayerMedia();
      play();
      // System.out.println("SONG DURATION?: " + currentSongmediaPlayer.getTotalDuration().toString());

      //set the cover image and song information
      setDisplayedImage(currentSongInQueue);
      setDisplayedTexts(currentSongInQueue);

      //setup of the scrolalble buttons
      uiController.buttonSetup();
   }

   private void addSongToEndOfQueue(int songIndex) {
      musicQueue.add(songIndex);        //add the MediaPlayer to the play queue
   }

   //------------------------------------------------------------------------------------
   //                                  Music playback control methods
   //------------------------------------------------------------------------------------

   //plays the next song in queue (if current song is last, then the first is played)
   private void playNextSongInQueue() {
      currentSongInQueue = (currentSongInQueue + 1) % musicQueue.size(); //the next song in queue

      //adjust the mediaplayer and display information
      setMediaPlayerMedia();
      setDisplayedImage(currentSongInQueue);
      setDisplayedTexts(currentSongInQueue);
      play();
   }

   //plays the previous song in the queue (if current song is first, then the last is played)
   private void playPreviousSongInQueue() {
      currentSongInQueue = (currentSongInQueue + musicQueue.size() - 1) % musicQueue.size(); //decrease current index by 1

      //adjust mediaplayer and diaplay information
      setMediaPlayerMedia();
      setDisplayedTexts(currentSongInQueue);
      setDisplayedImage(currentSongInQueue);
      play();
   }

   //plays a song on the parameter index
   public void playSongOnIndex(int index){
      currentSongInQueue = index; //update the index of the current song in the complete song list

      //adjust media player, song information and cover image
      setMediaPlayerMedia();
      setDisplayedTexts(index);
      setDisplayedImage(index);

      currentSongmediaPlayer.play(); //play the song
   }

   //adds a song to the next index
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

   //checks which looping type is active at the end of the queue
   private void songAtEndCheckNextPlay() {
      //not looping?
      if (currentSongInQueue == musicQueue.size() - 1 && loopStatus == loopNothing) {
         //yes -> set the current song to the first one in queue, but do not start playing
         currentSongInQueue = 0;
      } else {
         //no -> play next song which is the first on queue
         playNextSongInQueue();
      }
   }

   //------------------------------------------------------------------------------------
   //                                  Display methods
   //------------------------------------------------------------------------------------

   //sets the song information of the currently playing song
   private void setDisplayedTexts(int index){
      uiController.setSongTitle(musicFiles.get(activeSortedList.get(index)).getTitle());
      uiController.setSongAlbum(musicFiles.get(activeSortedList.get(index)).getAlbum());
      uiController.setSongArtist(musicFiles.get(activeSortedList.get(index)).getArtists()[0]);
   }

   //checks what kind of musicfile is going to start, and accordingly to type is using different methods to try to read the cover image based on the encoding of the file
   private void setDisplayedImage(int index){
      Musicfile currentSong = musicFiles.get(activeSortedList.get(index));
      //is the image sting = Image?
      if(currentSong.getImage().equals("Image")){
         //yes -> it is a mp3 file
         try {
            Mp3File mp3File = new Mp3File(currentSong.getFilePath());   //create a Mp3File from the libary
            ID3v2 songIdv2Tags = mp3File.getId3v2Tag();                 //get the mp3 tags
            byte[] imageByteArray = songIdv2Tags.getAlbumImage();       //get the cover image

            //is the image != null?
            if(imageByteArray != null) {
               //yes -> set the displayed image to the cover image
               InputStream inputStream = new ByteArrayInputStream(imageByteArray);
               uiController.setSongThumbnail(new Image(inputStream));  //set the image that is shown in the ui
            }else{
               //no -> set the displayed image to a standard image
               setDisplayedImageToStandard();
            }
         }catch(Exception e){
            System.err.println("ERROR: MusicManager: setDisplayedImage: MP3 Cover: " + e);
         }
      }else{
         //no -> it is an m4a file -> the string is teh actual path
         FileInputStream inputstream = null;
         try {
            inputstream = new FileInputStream(currentSong.getImage()); //open the needed image as FileStream
         }catch(Exception e){
            System.err.println("ERROR: MusicManager: setDisplayedImage: inputStream: " + e);
         }
         //is the image != null
         if(inputstream != null)
            //yes -> set the displayed image to the cover
            uiController.setSongThumbnail(new Image(inputstream));
         else
            //no -> set teh displayed image to the standard image
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

   //------------------------------------------------------------------------------------
   //                                  UI buttons functions
   //------------------------------------------------------------------------------------

   //play and pause button
   public void play() {
      if (currentSongmediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
         currentSongmediaPlayer.pause();
      } else {
         currentSongmediaPlayer.play();
      }
   }

   //next song button
   public void playNextButtonHit() {
      playNextSongInQueue();
   }

   //previous song button
   public void playPreviousButtonHit() {
      playPreviousSongInQueue();
   }

   //loop button
   public void loop() {
      loopStatus = (loopStatus + 1) % 3;
      System.out.println("'Loop' pressed - Current Status: " + loopStatus);
   }

   //------------------------------------------------------------------------------------
   //                                  Often reused methods
   //------------------------------------------------------------------------------------

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

   private void setMediaPlayerMedia() {
      Media songMedia;
      File file;
      if(currentSongmediaPlayer != null) {
         currentSongmediaPlayer.stop();
         currentSongmediaPlayer.dispose();
      }
      currentSongmediaPlayer = null;
      file = new File(musicFiles.get(activeSortedList.get(currentSongInQueue)).getFilePath());
      songMedia = createMedia(file);
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
}