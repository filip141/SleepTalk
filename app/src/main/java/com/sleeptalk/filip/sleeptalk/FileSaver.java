package com.sleeptalk.filip.sleeptalk;
import android.content.Context;
import android.os.Environment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by filip on 28.04.16.
 * PL: Klasa pomocnicza, służyła autorą do zapisywania danych
 * do pliku w celu późniejszego ich przetwarzania.
 */
public class FileSaver{

    String datafile = "//sdcard//data_file.txt";
    BufferedWriter fileWriter;

    public FileSaver(Context activity) {
        try {
            fileWriter = new BufferedWriter(new FileWriter(datafile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save data to file
    public void save(List<Double> points) throws IOException {

        for(Double b: points){
            fileWriter.write(b.toString());
            fileWriter.newLine();
        }
        fileWriter.close();
    }

    // Save data to file
    public void save3D(List<List<Double>> array3D) throws IOException {

        for(List<Double> list: array3D){
            for(Double b: list) {
                fileWriter.write(b.toString());
                fileWriter.newLine();
            }
            fileWriter.write("NA");
            fileWriter.newLine();
        }
        fileWriter.close();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    //Read from json file
    public JSONObject readFromJSON(String fileName) throws Exception {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, fileName);
        if(!file.exists())
            file.createNewFile();
        String fileContent = getStringFromFile(file);
        if(!fileContent.isEmpty()){
            JSONObject obj = new JSONObject(fileContent);
            return obj;
        }
        else{
            return null;
        }
    }

    // Save data to JSON file
    public void saveToJSON(List<List<Double>> itemList, String word, String filename) throws Exception {
        JSONObject obj = readFromJSON(filename);
        if(obj == null){
           obj = new JSONObject();
        }
        obj.put(word + System.currentTimeMillis(), itemList.toString());
        File sdcard = Environment.getExternalStorageDirectory();
        File filePath = new File(sdcard, filename);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(obj.toString());
        }
    }


}
