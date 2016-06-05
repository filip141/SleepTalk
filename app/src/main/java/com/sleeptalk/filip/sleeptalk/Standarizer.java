package com.sleeptalk.filip.sleeptalk;

import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Klasa służąca do standaryzacji sygnału wejściowego.
 * Klasa wykorzystywana do przeprowadzenia zabiegu standaryzacji sygnału, umożliwia wykonanie czynności które mają na celu zniwelowanie różnic między sygnałami pochodzącymi z różnych źródeł oraz usunięcie redunantnej informacji.
 * Created by filip on 01.05.16.
 */
public class Standarizer {

    List<Double> signal;
    int sampleRate;
    public Double[] highPassCoeffs = new Double[]{ -0.001945207254063, -0.00411960524828,-0.009835398951842, -0.01695068201474,
            -0.02273603655938,   0.9731072889955, -0.02273603655938, -0.01695068201474,
            -0.009835398951842, -0.00411960524828,-0.001945207254063 };

    /**
     * Konstruktor klasy Standarizer
     * @param signal Sygnał wejściowy
     * @param sampleRate Częstotliwość próbkowania syngału.
     */
    public Standarizer(List<Double> signal, int sampleRate){
        this.signal = signal;
        this.sampleRate = sampleRate;
    }

    /**
     * Metoda odrzuca co simpleRate/newFreq próbkę sygnału.
     * @param newFreq Nowa częstotliwość próbkowania.
     */
    public void decimate(int newFreq){
        int step;
        List<Double> decimatedArray = new ArrayList<>();

        // New frequency should be lower then Signal Sample Rate
        if(newFreq <= sampleRate){
            // New frequency and old sample rate should be divisible
            if(sampleRate % newFreq == 0){
                step = sampleRate / newFreq;
                for(int i=0; i < signal.size(); i=i+step){
                    decimatedArray.add(signal.get(i));
                }
                signal = decimatedArray;
                sampleRate = newFreq;
            }
            else{
                throw new IllegalArgumentException("New Frequency and Sample Rate should be divisible");
            }
        }
        else{
            throw new IllegalArgumentException("Passed Sample Rate should be greater");
        }
    }

    /**
     * Metoda usuwa składową stałą z sygnału i dzieli go przez wartość maksymalną.
     */
    public void standard(){
        // Get maximum value
        double maxValue = Statistics.max(signal);
        // Get average
        double average = Statistics.mean(signal);
        // Modify each element
        for(int i=0; i < signal.size(); i++){
            signal.set(i, (signal.get(i) - average)/maxValue);
        }
    }

    // Preemphasis filtration
    /**
     * Filtracja preemfazowa.
     */
    public void preemphasis(){
        lfilter(Arrays.asList(new Double[]{1.0, -0.9735}));
    }

    /**
     * Metoda implementuje filtr OOI
     * @param coeffs Wspołczynniki filtru
     */
    public void lfilter(List<Double> coeffs){
        double signalSample;
        double coefSample;
        int convSize = coeffs.size() + signal.size() - 1;
        double[] filtredSig = new double[convSize];
        List<Double> outputSignal = new ArrayList<>();

        // Filter loop
        for(int n = 0; n < convSize; n++){
            filtredSig[n] = 0;
            for(int m = 0; m < coeffs.size(); m++){
                if((n - m >= 0) && ((n - m) < signal.size())){
                    signalSample = signal.get(n - m);
                    coefSample = coeffs.get(m);
                    filtredSig[n] += signalSample * coefSample;
                }
            }
        }
        // Rewrite to new list
        for(double num: filtredSig){
            outputSignal.add(num);
        }
        signal = outputSignal;
    }

    /**
     * Metoda zwraca sygnał po operacji standaryzacji.
     * @return Sygnał po procesie standaryzacji.
     */
    public Pair<List<Double>,Integer> getSignal(){
        return new Pair<List<Double>,Integer>(signal, sampleRate);
    }

}
