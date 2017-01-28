package de.scrum_master.tdd;

class Tile {
  private int sizeOrder;

  Tile(int sizeOrder) {
    this.sizeOrder = sizeOrder;
  }

  public static float average(float... corners) {
    float sum = 0;
    for (float corner : corners)
      sum += corner;
    return sum / corners.length;
  }

  public int getEdgeLength() {
    return (int) (Math.pow(2, sizeOrder) + 1);
  }
}
