package com.sleeptalk.filip.sleeptalk;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private Thread sleepTalkThread;
    private boolean turnedOn;
    private FileSaver datafile;
    private WordLibrary lib;
    private String fileName;
    MediaPlayer mDing;
    MediaPlayer mWlaczono;
    MediaPlayer mPoprawnie;
    MediaPlayer mBlad;
    MediaPlayer mPowtorz;
    MediaPlayer mAlarm;
    private List<String> words;
    private List<MediaPlayer> recWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

        saveJSONtoCache();
        // Android objects initialization
        final Button recButton = (Button) findViewById(R.id.rec_button);
        TextMove textMove=(TextMove) findViewById(R.id.view);
        Thread t = new Thread(new TextMoveAnim(new Handler(),textMove));
        t.start();

        startButtonAnimation(recButton);
        startTextAnimation(textMove);

        datafile = new FileSaver(this);
        try {
            lib = datafile.getListFromJSON(getCacheDir().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Initialize media types and add them to list
        recWords = new ArrayList<>();
        mDing = MediaPlayer.create(MainActivity.this,R.raw.ding);
        mWlaczono = MediaPlayer.create(MainActivity.this,R.raw.wlaczono_sleeptalk);
        mPoprawnie = MediaPlayer.create(MainActivity.this,R.raw.poprawnie);
        mBlad = MediaPlayer.create(MainActivity.this,R.raw.blad);
        mPowtorz = MediaPlayer.create(MainActivity.this,R.raw.jeszcze_raz);
        mAlarm = MediaPlayer.create(MainActivity.this,R.raw.alarm);

        MediaPlayer mButelka = MediaPlayer.create(MainActivity.this,R.raw.butelka);
        MediaPlayer mMetoda = MediaPlayer.create(MainActivity.this,R.raw.metoda);
        MediaPlayer mRoznice = MediaPlayer.create(MainActivity.this,R.raw.roznice);
        MediaPlayer mCzlowiek = MediaPlayer.create(MainActivity.this,R.raw.czlowiek);
        MediaPlayer mTelefon = MediaPlayer.create(MainActivity.this,R.raw.telefon);
        final MediaPlayer mWylacz = MediaPlayer.create(MainActivity.this,R.raw.wylacz);
        recWords.add(mButelka);
        recWords.add(mMetoda);
        recWords.add(mRoznice);
        recWords.add(mCzlowiek);
        recWords.add(mTelefon);
        words = buildWordList();
        // Initialize Wav recorder
        final WavRecord wv = new WavRecord();
        if (recButton != null) {
            recButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recButton.setEnabled(false);
                    // SleepTalk turned on or off
                    if(turnedOn){
                        mWylacz.start();
                        turnedOn = false;
                    }
                    else{
                        mWlaczono.start();
                        turnedOn = true;
                        sleepTalkThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int mPlayerIndex;
                                int min = 0;
                                String desiredWord;
                                String result;
                                int max = recWords.size() - 1;
                                Random r = new Random();
                                // First wait
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                while(true){
                                    // Close thread when button pressed
                                    if(!turnedOn){
                                        break;
                                    }
                                    else{
                                        // Take random word
                                        mPlayerIndex = r.nextInt(max - min + 1) + min;
                                        desiredWord = words.get(mPlayerIndex);
                                        recWords.get(mPlayerIndex).start();
                                        try {
                                            result = processWord(wv);
                                            if(result.equals(desiredWord)){
                                                mPoprawnie.start();
                                            }
                                            else{
                                                mBlad.start();
                                                Thread.sleep(1100);
                                                mPowtorz.start();
                                                result = processWord(wv);
                                                if(result.equals(desiredWord)){
                                                    mPoprawnie.start();
                                                }
                                                else{
                                                    mBlad.start();
                                                }
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        catch(IllegalArgumentException e){
                                            mPowtorz.start();
                                            try {
                                                Thread.sleep(1100);
                                                result = processWord(wv);
                                                if(result.equals(desiredWord)){
                                                    mPoprawnie.start();
                                                }
                                                else{
                                                    mBlad.start();
                                                }
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                            catch(IllegalArgumentException e2) {
                                                mAlarm.start();
                                            }
                                        }
                                    }
                                    // Wait
                                    try {
                                        Thread.sleep(60000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        sleepTalkThread.start();
                    }
                    recButton.setEnabled(true);
                }
            });
        }

    }

    private void saveJSONtoCache() {
        File f = new File(getCacheDir()+"/final.json");
        if (!f.exists()) try {

            InputStream is = getAssets().open("final.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void startTextAnimation(TextMove textMove) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(2000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        textMove.startAnimation(animation);
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

    public List<String> buildWordList(){
        List<String> wordList = new ArrayList<>();
        wordList.add("butelka");
        wordList.add("metoda");
        wordList.add("różnice");
        wordList.add("człowiek");
        wordList.add("telefon");
        return wordList;
    }

    public String processWord(WavRecord wv) throws InterruptedException {
        Thread.sleep(10000);
        mDing.start();
        Thread.sleep(1100);
        wv.startRecording();
        Thread.sleep(2800);
        fileName = wv.stopRecording();
        WavFile file = new WavFile(fileName);
        List<List<Double>> mfccCoefs = mfccComputing(file);
        String result = findWord(lib, mfccCoefs);
        wv.deleteFile();
        return result;
    }

}
