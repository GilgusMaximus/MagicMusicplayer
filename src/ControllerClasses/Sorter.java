package ControllerClasses;

import FileClasses.Musicfile;
import java.util.ArrayList;

public class Sorter extends Thread{
  private ArrayList<ArrayList<Integer>> lists;
  private ArrayList<Integer> list;
  private ArrayList<Musicfile> musicfiles;
  private int current = 0;

  Sorter(ArrayList<Musicfile> Musicfiles){
    musicfiles = Musicfiles;
    lists = new ArrayList<>();
    lists.add(new ArrayList<Integer>()); //title
    lists.add(new ArrayList<Integer>()); //album
    lists.add(new ArrayList<Integer>()); //artist
    for(ArrayList a : lists)
      for(int i = 0; i < musicfiles.size(); i++){
        a.add(i);
      }
  }

  @Override
  public void run() {
    for(current = 0; current < 3; current++) {
      list = lists.get(current);
      quickSort();
    }
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
    String p = null;
    switch(current){
      case 0: p = musicfiles.get(list.get(pivot)).getTitle(); break;
      case 1: p = musicfiles.get(list.get(pivot)).getAlbum(); break;
      case 2: p = musicfiles.get(list.get(pivot)).getArtists()[0];break;
    }
    switchItems(b, pivot);
    pivot = b;
    do{
      do{
        a++;
      }while(musicfiles.get(list.get(a)).compare(p, current) < 0);
      do{
        b--;
      }while(b >= a && musicfiles.get(list.get(b)).compare(p, current) > 0);
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
  ArrayList<Integer> getList(){
    return lists.get(2);
  }
}
