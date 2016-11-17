package com.nerdery.imagechallenge.services.filters

import java.awt.image.BufferedImage

/**
 * A Kotlin-based extension of [ImageFilter] that sanitizes the types (removes nullability) and adds a default
 * implementation for [getName].
 *
 * @author Ryan Evans (revans@nerdery.com)
 */
interface KImageFilter : ImageFilter {

    override fun getName(): String = javaClass.simpleName

    override fun transform(sourceImage: BufferedImage): BufferedImage
}