package fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ControllerClasses.MusicManager;
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

    public void setSongTitle(String newTitle){
      songTitle.setText(newTitle);
    }

    public void setSongArtist(String newArtist){

    }

    public void setSongAlbum(String newAlbum){

    }

    public void setSongThumbnail(Image i) {
        mp3Thumb.setImage(i);
    }
}