package de.scrum_master.tdd;

import java.util.List;

class Tile {
  private int sizeOrder;

  Tile(int sizeOrder) {
    this.sizeOrder = sizeOrder;
  }

  public static float average(float... corners) {
    return -999999;
  }

  public int getEdgeLength() {
    return (int) (Math.pow(2, sizeOrder) + 1);
  }
}
