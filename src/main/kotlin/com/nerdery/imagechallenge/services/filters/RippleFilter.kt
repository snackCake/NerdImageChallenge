package com.nerdery.imagechallenge.services.filters

import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.lang.Math.*

/**
 * Applies a ripple displacement effect to the image.
 *
 * @author Justin Mullin
 */
@Component
open class RippleFilter : ImageFilter {
    override fun getName() = "Ripple"

    companion object {
        // The higher the frequency, the tighter the ripples are together
        val Frequency = 5.5

        // The higher the power, the more each ripple displaces the image
        val Power = 70.0
    }

    override fun transform(sourceImage: BufferedImage): BufferedImage {
        // Grab the source and destination data for processing
        val destinationImage = BufferedImage(sourceImage.width, sourceImage.height, BufferedImage.TYPE_3BYTE_BGR)
        val sourceData = getImageData(sourceImage)
        val destinationData = getImageData(destinationImage)

        // Loop over each pixel to figure out its displaced value
        for(x in 0 until sourceImage.width) {
            for(y in 0 until sourceImage.height) {
                val destinationIndex = coordToIndex(x, y, sourceImage.width)

                // Calculate the offset of this pixel from the center of the image
                val xDifference = x - sourceImage.width/2.0
                val yDifference = y - sourceImage.height/2.0
                val distance = sqrt(pow(xDifference, 2.0) + pow(yDifference, 2.0))

                // Harness the power of trigonometry to figure out the ripple amount at this pixel
                val displacementAmount = (Power + sin(distance * Frequency/100.0) * Power).toInt()

                // Calculate a vector pointed out from the image center with a magnitude of the displacement amount
                val displacementX = (-xDifference / distance) * displacementAmount
                val displacementY = (-yDifference / distance) * displacementAmount

                // Calculate the displaced pixel position
                val sourceIndex = coordToIndex(x + displacementX.toInt(), y + displacementY.toInt(), sourceImage.width)

                // Write the offset pixel (taken from sourceIndex) to the current pixel (at destinationIndex)
                destinationData[destinationIndex] = sourceData.getOrElse(sourceIndex, { 0.toByte() })
                destinationData[destinationIndex+1] = sourceData.getOrElse(sourceIndex+1, { 0.toByte() })
                destinationData[destinationIndex+2] = sourceData.getOrElse(sourceIndex+2, { 0.toByte() })
            }
        }

        return destinationImage
    }

    fun coordToIndex(x: Int, y: Int, width: Int) = (x + y * width) * 3
    fun getImageData(image: BufferedImage): ByteArray = (image.raster.dataBuffer as DataBufferByte).data
}