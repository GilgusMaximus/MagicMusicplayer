package fxml;

import ControllerClasses.MusicManager;
import java.io.File;
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

   private MusicManager manager;

   public void setMusicManager(MusicManager Manager){
      manager = Manager;
   }

   int counter = 0;

   public void addDirectoryToList(){
      TextField field = new TextField();
      field.setText(directoryField.getText());
      field.setId(""+counter++);
      field.setOnMouseClicked(event -> {delete(field);});
      field.setEditable(false);

      addedDirectoriesList.getChildren().add(field);
   }
   public void delete(TextField field){
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
      System.out.println("HI");
     manager.activateMusicWindows();
   }
}
