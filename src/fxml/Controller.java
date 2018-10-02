package fxml;

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
    protected void playPause(MouseEvent event) {
        manager.play();
    }

    @FXML
    protected void nextSong(MouseEvent event) {
        manager.playNextButtonHit();
    }

    @FXML
    private AnchorPane button1, button2, button3, button4, button5, button6, button7;
    AnchorPane[]buttons;
    @FXML
    private ImageView playSongButton1, playSongButton2, playSongButton3, playSongButton4, playSongButton5, playSongButton6, playSongButton7;
    public void buttonSetup(){
        AnchorPane[] a = {button1, button2, button3, button4, button5, button6, button7};
        buttons = a;
    }

    @FXML
    protected void scrollSongs(ScrollEvent event){
      for(AnchorPane pane : buttons){ //first update all positions accordingly
        pane.setLayoutY(pane.getLayoutY() + event.getDeltaY() * 0.30);
      }
      for(int i = 0; i < buttons.length; i++){  //check which button is not seen anymore, and move it accordingly ont top f the next or below the previous button
        if(buttons[i].getLayoutY() < -50){
          buttons[i].setLayoutY(buttons[(i+buttons.length-1)%buttons.length].getLayoutY()+50);
        }else if(buttons[i].getLayoutY() > 300){
          buttons[i].setLayoutY(buttons[(i+1)%buttons.length].getLayoutY()-50);
        }
      }
    }

    @FXML
    protected void previousSong(MouseEvent event) {
        manager.playPreviousButtonHit();
    }

    @FXML
    protected void playSongButton1(MouseEvent event){
      System.out.println("Button1 pressed");
    }
  @FXML
  protected void playSongButton2(MouseEvent event){
    System.out.println("Button2 pressed");
  }

  @FXML
  protected void playSongButton3(MouseEvent event){
    System.out.println("Button3 pressed");
  }
  @FXML
  protected void playSongButton4(MouseEvent event){
    System.out.println("Button4 pressed");
  }

  @FXML
  protected void playSongButton5(MouseEvent event){
    System.out.println("Button5 pressed");
  }
  @FXML
  protected void playSongButton6(MouseEvent event){
    System.out.println("Button6 pressed");
  }
  @FXML
  protected void playSongButton7(MouseEvent event){
    System.out.println("Button7 pressed");
  }
    @FXML
    protected void loop(MouseEvent event) {
        manager.loop();
    }

    @FXML
    private Text button1SongText, button2SongText, button3SongText, button4SongText, button5SongText, button6SongText, button7SongText;
    Text[] scrollableButtonsTexts;

    private void updateSrcollTexts(){

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