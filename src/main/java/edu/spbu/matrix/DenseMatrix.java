package edu.spbu.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;


public class DenseMatrix extends Matrix
{
  double[][] data;
  int h;
  int w;
  public DenseMatrix(String fileName)  {
    Scanner datascan;
    try {
      datascan = new Scanner(new File(fileName));
    } catch (FileNotFoundException e) {
      System.err.println("Невозможно загрузить матрицу: файл не найден");
      return;
    }
    ArrayList<Double> tempdata=new ArrayList<>();
    this.h=0;
    this.w=0;
    int z;
    while(datascan.hasNextLine())
    {
      z=0;
      this.h++;
      String[] array = datascan.nextLine().split(" ");
      for (String s : array) {
        if(s.length()!=0)
        {
          double el = Double.parseDouble(s);
          tempdata.add(el);
          z++;
        }
      }
      if(this.w==0)
      {
        this.w=z;
      }
    }
    datascan.close();
    this.data=new double[h][w];
    for (int i=0;i<tempdata.size();i++)
    {
      this.data[i/w][i%w]=tempdata.get(i);
    }
  }
  public DenseMatrix(double[][] matrix)
  {
    this.h = matrix.length;
    this.w = matrix[0].length;
    this.data = matrix;
  }
  /** получаем Высоту матрицы **/
  @Override
  public int getHeight() {
    return this.h;
  }
  /** получаем Ширину матрицы **/
  @Override
  public int getWidth() {
    return this.w;
  }

  @Override
  public double getElement(int i, int j)
  {
    return this.data[i][j];
  }

  @Override
  public String toString() {
    StringBuilder alpha= new StringBuilder();
    for(int i=0;i<h;i++)
    {
      for(int j=0;j<w;j++)
      {
        alpha.append(data[i][j]).append(" ");
      }
      if(i!=h-1) alpha.append("\n");
    }
    return alpha.toString();
  }
  @Override
  public Matrix mul(Matrix o) {
    if (this.w != o.getHeight()) {
      System.out.println("Операция невозможна: у матриц нет подходящих размеров");
    }
    if (o instanceof DenseMatrix) {
      return dmul((DenseMatrix) o);
    }
    else
    {
      if (o instanceof SparseMatrix) {
        return dmul((SparseMatrix) o);
      }
      return null;
    }
  }
    private DenseMatrix mul(DenseMatrix o)
    {
      int newHeight = this.h, newWidth = o.getWidth();
      double[][] data = new double[newHeight][newWidth];
      DenseMatrix result = new DenseMatrix (data);
    for (int i = 0; i < h; i++)
    {
      for (int j = 0; j < o.w; j++)
      {
        result.data[i][j] = 0;
        for (int l = 0; l < o.h; l++)
        {
          result.data[i][j]+= this.data[i][l] * o.data[l][j];
        }
      }
    }
    return result;
  }
  /** Умножение денс на денс**/
  private Matrix dmul(DenseMatrix o) {
    final DenseMatrix matrixF = new DenseMatrix(this.data);
    final DenseMatrix matrixS = new DenseMatrix(o.data);
    int newHeight = this.h, newWidth = o.getWidth();
    final double[][] newMatrix = new double[newHeight][newWidth];

    class DDMulThreaded implements Runnable {
      final int subHeightF, subHeightS, subWidthF, subWidthS;

      public DDMulThreaded(int subHeightF, int subHeightS, int subWidthF, int subWidthS){
        this.subHeightF = subHeightF;
        this.subHeightS = subHeightS;
        this.subWidthF = subWidthF;
        this.subWidthS = subWidthS;
      }

      @Override
      public void run(){
        for (int i = subHeightF; i < subHeightS; i++) {
          for (int j = subWidthF; j < subWidthS; j++) {
            for (int k = 0; k < matrixF.w; k++) {
              newMatrix[i][j] += matrixF.data[i][k] * matrixS.data[k][j];
            }
          }
        }
      }
    }
    int subHeight = newHeight / 2, subWidth = newWidth / 2;
    try {
      Thread threadFirst = new Thread(new DDMulThreaded(0, subHeight, 0, subWidth));
      Thread threadSecond = new Thread(new DDMulThreaded(0, subHeight, subWidth, newWidth));
      Thread threadThird = new Thread(new DDMulThreaded(subHeight, newHeight, 0, subWidth));
      Thread threadFourth = new Thread(new DDMulThreaded(subHeight, newHeight, subWidth, newWidth));

      threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
      threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

    } catch (Exception e){
      e.printStackTrace();
    }
    return new DenseMatrix(newMatrix);
  }
  @Override
  public Matrix transpose(){
    double[][] newMatrix = new double[this.w][this.h];
    for (int i = 0; i < this.h; i++) {
      for (int j = 0; j < this.w; j++) {
        newMatrix[j][i] = this.data[i][j];
      }
    }
    return new DenseMatrix(newMatrix);
  }
  /** Дэнс на спарс**/
  private Matrix dmul(SparseMatrix o)
  {
    /** Умножаемая матрица**/
    final SparseMatrix matrixS = new SparseMatrix(o.matrix);
    /** Матрица на которую умножают**/
    final DenseMatrix matrixD = new DenseMatrix(this.data);
    int newHeight = this.h, newWidth = o.getWidth();
    /** Результирующая матрица**/
    final double[][] newMatrix = new double[newHeight][newWidth];
    /** Заполнение результирующей матрицы нулями**/
    for (int i = 0; i < this.h; i++) {
      Arrays.fill(newMatrix[i], 0);
    }

    class SDMulThreaded implements Runnable {
      /** Обьявляем границы работы потоков**/
      final int subWidthStart, subWidthEnd, subHeightStart, subHeightEnd;

      public SDMulThreaded(int subWidthF, int subWidthS, int subHeightF, int subHeightS) {
        this.subWidthStart = subWidthF;
        this.subWidthEnd = subWidthS;
        this.subHeightStart = subHeightF;
        this.subHeightEnd = subHeightS;
      }

      @Override
      /** Функция работы с каждым потоком**/
      public void run(){
        for (int i = subWidthStart; i < subWidthEnd; i++) {
          for (int j = subHeightStart; j < subHeightEnd; j++) {
            int startPoint = matrixS.numElInRow[j], endPoint = matrixS.numElInRow[j + 1];
            for (int k = startPoint; k < endPoint; k++){
              newMatrix[j][i] += matrixS.elements[k]*matrixD.data[matrixS.columnIndex[k]][i];
            }
          }
        }
      }
    }
    /** найдём серединки матрицы по ширине и по длине**/
    int midHeight = newHeight / 2, midWidth = newWidth / 2;
    try {
      Thread threadFirst = new Thread(new SDMulThreaded(0, midWidth, 0, midHeight));
      Thread threadSecond = new Thread(new SDMulThreaded(midWidth, newWidth, 0, midHeight));
      Thread threadThird = new Thread(new SDMulThreaded(0, midWidth, midHeight, newHeight));
      Thread threadFourth = new Thread(new SDMulThreaded(midWidth, newWidth, midHeight, newHeight));
      /** Запустим каждый поток, и для каждого выполним join() (Чтобы большой поток ждал выполнения маленьких)**/
      threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
      threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

    }
    /** анализируем ошибочку**/
    catch (Exception e){
      e.printStackTrace();
    }

    return new SparseMatrix(newMatrix);
  }
  public boolean equals(Object o) {
    if (o instanceof DenseMatrix) {
      return this.ddequals(o);
    } else {
      return this.dsequal(o);
    }
  }

  public boolean dsequal(Object o) {
    SparseMatrix s = (SparseMatrix) o;
    if ((this.h != s.height) || (this.w != s.width)) return false;
    for (int i = 0; i < s.height; i++) {
      for (int j = 0; j < s.width; j++) {
        if (this.data[i][j] != s.getElement(i, j)) return false;
      }
    }
    return true;
  }

  public boolean ddequals(Object o) {
    DenseMatrix m = (DenseMatrix) o;
    if ((this.h != m.h) || (this.w != m.w)) return false;
    for (int i = 0; i < m.h; i++) {
      for (int j = 0; j < m.w; j++) {
        if (m.data[i][j] != this.getElement(i, j)) return false;
      }
    }
    return true;
  }

}