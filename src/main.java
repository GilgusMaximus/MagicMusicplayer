import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import javafx.application.Application;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import java.io.*;
public class main extends Application {
    MediaPlayer mediaPlayer;

    public static void main(String[] args){
        try {
            File testfile = new File("files/Searchdirectories.txt");
            BufferedReader reader = new BufferedReader(new FileReader(testfile));
            String a;
            while((a = reader.readLine()) != null){
                System.out.println(a);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println("MAIN");
        Application.launch(args);
        try {
            Thread.sleep(20000);
        }catch(Exception e){

        }
    }

    @Override
    public void start(Stage primaryStage) {
        //Scene scene = new Scene(root, 540, 210);
         File file=new File("E:\\Benutzer\\Musik\\Musik\\Aero Chord\\GTA ft. Sam Bruno - Red Lips (Aero Chord Remix).mp3");
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
}