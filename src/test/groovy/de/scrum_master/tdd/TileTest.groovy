package de.scrum_master.tdd

import spock.lang.Specification
import spock.lang.Unroll

import static java.lang.Math.abs
import static java.lang.Math.log

class TileTest extends Specification {
  @Unroll
  def "tile of size order #sizeOrder has square edge length 2^#sizeOrder + 1 = #edgeLength"() {
    given:
    def tile = Tile.ofSizeOrder(sizeOrder).create()

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
    0f        | 1f
    0f        | 10f
    1f        | 2f
    10f       | 3f
    3f        | 0f
    123.45f   | 5.67f
  }

  @Unroll
  def "tile array of size order #sizeOrder has correctly initialised corners"() {
    given:
    def bottomLeft = 1f
    def bottomRight = 2f
    def topLeft = 3f
    def topRight = 4f

    when:
    def matrix = Tile
      .ofSizeOrder(sizeOrder)
      .bottomLeft(bottomLeft)
      .bottomRight(bottomRight)
      .topLeft(topLeft)
      .topRight(topRight)
      .create()
      .toArray()
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
    def tile = Tile.ofSizeOrder(sizeOrder).randomAmplitude(randomAmplitude).create()

    when:
    def amplitudes = tile.getAmplitudes()
    //println amplitudes
    def previousAmplitude = 999999.99

    then:
    amplitudes[0] == abs(randomAmplitude)
    for (float amplitude : amplitudes) {
      assert amplitude <= previousAmplitude
      previousAmplitude = amplitude
    }

    where:
    sizeOrder | randomAmplitude
    1         | 11f
    2         | -22f
    3         | 33f
    4         | 0f
    5         | 0f
    6         | 4f
    7         | 1.1f
    8         | 2.5f
    9         | -6f
    10        | 1025f
  }

  @Unroll
  def "check tile [#bottomLeft, #bottomRight, #topLeft, #topRight] of size order 1 with random amplitude #randomAmplitude"() {
    given:
    def matrix = Tile
      .ofSizeOrder(1)
      .bottomLeft(bottomLeft)
      .bottomRight(bottomRight)
      .topLeft(topLeft)
      .topRight(topRight)
      .randomAmplitude(randomAmplitude)
      .create()
      .toArray()
    println matrix

    when:
    float midPoint = (bottomLeft + bottomRight + topLeft + topRight) / 4
    float bottomPoint = (bottomLeft + bottomRight + midPoint) / 3
    float topPoint = (topLeft + topRight + midPoint) / 3
    float leftPoint = (bottomLeft + topLeft + midPoint) / 3
    float rightPoint = (bottomRight + topRight + midPoint) / 3

    float midDeviation = abs(matrix[1][1] - midPoint)
    float bottomDeviation = abs(matrix[1][0] - bottomPoint)
    float topDeviation = abs(matrix[1][2] - topPoint)
    float leftDeviation = abs(matrix[0][1] - leftPoint)
    float rightDeviation = abs(matrix[2][1] - rightPoint)

    then:
    if (isRandomised) {
      assert 0 < midDeviation
      assert 0 < bottomDeviation
      assert 0 < topDeviation
      assert 0 < leftDeviation
      assert 0 < rightDeviation
    }
    else {
      assert midDeviation <= 1.0e7
      assert bottomDeviation <= 1.0e7
      assert topDeviation <= 1.0e7
      assert leftDeviation <= 1.0e7
      assert rightDeviation <= 1.0e7
    }

    and:
    midDeviation <= randomAmplitude
    bottomDeviation <= randomAmplitude
    topDeviation <= randomAmplitude
    leftDeviation <= randomAmplitude
    rightDeviation <= randomAmplitude

    where:
    bottomLeft | bottomRight | topLeft | topRight | randomAmplitude
    0          | 0           | 0       | 0        | 0
    0          | 1           | 2       | 3        | 0
    -10        | 3           | 22      | -5       | 0
    10         | 13          | 2       | 0        | 0
    999        | 9           | 11      | 77       | 0
    0          | 0           | 0       | 0        | 1
    0          | 1           | 2       | 3        | 2
    -10        | 3           | 22      | -5       | 3
    10         | 13          | 2       | 0        | 4
    999        | 9           | 11      | 77       | 10

    isRandomised = randomAmplitude != 0
  }
}
