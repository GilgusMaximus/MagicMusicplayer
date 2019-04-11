package ControllerClasses;

import static java.lang.Thread.sleep;

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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
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
   private Image standardImage;
   private Service<Void> background;

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


      Image logo = new Image(getClass().getResourceAsStream("/resources/MM.png")); //load image from resources folder
      primaryStage.getIcons().add(logo);                                              //set the application icon to the image

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
      FileWriter writer = new FileWriter(newDirectories, true, 3, "files/directories.txt");  //FileWriter for the selected directories
      Sorter sorter;                                                                                              //Sorter for the crawled music files

      Parent root = null;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
      MediaView mediaView = new MediaView();

      InputReader.readInput(newDirectories); //crawl all new directories
      writer.start();                        //start the writer

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
      standardImage = uiController.getStandardImage();
      //Creating a scene object
      @NotNull Scene scene = new Scene(root, 600, 300);
      ((Group) scene.getRoot()).getChildren().add(mediaView);

      windowStage.setScene(scene);  //Adding scene to the stage
      windowStage.show();           //Displaying the contents of the stage

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

      //setup of the scrollable buttons & scrollbar
      uiController.buttonSetup();
      if(musicFiles.size() > 6)
         uiController.setScrollSliderMaximum(musicFiles.size()-6);   //prevents bottom out of bounds
      else
         uiController.setScrollSliderMaximum(0);   //no scroll should be available if less or equal than 6 songs were found
   }

   //------------------------------------------------------------------------------------
   //                                  Music playback control methods
   //------------------------------------------------------------------------------------

   private void addSongToEndOfQueue(int songIndex) {
      musicQueue.add(songIndex);        //add the MediaPlayer to the play queue
   }

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
      uiController.setSongThumbnail(standardImage);  //set the image that is shown in the ui
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
      //is the current media player != null?
      if(currentSongmediaPlayer != null) {
         //yes -> stop the media player and dispose the resources
         currentSongmediaPlayer.stop();
         currentSongmediaPlayer.dispose();
      }
      currentSongmediaPlayer = null;
      file = new File(musicFiles.get(activeSortedList.get(currentSongInQueue)).getFilePath());  //get the new music file
      songMedia = createMedia(file);                                                            //create a media object with the file
      currentSongmediaPlayer = new MediaPlayer(songMedia);                                      //create a new MediaPlayer object with the Media and set it to the current media player

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

      currentSongmediaPlayer.setOnReady(new Runnable() { //method called, when the media player is ready
         @Override
         public void run() {
            uiController.updateTimeLine((int) currentSongmediaPlayer.getMedia().getDuration().toSeconds());
            System.out.println("Song length " + currentSongmediaPlayer.getMedia().getDuration().toSeconds());
         }
      });


      currentSongmediaPlayer.setOnStopped(new Runnable() { //Stop status is set, when setMediaPlayer is called
         @Override
         public void run() {
            background.cancel(); //stop the background thread
         }
      });


      currentSongmediaPlayer.setOnPlaying(new Runnable() { //Playing status is set, when the media player is playing a song
         @Override
         public void run() {
            //service is a background thread from javafx
            background = new Service<Void>() {
               @Override
               //create a new task, which should be executed on that thread
               protected Task<Void> createTask() {
                  return new Task<Void>() {
                     //get current song time - needed when the song is paused and played again
                     int time = (int)currentSongmediaPlayer.getCurrentTime().toSeconds();
                     @Override
                     protected Void call() throws Exception {

                        //is the media player playing?
                        while (currentSongmediaPlayer.getStatus() == Status.PLAYING) {
                           //yes-> does the slider have the same value as the time variable?
                           /*if(uiController.getSliderValue() != time) {
                              //no -> set the time value to the slider value (this means, the user has moved the slider to a different position)
                              time = uiController.getSliderValue();
                              currentSongmediaPlayer.seek(Duration.seconds(time));
                              //currentSongmediaPlayer.seek(Duration.seconds(time)); //adjust the playback time of the media player
                           }else {
                              //no -> update the time and sldier value to the current playback time
                           time = (int)currentSongmediaPlayer.getCurrentTime().toSeconds();
                           uiController.setSliderPosition(time);
                           }*/
                           //System.out.println(currentSongmediaPlayer.get().toSeconds() + " time ");
                           if(uiController.getDragged()) {
                              time = uiController.getSliderValue();
                              currentSongmediaPlayer.seek(Duration.seconds(time));
                              System.out.println("SLDIERVALUE: " + uiController.getSliderValue() + " SEEEK: " + currentSongmediaPlayer.getCurrentTime().toMillis());
                              uiController.setTimeLineDraggedFalse();
                           }else {
                              time = (int)currentSongmediaPlayer.getCurrentTime().toSeconds();
                              if(time > 10 && a) {
                                 a = false;
                                 currentSongmediaPlayer.seek(Duration.seconds(30));
                                 time = (int) currentSongmediaPlayer.getCurrentTime().toSeconds();
                              }
                              uiController.setSliderPosition(time);
                           }
                           sleep(100);   //sleep for one second
                        }
                        return null;
                     }
                  };
               }
            };
            background.restart(); //start the thread
         }
      });

      System.gc();
   }
   boolean a = true;
}