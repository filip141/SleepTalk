package com.sleeptalk.filip.sleeptalk;
import android.content.Context;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by filip on 28.04.16.
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


}