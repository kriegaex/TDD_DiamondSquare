package de.scrum_master.tdd;

import java.util.Random;

class Tile {
  private static final Random RANDOM = new Random();

  private int sizeOrder;

  public Tile(int sizeOrder) {
    this.sizeOrder = sizeOrder;
  }

  public Tile(int sizeOrder, float bottomLeft, float bottomRight, float topLeft, float topRight) {
    this(sizeOrder);
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
    return (int) (Math.pow(2, sizeOrder) + 1);
  }

  public float[][] toArray() {
    return new float[getEdgeLength()][getEdgeLength()];
  }
}
