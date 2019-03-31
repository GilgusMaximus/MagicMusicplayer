package fxml;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class DirectoryUIController {

   @FXML
   TextField directoryField;
   @FXML
   AnchorPane anchorpane;

   private void browseButtonclicked(){
      DirectoryChooser chooser = new DirectoryChooser();
      Stage stage = (Stage) anchorpane.getScene().getWindow();
      File file = chooser.showDialog(stage);
      if(file != null){
         System.out.println("PATH: " + file.getPath());
      }
   }
}
