package ControllerClasses;

import FileClasses.InputReader;
import fxml.Controller;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;

public class MusicManager extends Application{
    private MediaPlayer mediaPlayer;
    private  ArrayList<String> musicFiles;
    private ArrayList<Integer> musicQueue;
    private ArrayList<MediaPlayer> mediaPlayers;
    private int currentSongInQueue = 0;
    private Controller uiController;

    private final int loopNothing   = 0;
    private final int loopQueue     = 1;
    private final int loopSong      = 2;

    private int loopStatus = loopNothing;

    public static void main(String[] args){
        InputReader.readInput();
        Application.launch();
    }



    public void initialize(ArrayList<String> allFiles){

        musicFiles = allFiles;
        Application.launch();
    }

    public void start(Stage primaryStage) {
        musicFiles = InputReader.getAllFiles();

        musicQueue = new ArrayList<>();
        mediaPlayers = new ArrayList<>();
        musicQueue.add(0);
        musicQueue.add(2);

        File firstSong = createMusicFile("E:Benutzer/Musik/Musik\\Aero Chord\\Aero Chord - Surface.mp3");
        Media firstMedia = createMedia(firstSong);
        mediaPlayers.add(new MediaPlayer(firstMedia));
        mediaPlayers.get(0).play();

        Parent root  = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ui.fxml"));
        try {
           root = loader.load();
        }catch(Exception e){

        }
        uiController = loader.getController();
        uiController.setManager(this);
        //Creating a scene object
        Scene scene = new Scene(root, 600, 300);

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
    }

    private void stopMediaPlayer(){
        mediaPlayer.stop();
    }

    private File createMusicFile(String filePath){
        return new File(filePath);
    }

    private void setMediaPlayerMedia(Media media, int oldSongNumber){
        mediaPlayers.add(new MediaPlayer(media));
        mediaPlayers.get(oldSongNumber).stop();
        mediaPlayers.get(currentSongInQueue).play();
    }

    private Media createMedia(File file){
        final String source= file.toURI().toString();
        return new Media(source);
    }

    public void playNextSongInQueue(){
        int oldSongInQueue = currentSongInQueue;
        currentSongInQueue = (currentSongInQueue+1)%musicQueue.size();
        File newSong = createMusicFile(musicFiles.get(musicQueue.get(currentSongInQueue)));
        Media newMedia = createMedia(newSong);
        setMediaPlayerMedia(newMedia, oldSongInQueue);
    }
    public void playPreviousSongInQueue(){
        if(currentSongInQueue == 0 && musicQueue.size() > 1){
            currentSongInQueue = musicQueue.size()-1;
        }
    }
    public void loop(){
        loopStatus = (loopStatus+1)%3;
    }

}
