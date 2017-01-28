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

  private static int log2(int number) {
    log(number) / log(2)
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

  @Unroll
  def "random value around #baseValue with amplitude #amplitude is within specified bounds"() {
    given:
    def randomValue = Tile.randomise(baseValue, amplitude)

    expect:
    randomValue >= baseValue - amplitude
    randomValue <= baseValue + amplitude

    where:
    baseValue | amplitude
    0         | 1
    0         | 10
    1         | 2
    10        | 3
    3         | 0
    123.45    | 5.67
  }

  @Unroll
  def "tile array of size order #sizeOrder has correctly initialised corners"() {
    given:
    def bottomLeft = 1
    def bottomRight = 2
    def topLeft = 3
    def topRight = 4

    when:
    def matrix = new Tile(sizeOrder, bottomLeft, bottomRight, topLeft, topRight).toArray()
    def maxIndex = matrix.length - 1

    then:
    matrix[0][0] == bottomLeft
    matrix[maxIndex][0] == bottomRight
    matrix[0][maxIndex] == topLeft
    matrix[maxIndex][maxIndex] == topRight

    where:
    sizeOrder << (1..10).collect()
  }

  @Unroll
  def "random amplitude #randomAmplitude for tile of size order #sizeOrder gets successively smaller for sub-tiles"() {
    given:
    def tile = new Tile(sizeOrder, randomAmplitude)

    when:
    def amplitudes = tile.getAmplitudes()
    println amplitudes
    def previousAmplitude = 999999.99

    then:
    amplitudes[0] >= 0
    for (float amplitude : amplitudes) {
      assert amplitude <= previousAmplitude
      previousAmplitude = amplitude
    }

    where:
    sizeOrder | randomAmplitude
    1         | 11
    2         | -22
    3         | 33
    4         | 0
    5         | 0
    6         | 4
    7         | 1.1
    8         | 2.5
    9         | -6
    10        | 1025
  }

}
