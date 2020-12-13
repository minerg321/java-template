package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */

  @Test
  public void dmulDD() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void dmulDS() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void dmulSD() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void dmulSS() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void mulDD() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void mulDS() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void mulSD() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void mulSS() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void testString() {
    Matrix m1 = new DenseMatrix("m1.txt");
    System.out.println(m1.toString());
    Matrix m2 = new SparseMatrix("m1.txt");
    System.out.println(m2.toString());
  }

  @Test
  public void testEqualsDD() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m1.txt");
    Matrix m3 = new DenseMatrix("m1.txt");
    Matrix m4 = new DenseMatrix("m2.txt");
    assertTrue(m1.equals(m2));
    assertFalse(m3.equals(m4));
  }

  @Test
  public void testEqualsSS() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m1.txt");
    Matrix m3 = new SparseMatrix("m1.txt");
    Matrix m4 = new SparseMatrix("m2.txt");
    assertTrue(m1.equals(m2));
    assertFalse(m3.equals(m4));
  }

  @Test
  public void testEqualsSD() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m1.txt");
    Matrix m3 = new SparseMatrix("m1.txt");
    Matrix m4 = new DenseMatrix("m2.txt");
    assertFalse(m3.equals(m4));
    assertTrue(m1.equals(m2));
  }
  @Test
  public void testEqualsDS() {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m1.txt");
    Matrix m3 = new SparseMatrix("m1.txt");
    Matrix m4 = new DenseMatrix("m2.txt");
    assertFalse(m4.equals(m3));
    assertTrue(m2.equals(m1));
  }
}