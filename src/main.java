import javafx.application.Application;

import javafx.event.EventHandler;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;

import java.io.*;

import java.util.ArrayList;
import java.util.Scanner;

public class main extends Application {
    private MediaPlayer mediaPlayer;
    private static ArrayList<String> allFiles;
    public static void main(String[] args){
        boolean search = true;
        FileReader reader = new FileReader();
        System.out.println("Willkommen beim MagicMusicPlayer");

        if(reader.readFile("files/Searchdirectories.txt")) {
            allFiles = reader.getAllFiles();
        }else{
            allFiles = null;
        }


        if(allFiles == null){
            System.out.println("Bitte geben sie einen Ordner an in welchem selbst + allen Unterordnern nach Musik gesucht werden soll");
        }else{
            System.out.println("Möchten sie zu den bereits durchsuchten Directories weitere hinzufügen? y/n");
            Scanner s = new Scanner(System.in);
            if(!s.nextLine().toLowerCase().equals("y")){
                search = false;
            }
        }
        ArrayList<String> directories;
        if(search) { //did teh user add more directories / had no existing ones?
            //yes -> search for files
            directories = readInputDirectories();                   //user inputs directories to search for music files
            System.out.println("Now searching for music files. This may take a while...");
            if(allFiles == null) {  //Do we already have files saved?
                //no -> allFiles = the directories that have been searched
                allFiles                    = FileSearcher.findAllFiles(directories);   //search through all directories and subdirectories
            }else{
                //yes -> we append the new files to the old ones
                ArrayList<String> newFiles = FileSearcher.findAllFiles(directories);
                allFiles.addAll(newFiles);
            }
            System.out.println("File search has been completed. " + (allFiles.size()/2) + " mp3 files have been found.");
            FileWriter.writeToFile("files/Searchdirectories.txt", allFiles, false);    //write all files into the Seachdirectories.txt with the following system: absolute file path \n file name \n
        }
            //no -> just start the app
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        //Scene scene = new Scene(root, 540, 210);
         File file=new File(allFiles.get((int)(Math.random() * 500) * 2));
        System.out.println("START");
         final String source= file.toURI().toString();
        // create media player
        Media media = new Media(source);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        Circle circle = new Circle();
        //Setting the text
        Text text = new Text("Click on the circle to change its color");

        //Setting the position of the circle
        circle.setCenterX(300.0f);
        circle.setCenterY(135.0f);

        //Setting the radius of the circle
        circle.setRadius(25.0f);

              EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
                 @Override
                 public void handle(MouseEvent e) {
                    mediaPlayer.stop();
                 }
             };
        //Registering the event filter
        circle.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        circle.setFill(Color.BROWN);
        //Creating a Group object
        Group root = new Group(circle, text);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 300);

        //Setting the fill color to the scene
        scene.setFill(Color.LAVENDER);

        //Setting title to the Stage
        primaryStage.setTitle("Event Filters Example");

        //Adding scene to the stage
        primaryStage.setScene(scene);

        //Displaying the contents of the stage
        primaryStage.show();
    }

    static ArrayList<String> readInputDirectories(){
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> directories = new ArrayList<>();
        String directory = null;
        while(directory == null){
            directory = scanner.nextLine();
            File checkExistence = new File(directory);
            if(!checkExistence.isDirectory()) {
                directory = null;
                System.out.println("This is not a valid directory. please try again");
            }else{
                directories.add(directory);
                System.out.println("Möchten sie einen weiteren Ordner hinzufügen? y/n");
                String answer = scanner.nextLine().toLowerCase();
                if(answer.equals("y")) {
                    directory = null;
                    System.out.println("Bitte geben sie einen weiteren Ordnern an");
                }
            }
        }
        return directories;
    }

}

