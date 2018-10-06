package com.bit0123.feature;

public class KirschCompasMask {

    public static int[][][] Kirsch_mask = {
            /**
             * Kirsch compass edge detection mask: East.
            */
            {
                    {-3, -3, -3},
                    {-3, 0, -3},
                    {5, 5, 5}
            },

            /**
             * Kirsch compass edge detection mask: Northeast.
            */
            {
                    {-3, -3, -3},
                    {-3, 0, 5},
                    {-3, 5, 5}
            },
            /**
             * Kirsch compass edge detection mask: North.
            */
            {
            {-3, -3, 5},
            {-3, 0, 5},
            {-3, -3, 5}
            },

            /**
             * Kirsch compass edge detection mask: Northwest.
             */
            {
            {-3, 5, 5},
            {-3, 0, 5},
            {-3, -3, -3}
            },

            /**
             * Kirsch compass edge detection mask: West.
             */
            {
            {5, 5, 5},
            {-3, 0, -3},
            {-3, -3, -3}
            },

            /**
             * Kirsch compass edge detection mask: Southwest.
             */
            {
            {5, 5, -3},
            {5, 0, -3},
            {-3, -3, -3}
            },

            /**
             * Kirsch compass edge detection mask: South.
             */
            {
            {5, -3, -3},
            {5, 0, -3},
            {5, -3, -3}
            },

            /**
             * Kirsch compass edge detection mask: Southeast.
             */
            {
            {-3, -3, -3},
            {5, 0, -3},
            {5, 5, -3}
            }
    };


    public static int[] getResponse(int[][] pixels3x3)
    {
        int[] response = new int[8];

        for(int maskIndx=0; maskIndx < 8; maskIndx++)
        for(int i=0; i < 3; i++)
            for(int j=0; j < 3; j++)
            {
                response[maskIndx] += pixels3x3[i][j] * Kirsch_mask[maskIndx][i][j];
            }

        return response;
    }

}
