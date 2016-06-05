package com.sleeptalk.filip.sleeptalk;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa służąca do ekstrakcji współczynników mel-cepstralnych.
 * Klasa służąca do ekstrakcji cech wykorzystywanych w dalszej części przetwarzania.
 * Zawiera ona metody takie jak hertz2Mel bądz mel2Hertz służące to konwersji ze skali melowej do skali w Hertzach, jak i również metody do tworzenia banku filtrów melowych oraz obliczania współczynników mel-cepstralnych.
 * Created by filip on 28.04.16.
 */
public class Mfcc{

    // Primitive types
    private int filters;
    private int sampleRate;
    private double highFilterBankFreq;
    private double lowFilterBankFreq;

    private List<Double> signal;
    private List<List<Double>> framesBuffer;
    private List<List<ComplexNumber>> framesPSDBuffer;
    private static int defaultFilters = 26;
    private static int numberOfMelCoeffs = 12;

    /**
     * Konstruktor klasy Mfcc.
     * @param signal Sygnał dla którego będą wyliczane współczynniki Mfcc.
     * @param sampleRate Częstotliwość próbkowania powyższego sygnału.
     */
    public Mfcc(List<Double> signal, int sampleRate){
        this(signal, sampleRate, defaultFilters);
    }

    /**
     * Konstruktor z dodatowym parametrem filters.
     * @param signal Sygnał dla którego będą wyliczane współczynniki Mfcc.
     * @param sampleRate Częstotliwość próbkowania powyższego sygnału.
     * @param filters Ilość filtrów trójkątnych.
     */
    public Mfcc(List<Double> signal, int sampleRate, int filters){
        VoiceDetector vad = new VoiceDetector(signal, sampleRate);
        this.signal = vad.removeSilence();
        this.filters = filters;
        this.sampleRate = sampleRate;
        framesPSDBuffer = vad.framesPSDBuffer;
        framesBuffer = vad.framesBuffer;
        lowFilterBankFreq = 300;
        highFilterBankFreq = sampleRate / 2;
    }


    /**
     * Metoda sprawdza czy sygnał nie jest pusty.
     * @return True, jeśli sygnał jest pusty lub false w przeciwnym wypadku.
     */
    public boolean isSignalNull(){
        if(signal == null){
            return true;
        }
        return false;
    }


    /**
     * Metoda zwraca sygnał po sprawdzeniu detektorem aktywności głosowej.
     * @return Sygnał po VAD
     */
    public List<Double> getSignal(){
        return this.signal;
    }


    /**
     * Metoda wylicza współczynniki mel-cepstralne sygnału.
     * @return Wektor współczynników Mfcc.
     */
    public List<List<Double>> compute(){
        double cepstralSum;
        double melFilterBankSum;
        List<Double> melLogs;
        List<Double> framePSD;
        List<Double> melCoeffs;
        List<Double> melScalars;
        List<ComplexNumber> frameFft;
        List<List<Double>> deltaCoeffs;
        List<List<Double>> doubleDeltaCoeffs;
        List<List<Double>> melCoeffsBuffor = new ArrayList<>();
        List<List<Double>> filterBank = melFilterBank(framesPSDBuffer.get(0).size());
        for(List<ComplexNumber> currentPSD: framesPSDBuffer) {
            // Declare new arrays
            melScalars = new ArrayList<>();
            melLogs = new ArrayList<>();
            melCoeffs = new ArrayList<>();
            framePSD = new ArrayList<>();
            frameFft = currentPSD.subList(0, currentPSD.size() / 2);

            // Complex to double conversion, fft absolute value
            for (ComplexNumber cnb: frameFft){
                framePSD.add(Math.pow(ComplexNumber.abs(cnb), 2));
            }
            // for each filter
            for(List<Double> melFilter: filterBank){
                melFilterBankSum = 0;
                // Multiply with filter
                for(int n = 0; n < melFilter.size(); n++){
                    melFilterBankSum += melFilter.get(n) * framePSD.get(n);
                }
                melScalars.add(melFilterBankSum);
                melLogs.add(2 * Math.log(melFilterBankSum));
            }

            // Discrete Cosine Transform
            for(int j = 0; j < numberOfMelCoeffs; j++){
                cepstralSum = 0;
                for(int i = 0; i < filters; i++){
                    cepstralSum = cepstralSum + melLogs.get(i) *
                            Math.cos((Math.PI * j / filters) * (i - 0.5));
                }
                melCoeffs.add(cepstralSum * Math.sqrt(2.0 / filters));
            }
            // Add frame energy as 13 coeff
            melCoeffs.add((1.0 / framePSD.size())*Statistics.sum(framePSD));
            melCoeffsBuffor.add(melCoeffs);
        }
        deltaCoeffs = findDelta(melCoeffsBuffor);
        melCoeffsBuffor = addVertically(melCoeffsBuffor, deltaCoeffs);
        doubleDeltaCoeffs = findDelta(deltaCoeffs);
        melCoeffsBuffor = addVertically(melCoeffsBuffor, doubleDeltaCoeffs);
        return melCoeffsBuffor;
    }


    /**
     * Metoda służy do dodawania dwóch macierzy pionowo.
     * @param first Pierwsza macierz.
     * @param second Druga macierz.
     * @return Suma macierzy.
     */
    public List<List<Double>> addVertically(List<List<Double>> first, List<List<Double>> second)
    {
        List<Double> buffer;
        if(first.size() != second.size()){
            throw new IllegalArgumentException("List should have same length !");
        }

        for(int n = 0; n < first.size(); n++){
            buffer = first.get(n);
            buffer.addAll(second.get(n));
            first.set(n, buffer);
        }
        return first;
    }


    /**
     * Obliczanie współczynników Delta.
     * @param mfcc Wektor współczynników Mfcc.
     * @return Wektor współczynników Delta.
     */
    public List<List<Double>> findDelta(List<List<Double>> mfcc){
        List<Double> prevMfccs;
        List<Double> nextMfccs;
        List<Double> currentDeltas;
        List<List<Double>> deltaMfcc = new ArrayList<>();
        for(int n = 0; n < mfcc.size(); n++){
            currentDeltas = new ArrayList<>();
            // If no previous exist take current
            if(n != 0){
                prevMfccs = mfcc.get(n - 1);
            }
            else{
                prevMfccs = mfcc.get(n);
            }

            // If no next exist tak current
            if(n != mfcc.size() - 1){
                nextMfccs = mfcc.get(n + 1);
            }
            else{
                nextMfccs = mfcc.get(n);
            }

            // Calculate delta coeffs
            for(int j = 0; j < nextMfccs.size(); j++){
                currentDeltas.add((nextMfccs.get(j) - prevMfccs.get(j)) / 2.0);
            }
            deltaMfcc.add(currentDeltas);
        }
        return deltaMfcc;
    }


    /**
     * Metoda buduje bank filtrów trójkątnych.
     * @param nfft Rozdzielczość transformaty Fouriera.
     * @param lowFrequency Dolna częstotliwość filtru.
     * @param highFrequency Górna częstotliwość filtru.
     * @return Bank filtrów trójkątnych.
     */
    public List<List<Double>> melFilterBank(int nfft, double lowFrequency, double highFrequency){
        List<Double> melFilter;
        List<List<Double>> melFilterBankBuffer = new ArrayList<>();
        double melFrequencies[] = new double[filters + 2];
        double hzFrequencies[] = new double[filters + 2];
        int frequencySamples[] = new int[filters + 2];
        double lowMelFrequency = hertz2Mel(lowFrequency);
        double highMelFrequency = hertz2Mel(highFrequency);
        double frequencyStep = ((highMelFrequency - lowMelFrequency) / (filters + 1));
        double frequency = lowMelFrequency;
        int psdLength = (nfft - 1) / 2 + 1;
        int counter = 1;

        // Initialize first and last frequencies
        melFrequencies[0] = lowMelFrequency;
        melFrequencies[filters + 1] = highMelFrequency;
        hzFrequencies[0] = mel2Hertz(lowMelFrequency);
        hzFrequencies[filters + 1] = mel2Hertz(highMelFrequency);
        frequencySamples[0] = (int) ((nfft + 1) * hzFrequencies[0] / sampleRate);
        frequencySamples[filters + 1] = (int) ((nfft + 1) * hzFrequencies[filters + 1] / sampleRate);

        // Add points in the middle
        while(frequency < highMelFrequency){
            frequency = frequency + frequencyStep;
            melFrequencies[counter] = frequency;
            hzFrequencies[counter] = mel2Hertz(frequency);
            frequencySamples[counter] = (int) ((nfft + 1) * hzFrequencies[counter] / sampleRate);
            counter++;
        }

        // Build filters
        for(int i = 1; i < filters + 1; i++){
            melFilter = new ArrayList<>();
            for(int j = 1; j < psdLength + 1; j++){
                if(j < frequencySamples[i-1]){
                    melFilter.add(0.0);
                }
                else if(frequencySamples[i-1] <= j && j<= frequencySamples[i]){
                    melFilter.add(((j - frequencySamples[i - 1]) * 1.0
                            / (frequencySamples[i] - frequencySamples[i - 1]) * 1.0));
                }
                else if(frequencySamples[i] <= j && j <= frequencySamples[i + 1]){
                    melFilter.add(((-j + frequencySamples[i+1]) * 1.0
                            /(frequencySamples[i+1] - frequencySamples[i]) * 1.0));
                }
                else{
                    melFilter.add(0.0);
                }
            }
            melFilterBankBuffer.add(melFilter);
        }

        return melFilterBankBuffer;
    }

    /**
     * Buduje bank filtrów trójkątnych.
     * @param psdLength Długość widmowej gęstości mocy.
     * @return Bank filtrów trójkątnych.
     */
    public List<List<Double>> melFilterBank(int psdLength){
        return melFilterBank(psdLength, lowFilterBankFreq, highFilterBankFreq);
    }

    /**
     * Konwertuje częstotliwość w Hz na skalę melową.
     * @param frequencyHertz Częstotliwość w Hz.
     * @return Częstotliwość w skali melowej.
     */
    public static double hertz2Mel(double frequencyHertz){
        return 1125 * Math.log(1 + frequencyHertz / 700.0);
    }

    /**
     * Konwertuje częstotliwość w skali melowej na Hz
     * @param frequencyMel Częstotliwość w skali melowej.
     * @return Częstotliwość w Hz.
     */
    public static double mel2Hertz(double frequencyMel){
        return 700 * (Math.exp(frequencyMel / 1125.0) - 1);
    }

}
