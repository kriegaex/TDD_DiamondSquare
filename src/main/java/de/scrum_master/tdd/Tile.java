package de.scrum_master.tdd;

class Tile {
  private int sizeOrder;

  public Tile(int sizeOrder) {
    this.sizeOrder = sizeOrder;
  }

  static float average(float... corners) {
    float sum = 0;
    for (float corner : corners)
      sum += corner;
    return sum / corners.length;
  }

  static float randomise(float baseValue, float amplitude) {
    return -999999;
  }

  public int getEdgeLength() {
    return (int) (Math.pow(2, sizeOrder) + 1);
  }
}
