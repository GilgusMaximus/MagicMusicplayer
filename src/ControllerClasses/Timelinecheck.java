package ControllerClasses;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import sun.reflect.annotation.ExceptionProxy;

public class Timelinecheck extends  Thread {
   MediaPlayer player;

   Timelinecheck(MediaPlayer Player){
      player = Player;
   }

   @Override
   public void run() {
      while (player.getStatus() == Status.PLAYING) {
         System.out.println(time++);
         try {
            sleep(1000);
         }catch(Exception e){

         }
      }
   }
   int time = 0;
}
