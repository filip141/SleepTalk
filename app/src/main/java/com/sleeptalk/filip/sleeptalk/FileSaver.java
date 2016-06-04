package com.sleeptalk.filip.sleeptalk;
import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by filip on 28.04.16.
 * PL: Klasa pomocnicza, służyła autorą do zapisywania danych
 * do pliku w celu późniejszego ich przetwarzania.
 */
public class FileSaver{

    private static final String datafile = "//sdcard//data_file.txt";

    private static final String JSON_NAME = "/final.json";
    private BufferedWriter fileWriter;

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

    // Get Java ArrayList from JSON object
    public WordLibrary getListFromJSON(String cacheDir) throws Exception {
        List<String> mfccCoeffs;
        int coeffsNumber = 39;
        JSONArray jsonList;
        String[] mfcc;
        String jsonKey;
        List<Double> coeffs;
        List<List<Double>> mellVectors;
        JSONObject obj = readFromJSON(cacheDir+JSON_NAME);
        Iterator<String> iter = obj.keys();
        List<String> wordKeys = new ArrayList<>();
        List<List<List<Double>>> wordRel = new ArrayList<>();
        while(iter.hasNext()){
            jsonKey = iter.next();
            jsonList = (JSONArray) obj.get(jsonKey);
            for(int i = 0; i < jsonList.length(); i++){
                mellVectors = new ArrayList<>();
                coeffs = new ArrayList<>();
                mfcc = ((String) jsonList.get(i)).replace("[", "").replace("]", "").split(",");
                for(int j = 1; j < mfcc.length + 1; j++){
                    coeffs.add(Double.parseDouble(mfcc[j - 1]));
                    if(j % coeffsNumber == 0){
                        mellVectors.add(coeffs);
                        coeffs = new ArrayList<>();
                    }
                }
                wordRel.add(mellVectors);
                wordKeys.add(jsonKey);
            }
        }
        return new WordLibrary(wordRel, wordKeys);
    }


}

final class WordLibrary {
    private final List<List<List<Double>>> wordRel;
    private final List<String> wordKeys;

    public WordLibrary(List<List<List<Double>>> wordRel, List<String> wordKeys) {
        this.wordRel = wordRel;
        this.wordKeys = wordKeys;
    }

    // Get element index
    public List<Integer> getIndex(String key){
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < wordKeys.size(); i++){
            if(wordKeys.get(i).equals(key)){
                indexes.add(i);
            }
        }
        return indexes;
    }

    // Get found elements
    public List<List<List<Double>>> get(String key) {
        List<List<List<Double>>> found = new ArrayList<>();
        List<Integer> wordIdx = getIndex(key);
        if(wordIdx.size() == 0){
            return null;
        }
        else{
            for(int i = 0; i < wordIdx.size(); i++){
                found.add(wordRel.get(wordIdx.get(i)));
            }
        }

        return found;
    }

    // Get library keys
    public HashSet<String> keys() {
        return new HashSet<String>(wordKeys);
    }

    // Get library values
    public List<List<List<Double>>> values() {
        return wordRel;
    }
}