package fxml;

import ControllerClasses.MusicManager;
import java.io.File;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class DirectoryUIController {

   @FXML
   TextField directoryField; //the field, which holds the currently browsed or typed directory
   @FXML
   AnchorPane anchorpane;
   @FXML
   private VBox addedDirectoriesList;  //the vbox, which holds all directories, that are added to the crawl list

   private ArrayList<String> alreadyScannedDirectories, newDirectories; //directories from the directories.txt file and the newly added directories

   private MusicManager manager;

   public void setMusicManager(MusicManager Manager){
      manager = Manager;
      newDirectories = new ArrayList<>();
   }
   //if the app was used previously, it loads the previously scanned directories to display them
   public void setAlreadyScannedDirectories(ArrayList<String> scannedDirectories){
      if(scannedDirectories == null)
         alreadyScannedDirectories = new ArrayList<>();
      else
         alreadyScannedDirectories = scannedDirectories;
      setUpAlreadyScannedDirectories();
   }

   //for each directory, add it to the vbox
   private void setUpAlreadyScannedDirectories(){
      for (String directory: alreadyScannedDirectories) {
         addDirectoryToList(directory);
      }
   }
   //wrapper function, so that null can be set as parameter
   public void addNewDirectoryToList(){
      addDirectoryToList(null);
   }

   //add new directory to the crawl list
   private void addDirectoryToList(String directory){
      TextField field = new TextField();
      //is directorry= null?
      if(directory == null) {
         //yes -> new directory is added
         newDirectories.add(directoryField.getText());    //add to arraylist of new directories
         field.setText(directoryField.getText());         //set the textfield display text
      }else {
         //no -> a previously scanned directory is added
         field.setText(directory);
      }
      field.setOnMouseClicked(event -> {delete(field);});   //set the function call, when the user clicks on the textfield object
      field.setEditable(false);                             //deactivate the possibility to edit it

      addedDirectoriesList.getChildren().add(field); //add the textfield to the vbox
   }

   //removes the textfield and arraylist entry
   private void delete(TextField field){
      //does the newDirectories list contan this element?
      if(newDirectories.contains(field.getText()))
         //yes -> remove it from there
         newDirectories.remove(field.getText());
      else
         //no -> remove it from the alreadyScannedDirectories //TODO has no effect on the show songs yet, as it does not trigger a recrawl or similar
         alreadyScannedDirectories.remove(field.getText());
      addedDirectoriesList.getChildren().remove(field);  //remove the textfield from the vbox
   }

   //opens a search dialog when the browse button was clicked
   public void browseButtonClicked(){
      //create DirectoryChooser dialog
      DirectoryChooser chooser = new DirectoryChooser();
      Stage stage = (Stage) anchorpane.getScene().getWindow();
      File file = chooser.showDialog(stage);
      //is the directory valid?
      if(file != null){
         //yes -> add teh path to the textfield of the currently selected directory
         directoryField.setText(file.getPath());
      }
   }

   //triggered when the user clicks finish
   public void finishEntry(){
      //are there any directories?
      if(alreadyScannedDirectories.size() + newDirectories.size() == 0)
         //no-> do not allow to finish
         return;
      //yes -> finish and start the actual music player
     manager.activateMusicWindows(newDirectories, alreadyScannedDirectories);
   }
}
