package de.scrum_master.tdd;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.util.Arrays.copyOf;

class Tile {
  private static final Random RANDOM = new Random();

  private final int sizeOrder;
  private final int edgeLength;
  private final int maxIndex;
  private final float[][] matrix;
  private final float[] randomAmplitudes;

  public static class Factory {
    private final int sizeOrder;
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

    maxIndex = edgeLength - 1;
    matrix = new float[edgeLength][edgeLength];
    matrix[0][0] = factory.bottomLeft;
    matrix[maxIndex][0] = factory.bottomRight;
    matrix[0][maxIndex] = factory.topLeft;
    matrix[maxIndex][maxIndex] = factory.topRight;

    randomAmplitudes = new float[sizeOrder];
    for (int i = 0; i < sizeOrder; i++)
      // Powers of < 1.5: too noisy; 1.5-2: ok, increasingly smooth; > 2: too smooth, boring
      randomAmplitudes[i] = abs(factory.randomAmplitude) / (float) pow(2, i);

    generateLandscape(0);
  }

  public static Factory ofSizeOrder(int sizeOrder) {
    return new Factory(sizeOrder);
  }

  private void generateLandscape(int detailLevel) {
    if (detailLevel >= sizeOrder)
      return;
    int stepSize = (int) pow(2, sizeOrder - detailLevel);
    float randomAmplitude = randomAmplitudes[detailLevel];
    performDiamondStep(stepSize, randomAmplitude);
    performSquareStep(stepSize, randomAmplitude);
    generateLandscape(++detailLevel);
  }

  private void performDiamondStep(int stepSize, float randomAmplitude) {
    int semiStepSize = stepSize / 2;
    List<Float> points = new LinkedList<>();
    for (int x = 0; x < maxIndex; x += stepSize) {
      for (int y = 0; y < maxIndex; y += stepSize) {
        points.clear();
        points.add(matrix[x][y]);
        points.add(matrix[x + stepSize][y]);
        points.add(matrix[x + stepSize][y + stepSize]);
        points.add(matrix[x][y + stepSize]);
        matrix[x + semiStepSize][y + semiStepSize] = randomise(average(points), randomAmplitude);
      }
    }
  }

  private void performSquareStep(int stepSize, float randomAmplitude) {
    int semiStepSize = stepSize / 2;
    List<Float> points = new LinkedList<>();
    for (int x = 0; x < maxIndex; x += stepSize) {
      for (int y = 0; y < maxIndex; y += stepSize) {

        // Bottom
        points.clear();
        points.add(matrix[x][y]);
        points.add(matrix[x + stepSize][y]);
        points.add(matrix[x + semiStepSize][y + semiStepSize]);
        if (y > 0)
          points.add(matrix[x + semiStepSize][y - semiStepSize]);
        matrix[x + semiStepSize][y] = randomise(average(points), randomAmplitude);

        // Top
        points.clear();
        points.add(matrix[x][y + stepSize]);
        points.add(matrix[x + stepSize][y + stepSize]);
        points.add(matrix[x + semiStepSize][y + semiStepSize]);
        if (y + stepSize < maxIndex)
          points.add(matrix[x + semiStepSize][y + stepSize + semiStepSize]);
        matrix[x + semiStepSize][y + stepSize] = randomise(average(points), randomAmplitude);

        // Left
        points.clear();
        points.add(matrix[x][y]);
        points.add(matrix[x][y + stepSize]);
        points.add(matrix[x + semiStepSize][y + semiStepSize]);
        if (x > 0)
          points.add(matrix[x - semiStepSize][y + semiStepSize]);
        matrix[x][y + semiStepSize] = randomise(average(points), randomAmplitude);

        // Right
        points.clear();
        points.add(matrix[x + stepSize][y]);
        points.add(matrix[x + stepSize][y + stepSize]);
        points.add(matrix[x + semiStepSize][y + semiStepSize]);
        if (x + stepSize < maxIndex)
          points.add(matrix[x + stepSize + semiStepSize][y + semiStepSize]);
        matrix[x + stepSize][y + semiStepSize] = randomise(average(points), randomAmplitude);

      }
    }
  }

  static float average(List<Float> corners) {
    float sum = 0;
    for (float corner : corners)
      sum += corner;
    return sum / corners.size();
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
