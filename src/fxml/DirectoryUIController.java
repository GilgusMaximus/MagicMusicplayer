package fxml;

import ControllerClasses.MusicManager;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class DirectoryUIController {

   @FXML
   TextField directoryField;
   @FXML
   AnchorPane anchorpane;

   @FXML
   private VBox addedDirectoriesList;

   private ArrayList<String> alreadyScannedDirectories;
   private  ArrayList<String> newDirectories;
   private MusicManager manager;

   public void setMusicManager(MusicManager Manager){
      manager = Manager;
      newDirectories = new ArrayList<>();
   }

   int counter = 0;

   public void setAlreadyScannedDirectories(ArrayList<String> scannedDirectories){
      if(scannedDirectories == null)
         alreadyScannedDirectories = new ArrayList<>();
      else
         alreadyScannedDirectories = scannedDirectories;
      setUpAlreadyScannedDirectories();
   }

   public void setUpAlreadyScannedDirectories(){
      for (String directory: alreadyScannedDirectories) {
         addAlreadyScannesDirectoryToList(directory);
      }
   }

   private void addAlreadyScannesDirectoryToList(String directory){
      TextField field = new TextField();
      field.setText(directory);
      field.setId(""+counter++);
      field.setOnMouseClicked(event -> {delete(field);});
      field.setEditable(false);

      addedDirectoriesList.getChildren().add(field);
   }
   public void addDirectoryToList(){
      TextField field = new TextField();
      newDirectories.add(directoryField.getText());
      field.setText(directoryField.getText());
      field.setId(""+counter++);
      field.setOnMouseClicked(event -> {
         delete(field);});
      field.setEditable(false);

      addedDirectoriesList.getChildren().add(field);
   }
   public void delete(TextField field){
      if(newDirectories.contains(field.getText()))
         newDirectories.remove(field.getText());
      else
         alreadyScannedDirectories.remove(field.getText());
      addedDirectoriesList.getChildren().remove(field);
   }



   public void browseButtonclicked(){
      DirectoryChooser chooser = new DirectoryChooser();
      Stage stage = (Stage) anchorpane.getScene().getWindow();
      File file = chooser.showDialog(stage);
      if(file != null){
         directoryField.setText(file.getPath());
      }
   }


   public void finishEntry(){
      if(alreadyScannedDirectories.size() + newDirectories.size() == 0)
         return;
     manager.activateMusicWindows(newDirectories, alreadyScannedDirectories);
   }
}
