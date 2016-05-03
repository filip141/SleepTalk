package com.sleeptalk.filip.sleeptalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FileSaver datafile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android objects initialization
        Button recButton = (Button)findViewById(R.id.rec_button);
        datafile = new FileSaver(this);
//        WavFile file = new WavFile("/sdcard/sine.wav");
        WavFile file = new WavFile("/sdcard/Music/FB_MAT_1.wav");

        List<Double> wavBuffer = file.read();
        Standarizer signalStandarizer = new Standarizer(wavBuffer, file.sampleRate);
        signalStandarizer.decimate(16000);
        signalStandarizer.standard();
        signalStandarizer.lfilter(Arrays.asList(signalStandarizer.highPassCoeffs));
        signalStandarizer.preemphasis();
        Pair<List<Double>,Integer> signalParams = signalStandarizer.getSignal();
        List<Double> stdSignal = signalParams.first;
        int sampleRate = signalParams.second;
        VoiceDetector vad = new VoiceDetector(stdSignal, sampleRate);

//        for(int i = 0; i < 256; i++){
//            signal.add(0.0);
//        }
//        signal.set(128, 1.0);


        try {
            datafile.save(stdSignal);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // OnClick listeners
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Hello");
            }
        });
    }
}
