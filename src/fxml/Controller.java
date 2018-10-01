package fxml;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ControllerClasses.MusicManager;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class Controller {

    private MusicManager manager;

    public void setManager(MusicManager m){
        manager = m;
    }

    @FXML
    private TextField vornameTF;

    @FXML
    protected void playPause(MouseEvent event) {
        manager.play();
    }

    @FXML
    protected void nextSong(MouseEvent event) {
        manager.playNextButtonHit();
    }

    @FXML
    private AnchorPane button1, button2, button3, button4, button5, button6;
    AnchorPane[]buttons;


    public void buttonSetup(){
        AnchorPane[] a = {button1, button2, button3, button4, button5, button6};
        buttons = a;
    }

    @FXML
    protected void scrollSongs(ScrollEvent event){
        for(AnchorPane a : buttons) {
            if(a.getLayoutY() > 325){
                a.setLayoutY(-75+a.getLayoutY()-300);
            }else {
                if ((a.getLayoutY() < -75))
                    a.setLayoutY(300-a.getLayoutY()+50);
                else
                    a.setLayoutY(a.getLayoutY() + event.getDeltaY() * 0.30);
            }
        }
    }

    @FXML
    protected void previousSong(MouseEvent event) {
        manager.playPreviousButtonHit();
    }

    @FXML
    protected void loop(MouseEvent event) {
        manager.loop();
    }

    @FXML
    private ImageView mp3Thumb;

    @FXML
    private Text songTitle;

    @FXML
    private Text artistTitle;

    @FXML
    private Text albumTitle;

    public void setSongTitle(String newTitle){
      songTitle.setText(newTitle);
    }

    public void setSongArtist(String newArtist){
        artistTitle.setText(newArtist);
    }

    public void setSongAlbum(String newAlbum){
        albumTitle.setText(newAlbum);
    }

    public void setSongThumbnail(Image i) {
        mp3Thumb.setImage(i);
    }
}