package ControllerClasses;

import FileClasses.Musicfile;
import java.util.ArrayList;

public class Sorter extends Thread{
  ArrayList<Integer> list;
  ArrayList<Musicfile> musicfiles;
  Sorter(ArrayList<Musicfile> Musicfiles){
    musicfiles = Musicfiles;
    list = new ArrayList<>();
    for(int i = 0; i < musicfiles.size(); i++){
      list.add(i);
    }
  }

  @Override
  public void run() {
    quickSort();
  }
  private void quickSort(){
    int size = musicfiles.size();
    int a = 0, b = size-1;
    quickSortRec(a, b, (int)(Math.random()*size));
  }

  private void quickSortRec(int a, int b, int pivot){
    if(a >= b)
      return;
    int aS = a, bS = b;
    a--;
    String p = musicfiles.get(list.get(pivot)).getTitle();
    switchItems(b, pivot);
    pivot = b;
    do{
      do{
        a++;
      }while(musicfiles.get(list.get(a)).compare(p) < 0);
      do{
        b--;
      }while(b >= a && musicfiles.get(list.get(b)).compare(p) > 0);
      if(a < b)
        switchItems(a, b);
    }while(a < b);
    switchItems(a, pivot);
    quickSortRec(aS, a-1, a-1);
    quickSortRec(a+1, bS, bS);
  }
  private void switchItems(int a, int b){
    int help = list.get(a);
    list.set(a, list.get(b));
    list.set(b, help);
  }
  public ArrayList<Integer> getList(){
    return list;
  }
}
