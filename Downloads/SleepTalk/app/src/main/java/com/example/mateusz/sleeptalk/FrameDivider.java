package com.example.mateusz.sleeptalk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 03.05.16.
 * PL: FrameDivider zawiera metody służące
 * do podziału sygnału na zbiór ramek.
 * Możemy przyjąć, że każda ramka posiada stacjonarne
 * widmo. Ramki mają określony czas trwania(frameTime)
 * oraz czas nakładkowania timeOverlap.
 *
 */
public class FrameDivider {

    private List<Double> signal;
    private int sampleRate;

    public FrameDivider(List<Double> signal, int sampleRate){
        this.signal = signal;
        this.sampleRate = sampleRate;
    }

    private void appendZeros(int zerosNumber){
        for(int i = 0; i < zerosNumber; i++){
            signal.add(0.0);
        }
    }

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
