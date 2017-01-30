package de.scrum_master.tdd

import spock.lang.Specification
import spock.lang.Unroll

import static de.scrum_master.tdd.ImageMapper.ColourMap.*
import static java.lang.Math.abs
import static java.lang.Math.log

class TileTest extends Specification {
  @Unroll
  "tile of size order #sizeOrder has square edge length 2^#sizeOrder + 1 = #edgeLength"() {
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
  "average of #corners is #average"() {
    expect:
    Tile.average(corners) == average

    where:
    corners                      | average
    [0f, 0f, 0f, 0f]             | 0f
    [5f, 5f, 5f, 5f]             | 5f
    [1f, 2f, 2f, 1f]             | 1.5f
    [1f, 2f, 3f, 4f]             | 2.5f
    [-2f, 2f, -3f, 3f]           | 0f
    [1f, 2f, 3f]                 | 2f
    [1f, 2f]                     | 1.5f
    [3f]                         | 3f
    [12f, 34f, 56f, 78f]         | 45f
    [-11f, 22f, -33f, 44f]       | 5.5f
    [1.23f, 4.56f, 7.89f, 0.12f] | 3.45f
  }

  @Unroll
  "random value around #baseValue with amplitude #amplitude is within specified bounds"() {
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
  "tile array of size order #sizeOrder has correctly initialised corners"() {
    given:
    def bottomLeft = -4000f
    def bottomRight = 8000f
    def topLeft = 2500f
    def topRight = -2500f

    when:
    def matrix = Tile
      .ofSizeOrder(sizeOrder)
      .bottomLeft(bottomLeft)
      .bottomRight(bottomRight)
      .topLeft(topLeft)
      .topRight(topRight)
      .randomAmplitude(5000)
      .create()
      .toArray()
    def maxIndex = matrix.length - 1

/*
    // Generate some nice pics in multiple variants per tile size
    def imageMapper = new ImageMapper(matrix)
    imageMapper.saveImageToFile("image-${sizeOrder}-globe", GMT_GLOBE)
    imageMapper.saveImageToFile("image-${sizeOrder}-gray-simple", GRAY_SIMPLE)
    imageMapper.saveImageToFile("image-${sizeOrder}-gray-stretched", GRAY_STRETCH_VALUES)
    imageMapper.saveImageToFile("image-${sizeOrder}-clouds-stretched", CLOUDS_STRETCHED)
*/

    then:
    matrix[0][0] == bottomLeft
    matrix[maxIndex][0] == bottomRight
    matrix[0][maxIndex] == topLeft
    matrix[maxIndex][maxIndex] == topRight

    where:
    sizeOrder << (1..11).collect()
  }


  @Unroll
  "random amplitude #randomAmplitude for tile of size order #sizeOrder gets successively smaller for sub-tiles"() {
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
  "check tile [#bottomLeft, #bottomRight, #topLeft, #topRight] of size order 1 with random amplitude #randomAmplitude"() {
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
    //println matrix

    when:
    float midPoint = (bottomLeft + bottomRight + topLeft + topRight) / 4
    float midPointRandomised = matrix[1][1]
    float bottomPoint = (bottomLeft + bottomRight + midPointRandomised) / 3
    float topPoint = (topLeft + topRight + midPointRandomised) / 3
    float leftPoint = (bottomLeft + topLeft + midPointRandomised) / 3
    float rightPoint = (bottomRight + topRight + midPointRandomised) / 3

    float midDeviation = abs(midPointRandomised - midPoint)
    float bottomDeviation = abs(matrix[1][0] - bottomPoint)
    float topDeviation = abs(matrix[1][2] - topPoint)
    float leftDeviation = abs(matrix[0][1] - leftPoint)
    float rightDeviation = abs(matrix[2][1] - rightPoint)

    then:
    if (randomAmplitude) {
      // Caveat: In rare cases the random deviation can be exactly 0.
      assert midDeviation > 0
      assert bottomDeviation > 0
      assert topDeviation > 0
      assert leftDeviation > 0
      assert rightDeviation > 0
    } else {
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
  }
}
