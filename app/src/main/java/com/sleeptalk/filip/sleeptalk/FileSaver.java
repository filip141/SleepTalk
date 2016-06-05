package com.sleeptalk.filip.sleeptalk;
import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Klasa pomocnicza, służyła autorom do zapisywania danych do pliku w celu późniejszego ich przetwarzania.
 * Created by filip on 02.05.16.
 */
public class FileSaver{

    private static final String datafile = "//sdcard//data_file.txt";
    Context activity;
    private static final String JSON_NAME = "/final.json";
    private BufferedWriter fileWriter;

    /**
     * Konstruktor klasy FileSaver.
     * @param activity Kontekst aplikacji.
     */
    public FileSaver(Context activity) {
        this.activity=activity;
        try {
            fileWriter = new BufferedWriter(new FileWriter(datafile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służy do zapisywania danych do pliku w celu utworzenia wykresu dwuwymiarowego.
     * @param points Lista współrzędnych punktów.
     * @throws IOException
     */
    public void save(List<Double> points) throws IOException {

        for(Double b: points){
            fileWriter.write(b.toString());
            fileWriter.newLine();
        }
        fileWriter.close();
    }

    /**
     * Metoda służy do zapisywania danych do pliku w celu utworzenia wykresu trójwymiarowego.
     * @param array3D Lista współrzędnych punktów.
     * @throws IOException
     */
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

    /**
     * Metoda konwertuje strumień danych do Stringa.
     * @param is Strumień danych.
     * @return String zawierający dane z strumienia danych.
     * @throws Exception
     */
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

    /**
     * Metoda służy do wczytywania pliku do Stringa.
     * @param fl Plik, który ma zostać wczytany.
     * @return String w którym jest zawartość pliku.
     * @throws Exception
     */
    public static String getStringFromFile (File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    /**
     * Służy do wczytywania obiektu JSON z pliku .json.
     * @param fileName  Nazwa pliku .json z którego mają być wczytane dane.
     * @return obiekt JSON z danymi z pliku .json.
     * @throws Exception
     */
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

    /**
     * Służy do wczytywania obiektu JSON z pliku .json znajdującego się w folderze Assets aplikacji.
     * @return obiekt JSON z danymi z pliku .json znajdującego się w folderze Assets aplikacji.
     */
    public JSONObject loadJSONFromAssets()
    {
        String json = null;
        JSONObject jo = null;
        try
        {
            InputStream is = activity.getAssets().open("final.json");
            int size = is.available();
            byte[] buffer =new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        try {
            jo= new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jo;
    }


    /**
     * Metoda służy do zapisywania współczynników Mfcc do pliku .json.
     * @param itemList Współczynniki Mfcc zapisywanego słowa.
     * @param word  Nazwa zapisywanego słowa
     * @param filename  Nazwa pliku do którego zapisujemy.
     * @throws Exception
     */
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

    /**
     * Metoda służy do utworzenia listy z obiektu JSON.
     * @param obj - obiekt JSON z którego tworzymy listę.
     * @return Lista utworzona z podanego obiektu JSON.
     * @throws Exception
     */
    public WordLibrary getListFromJSON(JSONObject obj) throws Exception {
        List<String> mfccCoeffs;
        int coeffsNumber = 39;
        JSONArray jsonList;
        String[] mfcc;
        String jsonKey;
        List<Double> coeffs;
        List<List<Double>> mellVectors;
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