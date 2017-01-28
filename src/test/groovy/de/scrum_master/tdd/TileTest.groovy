package de.scrum_master.tdd

import spock.lang.Specification
import spock.lang.Unroll

class TileTest extends Specification {
  @Unroll
  def "tile of size order #sizeOrder has square edge length #edgeLength"() {
    given:
    def tile = new Tile(sizeOrder)

    expect:
    tile.getEdgeLength() == edgeLength

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
}
