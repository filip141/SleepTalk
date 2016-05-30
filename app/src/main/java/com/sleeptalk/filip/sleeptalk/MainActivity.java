package com.sleeptalk.filip.sleeptalk;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Handler;
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
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private FileSaver datafile;
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android objects initialization
        Button recButton = (Button) findViewById(R.id.rec_button);
        startButtonAnimation(recButton);
        datafile = new FileSaver(this);
//        WavFile file = new WavFile("/sdcard/square.wav");
        WavFile file = new WavFile("/sdcard/Music/FB_MAT_1.wav");
        List<List<Double>> mfccCoefs = mfccComputing(file);

        final WavRecord wv = new WavRecord();
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.startRecording();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        String fileName = wv.stopRecording();
                    }
                }, 3000);
            }
        });

        try {
            datafile.save3D(mfccCoefs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startButtonAnimation(Button button) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(4000); // duration - half a second
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
        return mfcc.compute();
    }
}
