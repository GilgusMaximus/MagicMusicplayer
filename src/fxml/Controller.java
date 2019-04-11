package fxml;

import FileClasses.Musicfile;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ControllerClasses.MusicManager;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class Controller {

  private MusicManager manager;
  public void setManager(MusicManager m) {
    manager = m;
  }

  public Image getStandardImage(){
    return mp3Thumb.getImage();
  }

  @FXML
  private Slider musicTimeLine;
  //------------------------------------------------------------------------------------
  //                                  Bottombar
  //------------------------------------------------------------------------------------

  @FXML
  protected void playPause() {
    manager.play();
  }

  @FXML
  protected void nextSong() {
    manager.playNextButtonHit();
  }

  @FXML
  protected void previousSong() {
    manager.playPreviousButtonHit();
  }

  @FXML
  protected void loop() {
    manager.loop();
  }

  //setup the music time line when a new song is played
  public void updateTimeLine(double songLength){
    musicTimeLine.setMax(songLength);
    musicTimeLine.setValue(0);
  }

  public void setSliderPosition(int value){
    musicTimeLine.setValue(value);
  }
public double getSliderMaxValue(){
    return musicTimeLine.getMax();
}

  public int getSliderValue(){
    return (int) musicTimeLine.getValue();
  }
  //------------------------------------------------------------------------------------
  //                                  Sidebar buttons
  //------------------------------------------------------------------------------------

  @FXML
  protected void songsClicked(){
    System.out.println("songs clicked");
  }
  @FXML
  protected void artistsClicked(){
    System.out.println("artists clicked");
  }
  @FXML
  protected void albumsClicked(){
    System.out.println("albums clicked");
  }

  //------------------------------------------------------------------------------------
  //                            UI Buttons
  //------------------------------------------------------------------------------------

  private int indexHighestButton = 0; //the button with the highest y coordinate
  private int indexHighestSong = 0;   //the song (as index) that is on the highest button

   @FXML
   private Slider scrollSlider;
  @FXML
  private AnchorPane button1, button2, button3, button4, button5, button6, button7;                                                         //the panels that represent a song
  @FXML
  private ImageView playSongButton1, playSongButton2, playSongButton3, playSongButton4, playSongButton5, playSongButton6, playSongButton7;  //the play button on every panel
  @FXML
  private Text button1SongText, button2SongText, button3SongText, button4SongText, button5SongText, button6SongText, button7SongText;       //the Song Title on every pane
  @FXML
  private Text button1ArtistText, button2ArtistText, button3ArtistText, button4ArtistText, button5ArtistText, button6ArtistText, button7ArtistText;       //the Song Title on every pane
  @FXML
  private Text button1AlbumText, button2AlbumText, button3AlbumText, button4AlbumText, button5AlbumText, button6AlbumText, button7AlbumText;
  private AnchorPane[] buttons;
  private Text[] buttonSongTexts, buttonArtistTexts, buttonAlbumTexts;
  private ImageView[] playSongButtons ;

  //gets all UI element references and stores them in global arrays
  public void buttonSetup(){  //is needed, because the ui items get initalozed later on, so it is not possible to initialize them on declaration
    ImageView[] playImage     = {playSongButton1, playSongButton2, playSongButton3, playSongButton4, playSongButton5, playSongButton6, playSongButton7};
    AnchorPane[] buttonPanes  = {button1, button2, button3, button4, button5, button6, button7};
    Text[] songTitle          = {button1SongText, button2SongText, button3SongText, button4SongText, button5SongText, button6SongText, button7SongText};
    Text[] songArtist         = {button1ArtistText, button2ArtistText, button3ArtistText, button4ArtistText, button5ArtistText, button6ArtistText, button7ArtistText};
    Text[] songAblum          = {button1AlbumText, button2AlbumText, button3AlbumText, button4AlbumText, button5AlbumText, button6AlbumText, button7AlbumText};
    playSongButtons = playImage;
    buttons = buttonPanes;
    buttonSongTexts = songTitle;
    buttonArtistTexts = songArtist;
    buttonAlbumTexts = songAblum;

    //fills the first 6 buttons with the first 6 informations
    initialButtonTextFill();
  }
  private void initialButtonTextFill(){
    for (int i = 0; i < buttonSongTexts.length; i++) {
      Musicfile file = manager.getMusicfileAtPosition(i);
      if (file != null) {
        assignTextsToButons(file, i);
      }
    }
  }
  //------------------------------------------------------------------------------------
  //                            Scrolling buttons in songs
  //------------------------------------------------------------------------------------

   public void setScrollSliderMaximum(int maximum){
      scrollSlider.setMax(maximum);
   }



   @FXML
   void mouseDrag(){  //method is called when the slider for song scrolling is dragged
     System.out.println("IndexHighestSOng:" + indexHighestSong);
     int scrollSliderValue = (int) scrollSlider.getValue(); //get the sldier value
     indexHighestSong = scrollSliderValue;                  //update the highest song index
     System.out.println("ScrollSldierValue:" + scrollSliderValue);

     //reset the buttons, so that button 0 is at position y=0 and every other buttons in index order blow by 50px -> prevents top and bottom out of bounds
     for (int i = 0; i < buttons.length; i++) {
       indexHighestButton = 0;
       buttons[(indexHighestButton+i)%buttons.length].setLayoutY(i*50);
       assignTextsToButons(manager.getMusicfileAtPosition(i+indexHighestSong), i);  //assign the correct texts to every button
     }
   }

  @FXML
  void scrollSongs(ScrollEvent event) { //handles the scrolling of the panes, and correct updating of their information
    float speed = 6.20f;
    if(event.getDeltaY() < 0)
      speed *= -1;
    //is the song with index 0 already on top at the position y = 0 (start position)? or the song with the lowest index-5 on the higehst position close to the position y = 0 (different scroll speeds do not allow hitting y = 0)?
    if(!(buttons[indexHighestButton].getLayoutY() >= -15 && buttons[indexHighestButton].getLayoutY() <= 15 && indexHighestSong == manager.getNumberOfSongs() - 5 && event.getDeltaY() < 0)) {
      if (!(buttons[0].getLayoutY() == 0 && indexHighestSong == 0 && event.getDeltaY() > 0)) {
        //no -> we can sroll
        for (AnchorPane pane : buttons) { //first update all positions accordingly
          pane.setLayoutY(pane.getLayoutY() + speed /*event.getDeltaY() * 0.40*/);
        }
        for (int i = 0; i < buttons.length; i++) {  //check which button is not seen anymore, and wrap it accordingly ont top of the next or below the previous button
          if (buttons[i].getLayoutY() < -50) {
            buttons[i].setLayoutY(buttons[(i + buttons.length - 1) % buttons.length].getLayoutY() + 50);
            indexHighestSong++;
            scrollSlider.setValue(scrollSlider.getValue()+1);
            updateSrcollTexts(false, i);
          } else if (buttons[i].getLayoutY() > 300) {
            buttons[i].setLayoutY(buttons[(i + 1) % buttons.length].getLayoutY() - 50);
            indexHighestSong--;
             scrollSlider.setValue(scrollSlider.getValue()-1);
            updateSrcollTexts(true, i);
          }
          if (buttons[i].getLayoutY() < buttons[indexHighestButton].getLayoutY()) { //find the button with the higehst y coordinate
            indexHighestButton = i;
          }
        }
      }
    }
    //yes -> do nothing, because we have reached the maximum we can sroll, without going out of boundaries
  }

  //called if one of the play button on the scollpane is pressed
  @FXML
  protected void playSongButton(MouseEvent event) {
    int index = -1;
    //go through all buttons, and register which button catched the event
    for (int i = 0; i < playSongButtons.length; i++) {
      int pos = (indexHighestButton + i) % playSongButtons.length;
      if (event.getSource().equals(playSongButtons[pos])) {
        index = i;
        break;
      }
    }
    //play the song according to the index
    manager.playSongOnIndex(indexHighestSong + index);
  }

  //sets the song of the the pane that has been wrapped to the top or bottom
  private void updateSrcollTexts(boolean up, int i) {
    Musicfile file;
    //did a song pane get wrapped to the top? (so the user scroleld up)
    if (up) {
      //yes -> just use the already updated index of the highest song
      file = manager.getMusicfileAtPosition(indexHighestSong);
    } else {
      //no -> we need to get the song that has an index to the current highest song + 6
      file = manager.getMusicfileAtPosition(indexHighestSong + 6);
    }
    assignTextsToButons(file, i);
  }

  //assigns the song information to the corresponding UI text elements
  private void assignTextsToButons(Musicfile file, int i){
    String[] artists = file.getArtists();
    String a = "";
    if(artists.length > 1)
      for(int j = 0; j < artists.length-1; j++)
        a += artists[j] + ", ";

    a += artists[artists.length-1];
    buttonArtistTexts[i].setText(a);
    buttonSongTexts[i].setText(file.getTitle());
    buttonAlbumTexts[i].setText(file.getAlbum());
  }

  //------------------------------------------------------------------------------------
  //                            currently playing song information
  //------------------------------------------------------------------------------------

  @FXML
  private ImageView mp3Thumb;

  @FXML
  private Text songTitle;

  @FXML
  private Text artistTitle;

  @FXML
  private Text albumTitle;

  public void setSongTitle(String newTitle) {
    songTitle.setText(newTitle);
  }

  public void setSongArtist(String newArtist) {
    artistTitle.setText(newArtist);
  }

  public void setSongAlbum(String newAlbum) {
    albumTitle.setText(newAlbum);
  }

  public void setSongThumbnail(Image i) { mp3Thumb.setImage(i);
  }
}