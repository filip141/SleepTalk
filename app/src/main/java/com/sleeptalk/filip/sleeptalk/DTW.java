package com.example.mateusz.sleeptalk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mateusz on 2016-05-25.
 */
public class DTW {
    private List<List<Double>> mfccCoeffs;
    public DTW(List<List<Double>> mfccCoeffs) {
        this.mfccCoeffs=mfccCoeffs;
    }
    public double[][] computeMatrix(List<List<Double>> mfccCoeffsTemp) //compute cumulated matrix
    {
        int Ns = mfccCoeffs.size();
        int Nw = mfccCoeffsTemp.size();
        int coeffs  = mfccCoeffsTemp.get(0).size();
        int Q=(int) Math.round(0.2*Math.max(Ns,Nw));
        double tg = (Nw-Q)/(Ns-Q);
        List<Integer> upLimit = new ArrayList<>();   // up Limitation indices
        List<Integer> downLimit = new ArrayList<>(); // down Limitation indices
        double [][] distanceMatrix = new double[Ns][Nw];
        double [][] cumulatedMatrix;

        for(int i =0;i<Ns;i++)
        {
            int down =(int) Math.max(0,Math.floor(tg*i-Q*tg));
            int up = (int) Math.min(Nw,Math.ceil(tg*i+Q));
            upLimit.add(up);
            downLimit.add(down);
            for(int j=0;j<Ns;j++)
            //for(int j=down;j<up;j++)
            {
                double dist_eukl=0;
                for(int k =0;k<coeffs;k++)
                {
                    double temp=mfccCoeffs.get(i).get(k)-mfccCoeffsTemp.get(j).get(k);
                    dist_eukl += temp*temp;
                }
                distanceMatrix[i][j]=Math.sqrt(dist_eukl);
            }
        }
        /*cumulatedMatrix=Arrays.copyOf(distanceMatrix,distanceMatrix.length);

        for(int i=1;i<Ns;i++)
        {
            cumulatedMatrix[i][0]=cumulatedMatrix[i-1][0]+distanceMatrix[i][0];
        }
        for(int i=1;i<Nw;i++)
        {
            cumulatedMatrix[0][i]=cumulatedMatrix[0][i-1]+distanceMatrix[0][i];
        }


        for(int i=1;i<Ns;i++)
        {
            for(int j=Math.max(downLimit.get(i),1);j<upLimit.get(i);j++)
            {
                List<Double> temp = new ArrayList<>();
                double dd = distanceMatrix[i][j];
                temp.add(cumulatedMatrix[i-1][j]+dd);
                temp.add(cumulatedMatrix[i-1][j-1]+2*dd);
                temp.add(cumulatedMatrix[i][j-1]+dd);
                cumulatedMatrix[i][j]= Collections.min(temp);
            }
        }*/

        return distanceMatrix;
    }
}
