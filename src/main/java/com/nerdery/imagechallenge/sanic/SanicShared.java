package com.nerdery.imagechallenge.sanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class SanicShared {
    private static final Logger LOGGER = LoggerFactory.getLogger(SanicShared.class);

    private Environment environment;
    private int bufferWidth = 800;
    private int bufferHeight = 600;
    private int bufferChannels = 3;
    private int bufferSize;
    private int[] buffer;
    private int poolSize;
    private int radius = 6;
    private int levels = 8;
    private final List<Vertex> radialOffsets = new ArrayList<>();
    private int rowCount = 64;

    public SanicShared(final Environment environment) {
        this.environment = environment;
        bufferWidth = getInt("sanic.buffer.width");
        bufferHeight = getInt("sanic.buffer.height");
        bufferSize = bufferWidth * bufferHeight * bufferChannels;

        LOGGER.info("Initializing Sanic buffer with {} width, {} height, and {} color channels", bufferWidth, bufferHeight, bufferChannels);
        buffer = new int[bufferWidth * bufferHeight * bufferChannels];

        poolSize = getInt("sanic.pool.size");

        radius = getInt("sanic.filter.radius");
        levels = getInt("sanic.filter.levels");
        calculateRadialOffsets();
        LOGGER.info("Initializing Sanic filter with radius {} and {} levels. Filter using {} radial offsets.", radius, levels, radialOffsets.size());

        rowCount = getInt("sanic.filter.row.count");
    }

    protected int getInt(final String property) {
        return environment.getProperty(property, Integer.class);
    }

    private void calculateRadialOffsets() {
        radialOffsets.clear();
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                final Vertex v = new Vertex(x, y);
                if (v.length() <= radius) {
                    radialOffsets.add(v);
                }
            }
        }
    }

    public int getBufferWidth() {
        return bufferWidth;
    }

    public void setBufferWidth(int bufferWidth) {
        this.bufferWidth = bufferWidth;
    }

    public int getBufferHeight() {
        return bufferHeight;
    }

    public void setBufferHeight(int bufferHeight) {
        this.bufferHeight = bufferHeight;
    }

    public int getBufferChannels() {
        return bufferChannels;
    }

    public void setBufferChannels(int bufferChannels) {
        this.bufferChannels = bufferChannels;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int[] getBuffer() {
        return buffer;
    }

    public void setBuffer(int[] buffer) {
        this.buffer = buffer;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public List<Vertex> getRadialOffsets() {
        return radialOffsets;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
}
