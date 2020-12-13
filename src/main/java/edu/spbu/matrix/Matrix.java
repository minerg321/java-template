package edu.spbu.matrix;

/**
 *
 */
public abstract class Matrix
{
  protected double[][] data;
  int h,w;
  abstract public Matrix mul(Matrix o);
  abstract public String toString();
  abstract public boolean equals(Object o);
  abstract public double getElement(int i, int j);
  abstract public int getHeight();
  abstract public int getWidth();
  abstract public Matrix transpose();
}