package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort (int[] array,int start, int end)
  {
    if (array.length == 0||start >= end)
      return;
    int mid = start +(end - start)/ 2;
    int el = array[mid];
    int i = start;
    int j = end;
    while(i<=j)
    {
      while (array[i] < el) {
        i++;
      }
      while(array[j] > el) {
        j--;
      }
      if(i<=j)
      {
        int tmp;
        tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
        i++;
        j--;
      }
    }
    if(start<j) {
      sort(array, start, j);
    }
    if(end>i) {
      sort (array, i,  end);
    }
}

public static void sort  (List<Integer> list) {
    Collections.sort(list);
  }

}
