package com.bit0123.feature;

public class FeaturePoint implements Comparable<FeaturePoint>{

    private int x = 0;
    private int y = 0;
    private int angle = -1;
    private int gradMag = 0;
    private int score = 0;

    public FeaturePoint(int x, int y, int gm, int angle)
    {
        this.x = x;
        this.y = y;
        this.gradMag = gm;
        this.angle = angle;
    }

    public FeaturePoint(int x, int y, int gm, int angle, int score)
    {

        this.x = x;
        this.y = y;
        this.gradMag = gm;
        this.angle = angle;
        this.score = score;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getGradMag()
    {
        return gradMag;
    }

    public int getAngle()
    {
        return angle;
    }

    public int getScore()
    {
        return score;
    }

    @Override
    public int compareTo(FeaturePoint fc)
    {
        if (fc.score == this.score) return 0;
        else if (fc.score < this.score) return 1;
        else return -1;
    }

}
