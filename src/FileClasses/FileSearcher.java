package FileClasses;

import java.io.File;
import java.util.ArrayList;
import ControllerClasses.textfileMarkers;

class FileSearcher {

   static ArrayList<String> findAllFiles(ArrayList<String> directories) {
      ArrayList<String> allFiles = new ArrayList<>();
      for (int i = 0; i < directories.size(); i++) {                            //go through all directories that have been passed
         //allFiles.add(textfileMarkers.searchDirectoryMarker + directories.get(i));
         String[] mainSubDirectories = new File(directories.get(i)).list();   //get all subdirectoriy names
         for (int j = 0; j < mainSubDirectories.length; j++)                   //go through all subdirectories recursively
         {
            findAllFilesRec(directories.get(i) + "\\" + mainSubDirectories[j], mainSubDirectories[j], allFiles);
         }
      }
      return allFiles;
   }

   private static void findAllFilesRec(String currentPath, String name, ArrayList<String> allFiles) {
      String[] subDirectoriesAndFiles = new File(currentPath).list(); //get the name of all subdirectories and files in the current directory
      if (subDirectoriesAndFiles == null) {                             //is the array equal to null?
         //yes -> the current directory is not a directory but a file
         String fileEnd = currentPath.substring(currentPath.length() - 3);
         if (fileEnd.equals("mp3") || fileEnd.equals("wav") || fileEnd.equals("m4a")) { //are the last 3 characters of the filename mp3/wav/m4a?
            //yes -> we found an mp3/wav/m4a file -> save
            allFiles.add(currentPath);
            //allFiles.add(name); //needed to save the file name -> easier to read it into the system again
         }else if(fileEnd.equals("png")||fileEnd.equals("jpg")||fileEnd.equals("jpeg")){
            System.out.println(currentPath);
         }
         return;
      } else {
         //no -> the current directory is indeed a directory
         for (int i = 0; i < subDirectoriesAndFiles.length; i++) {       //go through all sub directories recursively
            findAllFilesRec(currentPath + "\\" + subDirectoriesAndFiles[i], subDirectoriesAndFiles[i], allFiles);
         }
      }
   }
}
