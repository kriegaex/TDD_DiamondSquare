package de.scrum_master.tdd;

import java.util.Arrays;
import java.util.Random;

class Tile {
  private static final Random RANDOM = new Random();

  private int sizeOrder;
  private int edgeLength;
  private float[][] matrix;

  public Tile(int sizeOrder) {
    this.sizeOrder = sizeOrder;
    edgeLength = (int) (Math.pow(2, sizeOrder) + 1);
    matrix = new float[edgeLength][edgeLength];
  }

  public Tile(int sizeOrder, float bottomLeft, float bottomRight, float topLeft, float topRight) {
    this(sizeOrder);
    matrix[0][0] = bottomLeft;
    matrix[edgeLength - 1][0] = bottomRight;
    matrix[0][edgeLength - 1] = topLeft;
    matrix[edgeLength - 1][edgeLength - 1] = topRight;
  }

  static float average(float... corners) {
    float sum = 0;
    for (float corner : corners)
      sum += corner;
    return sum / corners.length;
  }

  static float randomise(float baseValue, float amplitude) {
    return baseValue + amplitude * (1 - 2 * RANDOM.nextFloat());
  }

  public int getEdgeLength() {
    return edgeLength;
  }

  public float[][] toArray() {
    final float[][] result = new float[edgeLength][];
    for (int i = 0; i < edgeLength; i++)
      result[i] = Arrays.copyOf(matrix[i], edgeLength);
    return result;
  }
}
