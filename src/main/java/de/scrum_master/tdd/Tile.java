package de.scrum_master.tdd;

class Tile {
  private int sizeOrder;

  Tile(int sizeOrder) {
    this.sizeOrder = sizeOrder;
  }

  public int getEdgeLength() {
    return (int) (Math.pow(2, sizeOrder) + 1);
  }
}
