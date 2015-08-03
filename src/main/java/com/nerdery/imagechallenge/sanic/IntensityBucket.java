package com.nerdery.imagechallenge.sanic;

public class IntensityBucket {
    private int[] channels;
    private int size = 0;

    public IntensityBucket() {
        channels = new int[3];
    }

    public int size() {
        return size;
    }

    public void clear() {
        channels[0] = 0;
        channels[1] = 0;
        channels[2] = 0;
        size = 0;
    }

    public void merge(int[] pixel) {
        channels[0] += pixel[0];
        channels[1] += pixel[1];
        channels[2] += pixel[2];
        size++;
    }

    public int averageRed() {
        return channels[0] / size;
    }

    public int averageGreen() {
        return channels[1] / size;
    }

    public int averageBlue() {
        return channels[2] / size;
    }
}
