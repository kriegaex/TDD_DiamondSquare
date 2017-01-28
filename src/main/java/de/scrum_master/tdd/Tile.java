package de.scrum_master.tdd;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.util.Arrays.copyOf;

class Tile {
  private static final Random RANDOM = new Random();

  private int sizeOrder;
  private int edgeLength;
  private float[][] matrix;
  private float[] randomAmplitudes;

  public static class Factory {
    private int sizeOrder;
    private float randomAmplitude, bottomLeft, bottomRight, topLeft, topRight;

    public Factory(int sizeOrder) {
      this.sizeOrder = sizeOrder;
    }

    public Factory randomAmplitude(float randomAmplitude) {
      this.randomAmplitude = randomAmplitude;
      return this;
    }

    public Factory bottomLeft(float bottomLeft) {
      this.bottomLeft = bottomLeft;
      return this;
    }

    public Factory bottomRight(float bottomRight) {
      this.bottomRight = bottomRight;
      return this;
    }

    public Factory topLeft(float topLeft) {
      this.topLeft = topLeft;
      return this;
    }

    public Factory topRight(float topRight) {
      this.topRight = topRight;
      return this;
    }

    public Tile create() {
      return new Tile(this);
    }
  }

  private Tile(Factory factory) {
    sizeOrder = factory.sizeOrder;
    edgeLength = (int) pow(2, sizeOrder) + 1;

    int maxIndex = edgeLength - 1;
    matrix = new float[edgeLength][edgeLength];
    matrix[0][0] = factory.bottomLeft;
    matrix[maxIndex][0] = factory.bottomRight;
    matrix[0][maxIndex] = factory.topLeft;
    matrix[maxIndex][maxIndex] = factory.topRight;

    randomAmplitudes = new float[sizeOrder];
    for (int i = 0; i < sizeOrder; i++)
      randomAmplitudes[i] =
        abs(factory.randomAmplitude) *
          ((float) pow(2, sizeOrder - i) + 1) / edgeLength;
  }

  public static Factory ofSizeOrder(int sizeOrder) {
    return new Factory(sizeOrder);
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
      result[i] = copyOf(matrix[i], edgeLength);
    return result;
  }

  public float[] getAmplitudes() {
    return copyOf(randomAmplitudes, randomAmplitudes.length);
  }
}
