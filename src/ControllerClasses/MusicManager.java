package ControllerClasses;

import FileClasses.FileReader;
import FileClasses.InputReader;
import FileClasses.Musicfile;
import FileClasses.PatternMatcher;
import com.sun.istack.internal.NotNull;
import fxml.Controller;
import javafx.application.Application;
import javafx.collections.MapChangeListener;

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
import FileClasses.Musicfile;

import java.io.File;
import java.util.ArrayList;

public class MusicManager extends Application{

    private  ArrayList<Musicfile> musicFiles;
    private ArrayList<Integer> musicQueue;
    private ArrayList<MediaPlayer> mediaPlayers;
    private int currentSongInQueue = 0;
    private Controller uiController;
    private MediaView mediaView;
    PatternMatcher patternMatcher = new PatternMatcher();
    private final int loopNothing   = 0;
    private final int loopQueue     = 1;
    private final int loopSong      = 2;

    private int loopStatus = loopNothing;

    public static void main(String[] args){
        InputReader.readInput();
        Application.launch();
    }




    public void start(Stage primaryStage) {
      FileReader reader = new FileReader();

        musicFiles = InputReader.getMusicFiles();

        musicQueue = new ArrayList<>();
        mediaPlayers = new ArrayList<>();

//       System.out.println("PATH " + musicFiles.get(0));

      //  addSongToEndOfQueue(musicFiles.get(0).getFilePath());
        //addSongToEndOfQueue(musicFiles.get(12));
        //addSongNext(musicFiles.get(18));
       // addSongToEndOfQueue("E:\\Benutzer\\Musik\\Soundeffekte\\Soundboard\\CENA.mp3");

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
        String[] tags;
        if(songPath.substring(songPath.length()-3).equals("m4a")){
            tags = patternMatcher.findM4AData(song);
        }else{
            tags = patternMatcher.findMp3Data(song);
        }


        Media media = createMedia(song);
        media.getMetadata().addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {
                if (ch.wasAdded()) {
                    if(ch.getKey().equals("artist"))
                        System.out.println(ch.getValueAdded());
                }
            }
        });
        addMediaPlayerToList(new MediaPlayer(media));
        musicQueue.add(mediaPlayers.size()-1);
    }



    private void addSongNext(String songPath)  {
        File song = createMusicFile(songPath);
        Media media = createMedia(song);
        media.getMetadata().addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {
                if (ch.wasAdded()) {
                    if(ch.getKey().equals("artist"))
                        System.out.println(ch.getValueAdded());
                }
            }
        });
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
        mediaPlayers.get(musicQueue.get(currentSongInQueue)).getMedia().getMetadata().addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {
                if (ch.wasAdded()) {

                }
                System.out.println(ch.getKey());
            }
        });
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