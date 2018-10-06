package com.bit0123.feature;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FASTFeatureExtractor {

    private static boolean NonMaxSuppression = false;
    private static int THRESHOLD = 10;
    private static int N = 100;
    private static FASTFeatureExtractor extractor = null;

    private FASTFeatureExtractor(){};

    public static FASTFeatureExtractor create()
    {

        if(extractor == null)
        {
            extractor = new FASTFeatureExtractor();
        }

        return extractor;
    }

    public List<FeaturePoint> detect(int[] pixels, int width, int height)
    {
        return detect(pixels, width, height, THRESHOLD, N);
    }

    public List<FeaturePoint> detect(int[] pixels, int width, int height, int thr)
    {
        return detect(pixels, width, height, thr, N);
    }

    public List<FeaturePoint> detect(Bitmap img, int width, int height, int thr, int n)
    {
        int[] pixels = new int[width*height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);

        int length = width*height;
        for(int i =0; i < length; i++)
        {

        }

        return detect(pixels, width, height, thr, n);
    }
    public List<FeaturePoint> detect(int[] pixels, int width, int height, int thr, int n)
    {
        THRESHOLD = thr;
        N = n;
        NonMaxSuppression = true;

        int[] nPos = getNeighborsOnCircle(width);

        ArrayList<FeaturePoint> corners = new ArrayList<FeaturePoint>();

        for(int i = 3; i < height - 3; i++) {
            int pos = i * width + 3;
            for (int j = 3; j < width - 3; j++) {

                int centerPixel = pixels[pos];
                int[] neighborPixels = new int[16];
                for (int x = 0; x < 16; x++)
                {
                    neighborPixels[x] = pixels[pos + nPos[x]];
                }

                int[] gradPixels = new int[9];
                int pPos = pos - width, pIndx = 0;
                for (int x = -1; x <= 1; x++)
                {
                    gradPixels[pIndx++] = pixels[pPos - 1];
                    gradPixels[pIndx++] = pixels[pPos];
                    gradPixels[pIndx++] = pixels[pPos + 1];
                    pPos += width;
                }

                if(!isExcluded(neighborPixels, centerPixel, THRESHOLD)) // Check whether the pixel is trivially excluded to check if its a corner by checking only top, lright, bottom, eft neighbor pixels
                {
                    if( isCorner(neighborPixels, centerPixel, THRESHOLD) ) // Now check if it's really a corner by checking the all remaining neighbor pixels
                    {
                        j += 3;

                        int[] grad = getGradient(gradPixels);

                        if(NonMaxSuppression)
                            corners.add(new FeaturePoint(j, i, grad[0], grad[1], cornerScore(neighborPixels, centerPixel)));
                        else
                            corners.add(new FeaturePoint(j, i, grad[0], grad[1]));
                    }
                }

                pos++;
            }
        }

        int nCorners = corners.size();
        Collections.sort(corners, Collections.reverseOrder());

        return corners.subList(0, (N < nCorners)? N : nCorners);
    }

    private int[] getGradient(int[] pixels3x3)
    {
        int[] grad = {0, -1};

        float gy = (pixels3x3[0] + pixels3x3[1] + pixels3x3[2] - pixels3x3[6] - pixels3x3[7] - pixels3x3[8])/3;
        float gx = (pixels3x3[0] + pixels3x3[3] + pixels3x3[6] - pixels3x3[2] - pixels3x3[5] - pixels3x3[8])/3;

        grad[0] = (int)(gx + gy);

        double theta_rad = Math.atan2(gy,gx);
        double theta_deg = (theta_rad/Math.PI*180);
        grad[1] =  quantizeAngle( Math.round(theta_deg  + (theta_rad >= 0 ? 0 : 360)));

        return grad;
    }

    public static int quantizeAngle(double angle)
    {
        int quant = -1;

        if(angle >= 0 && angle <=360)
        {
            if(angle < 22.5 || angle > 337.5) return 0;
            else if(angle < 67.5) return 1;
            else if(angle < 112.5) return 2;
            else if(angle < 157.5) return 3;
            else if(angle < 202.5) return 4;
            else if(angle < 247.5) return 5;
            else if(angle < 292.5) return 6;
            else if(angle < 337.5) return 7;

//            if(angle < 45) return 0;
//            else if(angle < 90) return 1;
//            else if(angle < 135) return 2;
//            else if(angle < 180) return 3;
//            else if(angle < 225) return 4;
//            else if(angle < 270) return 5;
//            else if(angle < 315) return 6;
//            else if(angle < 360) return 7;
        }

        Log.d("ANGLE", "angle: " + angle + " QuantAngle: " + quant);
        return quant;
    }

    private int cornerScore(int[] neighborPixels, int centerPixel)
    {
        int thrMin = 0;
        int thrMax = 255;
        int thr = (thrMax + thrMin)/2;

        while (true)
        {
            if (isCorner(neighborPixels, centerPixel, thr)) {
                thrMin = thr;
            } else {
                thrMax = thr;
            }

            if (thrMin == thrMax - 1 || thrMin == thrMax) {
                return thrMin;
            }

            thr = (thrMin + thrMax) / 2;
        }
    }

    private boolean isExcluded(int[] neighborPixels, int centerPixel, int thr)
    {
        int similarityCount = 0;

        for(int p = 0; p< 16; p+=4)
        {
            if(isBrighter(centerPixel, neighborPixels[p], thr))
                similarityCount++;
        }

        if(similarityCount < 3)
        {
            similarityCount = 0;
            for(int p = 0; p< 16; p+=4)
                if(isDarker(centerPixel, neighborPixels[p], thr))
                    similarityCount++;

            if(similarityCount < 3)
                return false; // Not a corner
        }

        return true;
    }

    private boolean isCorner(int[] neighborPixels, int centerPixel, int thr)
    {

        for (int x = 0; x < 16; x++) {
            boolean darker = true;
            boolean brighter = true;

            for (int y = 0; y < 9; y++) {
                int curNeighborPixel = neighborPixels[(x + y) & 15];

                if (!this.isBrighter(centerPixel, curNeighborPixel, thr)) {
                    brighter = false;
                    if (darker == false) {
                        break;
                    }
                }

                if (!this.isDarker(centerPixel, curNeighborPixel, thr)) {
                    darker = false;
                    if (brighter == false) {
                        break;
                    }
                }
            }

            if (brighter || darker) {
               return true;
            }
        }

        return false;
    }

    private boolean isBrighter(int center, int neighbor, int thr)
    {
        return (center + thr) < neighbor;
    }

    private boolean isDarker(int center, int neighbor, int thr)
    {
        return (center - thr) > neighbor;
    }

    private int[] getNeighborsOnCircle(int width)
    {
        int neighborPos[] = new int[16];

        neighborPos[0] = - width - width - width;
        neighborPos[1] = neighborPos[0] + 1;
        neighborPos[2] = neighborPos[1] + width + 1;
        neighborPos[3] = neighborPos[2] + width + 1;
        neighborPos[4] = neighborPos[3] + width;
        neighborPos[5] = neighborPos[4] + width;
        neighborPos[6] = neighborPos[5] + width - 1;
        neighborPos[7] = neighborPos[6] + width - 1;
        neighborPos[8] = neighborPos[7] - 1;
        neighborPos[9] = neighborPos[8] - 1;
        neighborPos[10] = neighborPos[9] - width - 1;
        neighborPos[11] = neighborPos[10] - width - 1;
        neighborPos[12] = neighborPos[11] - width;
        neighborPos[13] = neighborPos[12] - width;
        neighborPos[14] = neighborPos[13] - width + 1;
        neighborPos[15] = neighborPos[14] - width + 1;

        return neighborPos;
    }


}
