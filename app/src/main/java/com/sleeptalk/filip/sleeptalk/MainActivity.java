package com.sleeptalk.filip.sleeptalk;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FileSaver datafile;
    private WordLibrary lib;
    private String fileName;
    private MediaPlayer m ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android objects initialization
        final Button recButton = (Button) findViewById(R.id.rec_button);
        startButtonAnimation(recButton);
        datafile = new FileSaver(this);
        try {
            lib = datafile.getListFromJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Initialize media types
        m=MediaPlayer.create(MainActivity.this,R.raw.ding);
        final WavRecord wv = new WavRecord();

        if (recButton != null) {
            recButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recButton.setEnabled(false);
                    m.start();
                    Handler handlerStart = new Handler();
                    handlerStart.postDelayed(new Runnable() {
                        public void run() {
                            wv.startRecording();
                        }
                    }, 1100);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Process input
                            fileName = wv.stopRecording();
                            WavFile file = new WavFile(fileName);
                            try{
                                List<List<Double>> mfccCoefs = mfccComputing(file);
                                String result = findWord(lib, mfccCoefs);
                            }
                            catch(IllegalArgumentException e){

                            }
                            wv.deleteFile();
                            recButton.setEnabled(true);
                        }
                    }, 2800);

                }
            });
        }

    }



    public void startButtonAnimation(Button button) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(2000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        button.startAnimation(animation);

    }

    public List<List<Double>> mfccComputing(WavFile file)
    {
        List<Double> wavBuffer = file.read();
        Standarizer signalStandarizer = new Standarizer(wavBuffer, file.sampleRate);
        signalStandarizer.decimate(16000);
        signalStandarizer.standard();
        signalStandarizer.lfilter(Arrays.asList(signalStandarizer.highPassCoeffs));
        signalStandarizer.preemphasis();
        Pair<List<Double>,Integer> signalParams = signalStandarizer.getSignal();
        List<Double> stdSignal = signalParams.first;
        int sampleRate = signalParams.second;
        Mfcc mfcc = new Mfcc(stdSignal, sampleRate);
        if(mfcc.isSignalNull()){
            throw new IllegalArgumentException();
        }
        return mfcc.compute();
    }

    public String findWord(WordLibrary lib, List<List<Double>> mfccCoefs){
        double dynamicResult;
        double finalResult;
        DTW dynamicCompare = new DTW(mfccCoefs);
        HashSet<String> keys = lib.keys();
        List<List<List<Double>>> mfccList;
        List<Double> finalList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        for(String key: keys){
            mfccList = lib.get(key);
            for(List<List<Double>> mfccRel : mfccList){
                dynamicResult = dynamicCompare.computeMatrix(mfccRel);
                finalList.add(dynamicResult);
                keyList.add(key);
            }
        }
        finalResult = Collections.min(finalList);
        return keyList.get(finalList.indexOf(finalResult));
    }

}
