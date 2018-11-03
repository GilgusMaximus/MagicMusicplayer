package ControllerClasses;

import FileClasses.Musicfile;
import java.lang.reflect.Array;
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
    printList();
    quickSort();
    System.out.println("sortiert");
    printList();
  }
  void printList(){
    for(int x : list)
      System.out.println(x);
  }
  public void quickSort(){
    int size = musicfiles.size();
    int a = 0, b = size-1;
    quickSortRec(a, b, (int)(Math.random()*size+1));
  }

  private void quickSortRec(int a, int b, int pivot){
    if(a == b || pivot > 15)
      return;
    int startA = a, startB = b;
    String pivotTitle = musicfiles.get(pivot).getTitle();
    while(a != pivot && b != pivot){
      while(a < musicfiles.size() && musicfiles.get(a).compare(musicfiles.get(a).getTitle(), pivotTitle) < 0)
        a++;
      while(b > -1 && musicfiles.get(b).compare(musicfiles.get(b).getTitle(), pivotTitle) > 0)
        b--;
      if(a < b)
        switchItems(a,b);
    }
    if(a != pivot){
      switchItems(a, pivot);
    }else{
      switchItems(b, pivot);
    }
    quickSortRec(startA, pivot-1, (int)(Math.random()*(pivot-1-startA+1)));
    quickSortRec(pivot+1, startB, (int)(Math.random()*(startB-pivot+1+1)));
  }
  private void switchItems(int a, int b){
    int help = list.get(a);
    list.set(a, b);
    list.set(b, help);
  }
  private void mergeSort(){
    ArrayList<Integer> helper = new ArrayList<>();
    for(int i = 0; i < musicfiles.size(); i++)
      helper.add(i);
    int a = 0, b = musicfiles.size()-1;

  }
  private void mergeSortRec(int a, int b, ArrayList<Integer> helper){
    if(a+1 == b){
      if(musicfiles.get(helper.get(a)).getTitle().compareTo(musicfiles.get(helper.get(b)).getTitle()) < 0){
      }else{
        int h = helper.get(a);
        helper.set(a, helper.get(b));
        helper.set(b, h);
      }
      return;
    }else{
      int half = (a + b)/2;
      mergeSortRec(a, half, helper);
      mergeSortRec(half+1, b, helper);
      int[] array = new int[b-a+1];
      int arrayCounter = 0;
      int h = half;
      half++;
      while (a != h+1){
        if(musicfiles.get(helper.get(a)).getTitle().compareTo(musicfiles.get(helper.get(half)).getTitle()) < 0){
          array[arrayCounter] = helper.get(a);
          a++;
        }else{
          array[arrayCounter] = helper.get(half);
          half++;
        }
      }
    }

  }
}
