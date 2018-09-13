import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;

public class main extends Application {
    MediaPlayer mediaPlayer;
    private static final String MEDIA_URL =
            "E:/Benutzer/Musik/Musik/AeroChord/aduio.mp3";
    @Override
    public void start(Stage primaryStage) {
        //Scene scene = new Scene(root, 540, 210);
         File file=new File("E:\\Benutzer\\Musik\\Musik\\Aero Chord\\GTA ft. Sam Bruno - Red Lips (Aero Chord Remix).mp3");

         final String source= file.toURI().toString();
        // create media player
        Media media = new Media(source);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

    }
}