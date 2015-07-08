package com.nerdery.imagechallenge.services.filters

import java.awt.image.BufferedImage
import java.awt.{Color, Rectangle}

import org.springframework.stereotype.Component

/**
 * @author Justin Mullin
 *
 * Applies the classic "oil paint" effect, making the image appear to have been
 * painted using oil-based paints.
 */
@Component
class OilPaintFilter extends ImageFilter {
  /**
   * Play with these values to alter the effect.
   *
   * A larger radius gives the appearance of thicker brush strokes, while more
   * levels gives a more granular look to the image.
   */
  val Radius = 6
  val Levels = 8

  override def getName: String = "OilPaint"

  override def transform(source: BufferedImage): BufferedImage = {
    val (width, height) = (source.getWidth, source.getHeight)

    // If there's an alpha channel in the source image, we'll need to adjust our read offset
    val hasAlphaChannel = source.getColorModel.hasAlpha
    val colorComponents = if(hasAlphaChannel) 4 else 3

    // Grab an array of pixel data for reading; this is a little faster than using .getRGB, unfortunately
    val sourcePixels = source.getData.getPixels(0, 0, width, height, new Array[Int](width*height*colorComponents))

    val bounds = new Rectangle(0, 0, width, height)

    // Destination array of pixels for writing our resulting image
    val resultPixels = new Array[Int](width*height*colorComponents)

    // Loop over the image in a parallel fashion (gotta use those cores!)
    for(x <- (0 until width).par; y <- (0 until height).par) {
      // Gather the pixel colors within one radius of here
      val colorNeighborhood =
        for(dX <- -Radius to Radius;
            dY <- -Radius to Radius
            if length(dX, dY) <= Radius &&
            bounds.contains(x+dX, y+dY)) yield {

          getPixelColor(sourcePixels, width, colorComponents, x+dX, y+dY)
        }

      // Separate nearby pixels into buckets by color intensity
      val intensityBuckets = colorNeighborhood.groupBy(calculateIntensity)

      // Grab the bucket with the most associated pixels
      val (_, pixels) = intensityBuckets.maxBy(_._2.size)

      // Find an average color for this bucket
      val averageColor = new Color(
        pixels.map(_.getRed).sum/pixels.size,
        pixels.map(_.getGreen).sum/pixels.size,
        pixels.map(_.getBlue).sum/pixels.size)

      // Write the pixel
      setPixelColor(resultPixels, width, colorComponents, x, y, averageColor)
    }

    // Copy the destination array into an image for display
    val result = new BufferedImage(width, height, source.getType)
    result.getRaster.setPixels(0, 0, width, height, resultPixels)

    result
  }

  /**
   * Given a source array and information on its layout, read the color from a
   * given pixel location as a color.
   */
  def getPixelColor(source: Array[Int], width: Int, colorComponents: Int, x: Int, y: Int) = {
    val offset = imageOffset(width, colorComponents, x, y)
    new Color(source(offset), source(offset+1), source(offset+2))
  }

  /**
   * Given a destination array and information on its layout, set a given pixel location
   * to the specified color.
   */
  def setPixelColor(dest: Array[Int], width: Int, colorComponents: Int, x: Int, y: Int, color: Color) = {
    val offset = imageOffset(width, colorComponents, x, y)
    dest.update(offset, color.getRed)
    dest.update(offset+1, color.getGreen)
    dest.update(offset+2, color.getBlue)
  }

  /**
   * Given the width of an image and the number of components to each pixel color, returns
   * the offset into a pixel array for a given pixel location.
   */
  def imageOffset(width: Int, runSize: Int, x: Int, y: Int): Int = {
    (y * width + x) * runSize
  }

  /**
   * Calculates the intensity level for a given color, in the range of from 0-[Levels]
   */
  def calculateIntensity(color: Color) = (((color.getRed + color.getGreen + color.getBlue) / 3) * Levels) / 255

  /**
   * Calculates the length of a vector given the x and y components
   */
  def length(x: Int, y: Int) = math.sqrt(math.pow(x, 2)+math.pow(y, 2))
}