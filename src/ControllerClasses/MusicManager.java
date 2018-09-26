package ControllerClasses;

import FileClasses.InputReader;
import FileClasses.Musicfile;
import FileClasses.PatternMatcher;
import com.sun.istack.internal.NotNull;
import fxml.Controller;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class MusicManager extends Application {


   private ArrayList<Integer> musicQueue;
   private ArrayList<MediaPlayer> mediaPlayers;
   private int currentSongInQueue = 0;

   private MediaView mediaView;
   private final int loopNothing = 0;
   private final int loopSong = 2;

   private int loopStatus = loopNothing;

   public static void main(String[] args) {
      InputReader.readInput();
      Application.launch();
   }

   public void start(Stage primaryStage) {
      ArrayList<Musicfile> musicFiles;
      Controller uiController;

      musicFiles = InputReader.getMusicFiles();

      musicQueue = new ArrayList<>();
      mediaPlayers = new ArrayList<>();

      //       System.out.println("PATH " + musicFiles.get(0));

      addSongToEndOfQueue(musicFiles.get(0).getFilePath());
      addSongToEndOfQueue(musicFiles.get(1).getFilePath());
      //addSongToEndOfQueue(musicFiles.get(12));
      //addSongNext(musicFiles.get(18));
      // addSongToEndOfQueue("E:\\Benutzer\\Musik\\Soundeffekte\\Soundboard\\CENA.mp3");

      Parent root = null;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ui.fxml"));
      try {
         root = loader.load();
      } catch (Exception e) {
         System.err.println("ERROR: MusicManager: LOAD FXM " + e);
      }
      uiController = loader.getController();
      uiController.setManager(this);

      //Creating a scene object
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
      play();
   }

   private void addSongToEndOfQueue(String songPath) {
      File song = createMusicFile(songPath); //create actual File object
      Media media = createMedia(song);       //create Media object for the file

      addMediaPlayerToList(new MediaPlayer(media));   //create MediaPlayer for the Meda object and add it to the MediaPlayerList
      musicQueue.add(mediaPlayers.size() - 1);        //add the MediaPlayer to the play queue
   }


   private void addSongNext(String songPath) {  //play the song as next
      File song = createMusicFile(songPath);
      Media media = createMedia(song);

      addMediaPlayerToList(new MediaPlayer(media));
      if (currentSongInQueue == musicQueue.size()) {  //is the current song the last one in queue?
         //yes -> add the song to the end of the queue
         musicQueue.add(mediaPlayers.size() - 1);
      } else {
         //no -> move all songs in queue 1 position back, and add the song to the free position
         int value = mediaPlayers.size() - 1;
         for (int i = currentSongInQueue + 1; i < musicQueue.size(); i++) {
            int help = musicQueue.get(i);
            musicQueue.set(i, value);
            value = help;
         }
         musicQueue.add(value);
      }
   }

   private void setMediaPlayerMedia(int oldSongNumber) {
      mediaPlayers.get(musicQueue.get(oldSongNumber)).stop();
      mediaPlayers.get(musicQueue.get(currentSongInQueue)).play();
   }

   private void playNextSongInQueue() {
      System.out.println("'Play Next' pressed");
      int oldSongInQueue = currentSongInQueue;
      currentSongInQueue = (currentSongInQueue + 1) % musicQueue.size();
      setMediaPlayerMedia(oldSongInQueue);
   }

   private void playPreviousSongInQueue() {
      System.out.println("'Play Previous' pressed");
      int oldSongInQueue = currentSongInQueue;
      if (currentSongInQueue == 0 && musicQueue.size() > 0) {
         currentSongInQueue = musicQueue.size() - 1;
      } else {
         currentSongInQueue--;
      }
      setMediaPlayerMedia(oldSongInQueue);
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
      if (mediaPlayers.get(currentSongInQueue).getStatus().equals(MediaPlayer.Status.PLAYING)) {
         mediaPlayers.get(currentSongInQueue).pause();
      } else {
         mediaView.setMediaPlayer(mediaPlayers.get(currentSongInQueue));
         mediaPlayers.get(currentSongInQueue).play();
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

   private File createMusicFile(String filePath) {
      return new File(filePath);
   }

   private void addMediaPlayerToList(MediaPlayer mediaplayer) {   //adds the Media player to the list
      mediaplayer.setOnEndOfMedia(() -> { //method called when the song ends
         if (loopStatus == loopSong) { //are we currently looping the song?
            //yes -> reset the player and play again
            mediaplayer.seek(Duration.ZERO);
            mediaplayer.play();
         } else {
            //no -> see method for explanation
            songAtEndCheckNextPlay();
         }
      });
      mediaPlayers.add(mediaplayer);
   }

}