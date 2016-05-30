package com.example.mateusz.sleeptalk;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Mateusz on 2016-05-30.
 */
public class WordRec {
    private boolean isRecording=false;
    private Thread recordingThread;
    private int BufferElements2Rec=1024;
    private int BytesPerElement = 2;
    AudioFormat audioFormat;
    private int minBufferSize;
    final AudioRecord myAudioRecorder;
    public WordRec() {
        audioFormat = new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(16000).setChannelMask(AudioFormat.CHANNEL_IN_MONO).build();
        minBufferSize = AudioRecord.getMinBufferSize(16000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        myAudioRecorder = new AudioRecord.Builder().setAudioSource(MediaRecorder.AudioSource.MIC).setAudioFormat(audioFormat).setBufferSizeInBytes(3*minBufferSize).build();
    }

    public void recording()
    {
        myAudioRecorder.startRecording();
        isRecording=true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"Audio Thread");
        recordingThread.start();
        try {
            wait(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRecording=false;
        myAudioRecorder.stop();
        myAudioRecorder.release();
        recordingThread=null;
    }

    private void writeAudioDataToFile() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/recorded.wav";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os =new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(isRecording)
        {
            myAudioRecorder.read(sData,0,BufferElements2Rec);
    try{
        byte bData[] = short2byte(sData);
        os.write(bData,0,BufferElements2Rec*BytesPerElement);
    }
    catch (IOException e){
        e.printStackTrace();
    }
        }
    }

    private byte[] short2byte(short[] sData) {
     int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize*2];
        for(int i =0;i<shortArrsize;i++)
        {
            bytes[i*2]=(byte)(sData[i] & 0x00FF);
            bytes[(i*2)+1]=(byte) (sData[i]>> 8);
            sData[i]=0;
        }
        return bytes;
    }
}
