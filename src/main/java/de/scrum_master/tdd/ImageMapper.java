package de.scrum_master.tdd;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ImageMapper {
  public enum ColourMap {
    GMT_GLOBE("gmt_globe.txt", false),
    GMT_GLOBE_STRETCHED("gmt_globe.txt", true),
    GRAY_SIMPLE("gray_simple.txt", false),
    GRAY_STRETCHED("gray_stretched.txt", true),
    CLOUDS_STRETCHED("clouds_stretched.txt", true);

    private final String fileName;
    private final boolean stretchValues;

    ColourMap(String fileName, boolean stretchValues) {
      this.fileName = fileName;
      this.stretchValues = stretchValues;
    }
  }

  private final float[][] matrix;

  public ImageMapper(float[][] matrix) {
    this.matrix = matrix;
  }

  void saveImageToFile(String fileName, ColourMap colourMap) throws IOException, URISyntaxException {
    // Note 1: BufferedImage defines (0,0) as upper left corner, so the image is quasi upside-down
    // when compared to our semantic usage of terms like "top" and "bottom" elsewhere in the code.
    // Note 2: Gray-scale pics are saved in 24 bit RGB, we could also use use TYPE_BYTE_GRAY here.
    BufferedImage image = new BufferedImage(matrix.length, matrix.length, BufferedImage.TYPE_INT_RGB);
    int maxIndex = matrix.length - 1;

    ColourTable colourTable = ColourTable.fromFile(colourMap.fileName);
    if (colourMap.stretchValues) {
      float[] extremeValues = getExtremeValues();
      float minValue = extremeValues[0];
      float maxValue = extremeValues[1];
//      System.out.println("min/max = " + minValue + " / " + maxValue);
      colourTable.stretchEntries(minValue, maxValue);
    }

    for (int x = 0; x <= maxIndex; x++) {
      for (int y = 0; y <= maxIndex; y++) {
        Color color = colourTable.interpolateColour(matrix[x][y]);
        image.setRGB(x, y, color.getRGB());
      }
    }

    ImageIO.write(image, "PNG", new File(fileName + ".png"));
  }

  private float[] getExtremeValues() {
    int maxIndex = matrix.length - 1;
    float minValue = matrix[0][0];
    float maxValue = minValue;
    for (int x = 0; x <= maxIndex; x++) {
      for (int y = 0; y <= maxIndex; y++) {
        float currentValue = matrix[x][y];
        if (currentValue < minValue)
          minValue = currentValue;
        else if (currentValue > maxValue)
          maxValue = currentValue;
      }
    }
    return new float[]{minValue, maxValue};
  }

}
