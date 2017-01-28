package de.scrum_master.tdd

import spock.lang.Specification
import spock.lang.Unroll

import static java.lang.Math.log

class TileTest extends Specification {
  @Unroll
  def "tile of size order #sizeOrder has square edge length 2^#sizeOrder + 1 = #edgeLength"() {
    given:
    def tile = new Tile(sizeOrder)

    expect:
    tile.getEdgeLength() == edgeLength
    log2(edgeLength - 1) == sizeOrder

    where:
    sizeOrder | edgeLength
    1         | 3
    2         | 5
    3         | 9
    4         | 17
    5         | 33
    6         | 65
    7         | 129
    8         | 257
    9         | 513
    10        | 1025
  }

  @Unroll
  def "average of #corners is #average"(float[] corners, float average) {
    expect:
    Tile.average(corners) == average

    where:
    corners                  | average
    [0, 0, 0, 0]             | 0
    [5, 5, 5, 5]             | 5
    [1, 2, 2, 1]             | 1.5
    [1, 2, 3, 4]             | 2.5
    [-2, 2, -3, 3]           | 0
    [1, 2, 3]                | 2
    [1, 2]                   | 1.5
    [3]                      | 3
    [12, 34, 56, 78]         | 45
    [-11, 22, -33, 44]       | 5.5
    [1.23, 4.56, 7.89, 0.12] | 3.45
  }

  private static int log2(int number) {
    log(number) / log(2)
  }
}
