package com.sleeptalk.filip.sleeptalk;

import java.util.ArrayList;
import java.util.List;

/**
 * FrameDivider zawiera metody służące do podziału sygnału na zbiór ramek.
 * Możemy przyjąć, że każda ramka posiada stacjonarne widmo.
 * Ramki mają określony czas trwania(frameTime) oraz czas nakładkowania timeOverlap.
 * Created by filip on 03.05.16.
 */
public class FrameDivider {

    private List<Double> signal;
    private int sampleRate;

    /**
     * Konstruktor klasy FrameDivider.
     * @param signal Sygnał, który będzie dzielony na ramki.
     * @param sampleRate Częstotliwość próbkowania powyższego sygnału.
     */
    public FrameDivider(List<Double> signal, int sampleRate){
        this.signal = signal;
        this.sampleRate = sampleRate;
    }

    /**
     * Metoda dopisuje zera do sygnału, aby umożliwić jego prawidłowy podział na ramki.
     * @param zerosNumber Liczba zer do dopisania
     */
    private void appendZeros(int zerosNumber){
        for(int i = 0; i < zerosNumber; i++){
            signal.add(0.0);
        }
    }

    /**
     * Metoda służy do dzielenia sygnału na ramki.
     * @param frameTime Czas trwania okna.
     * @param timeOverlap Czas o jaki nakładają się dwie ramki.
     * @return Ramki sygnału.
     */
    public List<List<Double>> divide(double frameTime, double timeOverlap){
        int startFrame;
        int endFrame;
        int signalSize = signal.size();
        int frameSamples = (int) (frameTime*sampleRate);
        int overlapSamples = (int) (timeOverlap*sampleRate);
        int stepNumber = signalSize / (frameSamples - overlapSamples);

        List<Double> frame = new ArrayList<>();
        List<List<Double>> framesBuffer = new ArrayList<>();

        // Add zeros to signal
        appendZeros(frameSamples);
        for(int counter = 0; counter < stepNumber; counter++){
            startFrame = counter * (frameSamples - overlapSamples);
            endFrame = startFrame + frameSamples;
            frame = signal.subList(startFrame, endFrame);
            framesBuffer.add(frame);
        }
        return framesBuffer;
    }
}
