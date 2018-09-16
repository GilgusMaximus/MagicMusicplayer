package ControllerClasses;

import FileClasses.InputReader;
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

public class MusicManager extends Application{

    private MediaPlayer reset;

    private  ArrayList<String> musicFiles;
    private ArrayList<Integer> musicQueue;
    private ArrayList<MediaPlayer> mediaPlayers;
    private int currentSongInQueue = 0;
    private Controller uiController;
    private MediaView mediaView;
    private final int loopNothing   = 0;
    private final int loopQueue     = 1;
    private final int loopSong      = 2;

    private int loopStatus = loopSong;

    public static void main(String[] args){
        InputReader.readInput();
        Application.launch();
    }




    public void start(Stage primaryStage) {
        musicFiles = InputReader.getAllFiles();

        musicQueue = new ArrayList<>();
        mediaPlayers = new ArrayList<>();


        File resetFile = new File("files/silence.wav");
        Media resetMedia = createMedia(resetFile);
        reset = new MediaPlayer(resetMedia);

        addSongToEndOfQueue(musicFiles.get(4));
        addSongToEndOfQueue(musicFiles.get(8));
        addSongNext(musicFiles.get(10));
        addSongToEndOfQueue("E:\\Benutzer\\Musik\\Soundeffekte\\Soundboard\\CENA.mp3");


        Parent root  = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ui.fxml"));
        try {
           root = loader.load();
        }catch(Exception e){

        }
        uiController = loader.getController();
        uiController.setManager(this);

        //Creating a scene object
        @NotNull
        Scene scene = new Scene(root, 600, 300);
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

    private void addSongToEndOfQueue(String songPath){
        File song = createMusicFile(songPath);
        Media media = createMedia(song);
        addMediaPlayerToList(new MediaPlayer(media));
        musicQueue.add(mediaPlayers.size()-1);
    }

    private void addSongNext(String songPath){
        File song = createMusicFile(songPath);
        Media media = createMedia(song);
        addMediaPlayerToList(new MediaPlayer(media));
        if(currentSongInQueue == musicQueue.size()){
            musicQueue.add(mediaPlayers.size()-1);
        }else {
            int value =mediaPlayers.size()-1;
            for (int i = currentSongInQueue + 1; i < musicQueue.size(); i++) {
                int help = musicQueue.get(i);
                musicQueue.set(i, value);
                value = help;
            }
            musicQueue.add(value);
        }
    }

    private void setMediaPlayerMedia(int oldSongNumber){
        mediaPlayers.get(musicQueue.get(oldSongNumber)).stop();
        mediaPlayers.get(musicQueue.get(currentSongInQueue)).play();
    }

    public void playNextSongInQueue(){
        int oldSongInQueue = currentSongInQueue;
        currentSongInQueue = (currentSongInQueue+1)%musicQueue.size();
        setMediaPlayerMedia(oldSongInQueue);
    }
    public void playPreviousSongInQueue(){
        int oldSongInQueue = currentSongInQueue;
        if(currentSongInQueue == 0 && musicQueue.size() > 1){
            currentSongInQueue = musicQueue.size()-1;
        }else{
            currentSongInQueue--;
        }
        setMediaPlayerMedia(oldSongInQueue);
    }

    public void songAtEndCheckNextPlay(){
         if(currentSongInQueue == musicQueue.size()-1 && loopStatus == loopNothing){
                currentSongInQueue = 0;
        }else{
            playNextSongInQueue();
        }
    }

    //methods called from UI

    public void play(){
        if(mediaPlayers.get(currentSongInQueue).getStatus().equals(MediaPlayer.Status.PLAYING)){
            mediaPlayers.get(currentSongInQueue).pause();
        }else{
            mediaView.setMediaPlayer(mediaPlayers.get(currentSongInQueue));
            mediaPlayers.get(currentSongInQueue).play();
        }
    }


    public void playNextButtonHit(){
        playNextSongInQueue();
    }
    public void playPreviousButtonHit(){
        playPreviousSongInQueue();
    }
    public void loop(){
        loopStatus = (loopStatus+1)%3;
    }

    //often used create methods
    private Media createMedia(File file){
        final String source= file.toURI().toString();
        return new Media(source);
    }
    private File createMusicFile(String filePath){
        return new File(filePath);
    }
    private void addMediaPlayerToList(MediaPlayer mediaplayer){
        mediaplayer.setOnEndOfMedia(() -> {
            if(loopStatus == loopSong){
                mediaplayer.seek(Duration.ZERO);
                mediaplayer.play();
            }else {
                songAtEndCheckNextPlay();
            }
        });
        mediaPlayers.add(mediaplayer);
    }

}