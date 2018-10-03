package fxml;

import FileClasses.Musicfile;
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
    boolean first = true;
    @FXML
    private AnchorPane button1, button2, button3, button4, button5, button6, button7;
    AnchorPane[]buttons;
    @FXML
    private ImageView playSongButton1, playSongButton2, playSongButton3, playSongButton4, playSongButton5, playSongButton6, playSongButton7;
   @FXML
   private Text button1SongText, button2SongText, button3SongText, button4SongText, button5SongText, button6SongText, button7SongText;
   Text[] scrollableButtonsTexts;
   ImageView[] playSongButtons;
    public void buttonSetup(){
        AnchorPane[] a = {button1, button2, button3, button4, button5, button6, button7};
        buttons = a;
        Text[] b = {button1SongText, button2SongText, button3SongText, button4SongText, button5SongText, button6SongText, button7SongText};
        scrollableButtonsTexts = b;
         ImageView[] c = {playSongButton1, playSongButton2, playSongButton3, playSongButton4, playSongButton5, playSongButton6, playSongButton7};
       playSongButtons = c;
    }
    private int indexHighestButton = 0;
    private int indexHighestSong = 0;
    @FXML
    protected void scrollSongs(ScrollEvent event){
       if(first) {
          for (int i = 0; i < scrollableButtonsTexts.length; i++) {
             Musicfile file = manager.getMusicfileAtPosition(i);
             if (file != null) {
                String title = file.getTitle();
                Text a = scrollableButtonsTexts[i];
                a.setText(title);
             }
          }
          first = false;
       }
       if(!(buttons[0].getLayoutY() == 0 && indexHighestSong == 0 && event.getDeltaY() > 0) && !(buttons[indexHighestButton].getLayoutY() >= -15 && buttons[indexHighestButton].getLayoutY() <= 15 && indexHighestSong == manager.getNumberOfSongs()-5 && event.getDeltaY() < 0)) {
          for (AnchorPane pane : buttons) { //first update all positions accordingly
             pane.setLayoutY(pane.getLayoutY() + event.getDeltaY() * 0.40);
          }
          for (int i = 0; i < buttons.length; i++) {  //check which button is not seen anymore, and move it accordingly ont top f the next or below the previous button
             if (buttons[i].getLayoutY() < -50) {
                buttons[i].setLayoutY(buttons[(i + buttons.length - 1) % buttons.length].getLayoutY() + 50);
                indexHighestSong++;
                updateSrcollTexts(false, i);
             } else if (buttons[i].getLayoutY() > 300) {
                buttons[i].setLayoutY(buttons[(i + 1) % buttons.length].getLayoutY() - 50);
                indexHighestSong--;
                updateSrcollTexts(true, i);
             }
             if (buttons[i].getLayoutY() < buttons[indexHighestButton].getLayoutY())
                indexHighestButton = i;
          }
          System.out.println("highes " + indexHighestSong);
       }
    }



    @FXML
    protected void previousSong(MouseEvent event) {
        manager.playPreviousButtonHit();
    }

    @FXML
    protected void playSongButton(MouseEvent event){
       int index = -1;
      for(int i = 0; i < playSongButtons.length; i++){
         int pos =(indexHighestButton+i)%playSongButtons.length;
         if(event.getSource().equals(playSongButtons[pos])){
            index = i;
            break;
         }
      }

       manager.playSongOnIndex(indexHighestSong+index);
    }
    @FXML
    protected void loop(MouseEvent event) {
        manager.loop();
    }



    private void updateSrcollTexts(boolean up, int i){
       System.out.println(indexHighestSong);
          if (up) {
             Musicfile file = manager.getMusicfileAtPosition(indexHighestSong);
             scrollableButtonsTexts[i].setText(file.getTitle());
          } else {
             Musicfile file = manager.getMusicfileAtPosition(indexHighestSong+6);
             scrollableButtonsTexts[i].setText(file.getTitle());
          }
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