package com.nerdery.imagechallenge.sanic;

import java.awt.image.Raster;

public class SanicChunk {
    private int sourceWidth;
    private int sourceHeight;
    private int beginRowInclusive;
    private int endRowInclusive;
    private int beginBufferInclusive;
    private int endBufferInclusive;
    private Raster raster;

    public int getSourceWidth() {
        return sourceWidth;
    }

    public void setSourceWidth(int sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public int getBeginRowInclusive() {
        return beginRowInclusive;
    }

    public void setBeginRowInclusive(int beginRowInclusive) {
        this.beginRowInclusive = beginRowInclusive;
    }

    public int getEndRowInclusive() {
        return endRowInclusive;
    }

    public void setEndRowInclusive(int endRowInclusive) {
        this.endRowInclusive = endRowInclusive;
    }

    public int getBeginBufferInclusive() {
        return beginBufferInclusive;
    }

    public void setBeginBufferInclusive(int beginBufferInclusive) {
        this.beginBufferInclusive = beginBufferInclusive;
    }

    public int getEndBufferInclusive() {
        return endBufferInclusive;
    }

    public void setEndBufferInclusive(int endBufferInclusive) {
        this.endBufferInclusive = endBufferInclusive;
    }

    public Raster getRaster() {
        return raster;
    }

    public void setRaster(Raster raster) {
        this.raster = raster;
    }
}
