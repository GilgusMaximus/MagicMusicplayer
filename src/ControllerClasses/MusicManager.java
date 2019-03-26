package ControllerClasses;

import FileClasses.InputReader;
import FileClasses.Musicfile;
import com.sun.istack.internal.NotNull;
import fxml.Controller;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
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

   private double mouseX, mouseY;


   private final int loopNothing = 0;
   private final int loopSong = 2;
   private int loopStatus = loopNothing;
   private Controller uiController;
   private Stage pS;
   private boolean newMusicFiles = false;
   private ArrayList<Integer> activeSortedList;
   private boolean dragging;
   public static void main(String[] args) {
      InputReader.readInput();
      Application.launch();
   }
   Stage stage;
   public void start(Stage primaryStage) {
      MediaView mediaView;
      pS = primaryStage;
      musicFiles = InputReader.getMusicFiles();
      newMusicFiles = InputReader.getnewMusic();
      musicQueue = new ArrayList<>();

      for(int i = 0; i < musicFiles.size(); i++){
         addSongToEndOfQueue(i);
      }
      Sorter sorter = new Sorter(musicFiles);
      sorter.start();
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
     scene.setFill(null);
      //Adding scene to the stage
      primaryStage.setScene(scene);
      primaryStage.setWidth(640);
      primaryStage.setHeight(420);
      primaryStage.initStyle(StageStyle.TRANSPARENT);
      //primaryStage.setMaximized(true);

      //Displaying the contents of the stage
      primaryStage.show();
      stage = primaryStage;
     try {
       sorter.join();
       activeSortedList = sorter.getList();
     }catch(Exception e){
       System.err.println("MusicManager: Start: sorter.join: " + e);
     }
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
      File f = new File(musicFiles.get(activeSortedList.get(currentSongInQueue)).getFilePath());
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
      File fiel = new File(musicFiles.get(activeSortedList.get(index)).getFilePath());
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

   public void exitProgram(){

     currentSongmediaPlayer.stop();
     System.exit(1);
   }
   public void minimizeWindow(){
     pS.setIconified(true);
   }
   public int getNumberOfSongs(){
      return musicFiles.size()-1;
   }
   public Musicfile getMusicfileAtPosition(int position){
      return musicFiles.get(activeSortedList.get(position));
   }
   //TODO decide whether the usage of a java window has more advantages than creating a windows from scratch (bug below + extra effort needed to add scaling and fullscreen etc)
   //method needed to move the the window when it is dragged on the top bar
   public void moveWindows(){
      //get the current mouse position
      double xNew = MouseInfo.getPointerInfo().getLocation().getX();
      double yNew = MouseInfo.getPointerInfo().getLocation().getY();
      //currently hacky bug solution - because of no reason tzhe mouseposition between mouse clicked and onMouseDragged is gigantic, so that the window always jumps a big step
      if(mouseX-xNew < -50)
         mouseX=xNew;
      if(mouseY-yNew < -50)
         mouseY=yNew;
      //set the windows position
      stage.setX(stage.getX()+xNew-mouseX);
      stage.setY(stage.getY()+yNew-mouseY);
      if(stage.getY() > Toolkit.getDefaultToolkit().getScreenSize().getHeight())
        // stage.setY(Toolkit.getDefaultToolkit().getScreenSize().getHeight()+stage.getWidth()-100);
      if(stage.getY() < 0)
         stage.setY(0);
      mouseY = yNew;
      mouseX = xNew;
   }
   public void initializeMouseCoordinates(){
      Point p = MouseInfo.getPointerInfo().getLocation();
      mouseX = p.getX();
      mouseY = p.getY();
   }
}