package de.scrum_master.tdd;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class ColourTable {
  private static final Class<?> CLASS = ColourTable.class;

  private Entry[] entries;

  public static ColourTable fromFile(String fileName) throws IOException, URISyntaxException {
    ColourTable colourTable = new ColourTable();
    colourTable.entries = Files
      .lines(Paths.get(CLASS.getResource("/" + fileName).toURI()))
      .filter(line -> !line.matches("#.*|\\s*"))
      .map(ColourTable::namedColourToRGB)
      .map(line -> line.split("[\\s/]+"))
      .map(values ->
        new Entry(
          parseFloat(values[0]),
          new Color(parseInt(values[1]), parseInt(values[2]), parseInt(values[3]))
        )
      )
      .toArray(Entry[]::new);
    return colourTable;
  }

  private static String namedColourToRGB(String line) {
    // TODO: Remove this method when fix for http://gmt.soest.hawaii.edu/issues/996 has been released
    Matcher colourNameMatcher = Pattern
      .compile("(.+ )(gray([0-9]+)|[a-zA-Z]+)")
      .matcher(line.toLowerCase(Locale.ENGLISH));
    if (!colourNameMatcher.matches())
      return line;
    String lineWithoutColour = colourNameMatcher.group(1);
    String colourName = colourNameMatcher.group(2);
    String colourRGB;
    switch (colourName) {
      case "black":
        colourRGB = "0/0/0";
        break;
      case "white":
        colourRGB = "255/255/255";
        break;
      default:
        if (!colourName.startsWith("gray"))
          throw new IllegalArgumentException("unknown colour name " + colourName);
        float rgbValue = Float.valueOf(colourNameMatcher.group(3)) / 100;
        Color color = new Color(rgbValue, rgbValue, rgbValue);
        String redValue = ((Integer) color.getRed()).toString();
        colourRGB = redValue + "/" + redValue + "/" + redValue;
    }
//    System.out.println(colourName + " -> " + colourRGB);
    return lineWithoutColour + colourRGB;
  }

  public void stretchEntries(float minHeight, float maxHeight) {
    float minHeightOld = entries[0].minHeight;
    float maxHeightOld = entries[entries.length - 1].minHeight;
    float intervalOld = maxHeightOld - minHeightOld;
    float interval = maxHeight - minHeight;
    float stretchFactor = interval / intervalOld;
    for (Entry entry : entries)
      entry.minHeight = minHeight + (entry.minHeight - minHeightOld) * stretchFactor;
  }

  public Color interpolateColour(float height) {
    if (height < entries[0].minHeight)
      return entries[0].color;
    int maxIndex = entries.length - 1;
    if (height > entries[maxIndex].minHeight)
      return entries[maxIndex].color;

    Entry tempEntry = new Entry(height, null);
    int index = Arrays.binarySearch(entries, tempEntry);
    if (index >= 0)
      return entries[index].color;
    index = -(++index);
    Entry leftEntry = entries[index - 1];
    Entry rightEntry = entries[index];
//    System.out.println("left:  " + leftEntry.color);
//    System.out.println("right: " + rightEntry.color);

    float rangeRatio = (tempEntry.minHeight - leftEntry.minHeight) / (rightEntry.minHeight - leftEntry.minHeight);

    int leftRed = leftEntry.color.getRed();
    int rightRed = rightEntry.color.getRed();
    int leftGreen = leftEntry.color.getGreen();
    int rightGreen = rightEntry.color.getGreen();
    int leftBlue = leftEntry.color.getBlue();
    int rightBlue = rightEntry.color.getBlue();

    return new Color(
      Math.round(leftRed + (rightRed - leftRed) * rangeRatio),
      Math.round(leftGreen + (rightGreen - leftGreen) * rangeRatio),
      Math.round(leftBlue + (rightBlue - leftBlue) * rangeRatio)
    );
  }

  public static class Entry implements Comparable<Entry> {
    private float minHeight;
    private Color color;

    public Entry(float minHeight, Color color) {
      this.minHeight = minHeight;
      this.color = color;
    }

    @Override
    public int compareTo(Entry o) {
      if (minHeight < o.minHeight)
        return -1;
      if (minHeight > o.minHeight)
        return 1;
      return 0;
    }

    @Override
    public String toString() {
      return "Entry{" +
        "minHeight=" + minHeight +
        ", color=" + color +
        '}';
    }
  }

  public static void main(String[] args) throws IOException, URISyntaxException {
    ColourTable colourTable = fromFile("gmt_globe.txt");
    System.out.println("inter: " + colourTable.interpolateColour(33.33333f));
  }
}
