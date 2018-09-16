package fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ControllerClasses.MusicManager;

public class Controller {

    private MusicManager manager;

    public void setManager(MusicManager m){
        manager = m;
    }

    @FXML
    private TextField vornameTF;

    @FXML
    protected void nextSong(MouseEvent event) {
        manager.playNextSongInQueue();
    }

    @FXML
    protected void previousSong(MouseEvent event) {
        manager.playPreviousSongInQueue();
    }

    @FXML
    protected void loop(MouseEvent event) {
        manager.loop();
    }

}