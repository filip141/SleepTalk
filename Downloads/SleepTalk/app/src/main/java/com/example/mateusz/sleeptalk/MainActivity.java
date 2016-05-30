package com.example.mateusz.sleeptalk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FileSaver datafile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_WRITE_PERMISSIONS = 321;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionReadAdd();
        //final WordRec wordRec= new WordRec();

        // Android objects initialization
        Button recButton = (Button)findViewById(R.id.rec_button);
        //Button animation
        startButtonAnimation(recButton);

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  wordRec.recording();
            }
        });

        datafile = new FileSaver(this);
//        WavFile file = new WavFile("/sdcard/square.wav");
        WavFile file = new WavFile("/storage/1B18-231A/FB_MAT_1.wav");

        List<List<Double>> mfccCoefs = mfccComputing(file-);


        DTW dtw = new DTW(mfccCoefs);
        double[][] dtwmatrix = dtw.computeMatrix(mfccCoefs);
        double result = dtw.getResult(dtwmatrix);

        //Converting array to list for plotting
        List<List<Double>> dtwmatrixlist = new ArrayList<>();
        for(int i=0;i<dtwmatrix.length;i++) {
            List<Double> temp = new ArrayList<>();
            for (int j = 0; j < dtwmatrix[0].length; j++) {
                temp.add(dtwmatrix[i][j]);
            }
            dtwmatrixlist.add(temp);
        }
        /// FFT Tet
//        List<Double> nlist = new ArrayList<>();
//        for(int i = 0 ; i < 128; i++){
//            nlist.add(0.0);
//        }
//        List<Double> newSig = new ArrayList<>();
//        FourierTransform ft = new FourierTransform();
//        wavBuffer = wavBuffer.subList(0, 256);
//        wavBuffer.addAll(nlist);
//        List<ComplexNumber> psd = ft.fft(FourierTransform.addZeros(wavBuffer));
//        for(ComplexNumber cnp: psd){
//            newSig.add(Math.pow(ComplexNumber.abs(cnp), 2));
//        }
//
//        List<Double> testlist = wavBuffer;
//        for(int n = 0; n < testlist.size(); n++){
//            testlist.set(n, Math.pow(testlist.get(n),2 ));
//        }
//        double aa = Statistics.sum(testlist);
//        double bb = (1.0/newSig.size())*Statistics.sum(newSig);
//        newSig.size();
////
        permissionWriteAdd();
        try {
            datafile.save3D(dtwmatrixlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startButtonAnimation(Button button)
    {
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

    private void permissionReadAdd() {
        int hasReadExternalPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }

    private void permissionWriteAdd() {
        int hasReadExternalPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_PERMISSIONS);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_WRITE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

