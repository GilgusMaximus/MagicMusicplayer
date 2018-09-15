import java.io.File;
import java.util.ArrayList;

public class FileSearcher {


   public static ArrayList<String> findAllFiles(ArrayList<String> directories){
       ArrayList<String> allFiles = new ArrayList<>();
       for(int i = 0; i < directories.size(); i++) {                            //go through all directories that have been passed
           String[] mainSubDirectories = new File(directories.get(i)).list();   //get all subdirectoriy names
           for(int j = 0; j < mainSubDirectories.length; j++)                   //go through all subdirectories recursively
               findAllFilesRec(directories.get(i) + "\\" +  mainSubDirectories[j], mainSubDirectories[j], allFiles);
       }
       return allFiles;
   }

    private static void findAllFilesRec(String currentPath, String name, ArrayList<String> allFiles){
        String[] subDirectoriesAndFiles = new File(currentPath).list(); //get the name of all subdirectories and files in the current directory
        if(subDirectoriesAndFiles == null){                             //is the array equal to null?
                //yes -> the current directory is not a directory but a file
            if(currentPath.substring(currentPath.length()-3).equals("mp3")) { //are the last 3 characters of the filename mp3?
                //yes -> we found an mp3 file -> save
                allFiles.add(currentPath);
                allFiles.add(name);
            }
            else
                //no -> print that file for debug purposes only
                //System.out.println(currentPath + " not an mp3 file");
            return;
        }else{
                //no -> the current directory is indeed a directory
            for(int i = 0; i < subDirectoriesAndFiles.length; i++) {       //go through all sub directories recursively
                findAllFilesRec(currentPath + "\\" + subDirectoriesAndFiles[i], subDirectoriesAndFiles[i], allFiles);
            }
        }
    }
}
