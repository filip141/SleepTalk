package com.sleeptalk.filip.sleeptalk;

import java.util.Collections;
import java.util.List;

/**
 * Klasa implementująca metody statystyczne.
 * Klasa pomocnicza zawierająca kilka metod "statystycznych" służących do wyliczenia średniej, sumy bądz wariancji.
 * Created by filip on 03.05.16.
 */
public class Statistics {

    /**
     * Metoda oblicza średnią z elementów double listy.
     * @param list Lista dla której obliczana jest średnia.
     * @return Średnia z elementów listy.
     */
    public static double mean(List<Double> list){
        return sum(list)/ list.size();
    }

    /**
     * Metoda zwraca sumę elementów listy.
     * @param list Lista dla której obliczana jest suma.
     * @return Suma elementów listy.
     */
    public static double sum(List<Double> list){
        double sum = 0;
        for(Double num: list){
            sum+=num;
        }
        return sum;
    }

    /**
     * Metoda oblicza średnią z elementów int listy.
     * @param list Lista dla której obliczana jest średnia.
     * @return Średnia z elementów listy.
     */
    public static int isum(List<Integer> list){
        int sum = 0;
        for(Integer num: list){
            sum+=num;
        }
        return sum;
    }

    /**
     * Metoda oblicza wariancję elementów listy.
     * @param list Lista dla której obliczana jest wariancja.
     * @return Wariancja elementów listy.
     */
    public static double var(List<Double> list){
        double varSum = 0;
        double listMean = mean(list);
        for(Double num: list){
            varSum+= Math.pow(num - listMean, 2);
        }
        return varSum/ list.size();
    }

    /**
     * Znajduje maksymalną wartość w liście obiektów Double.
     * @param signal Lista w której szukamy maksimum.
     * @return Wartość maksymalna listy.
     */
    public static double max(List<Double> signal){
        return Collections.max(signal);
    }
    /**
     * Znajduje maksymalną wartość w liście obiektów Integer.
     * @param signal Lista w której szukamy maksimum.
     * @return Wartość maksymalna listy.
     */
    public static int imax(List<Integer> signal){
        return Collections.max(signal);
    }

    /**
     * Znajduje indeks maksymalnej wartości w liście obiektów Double.
     * @param signal Lista w której szukamy indeksu maksimum.
     * @return Indeks wartości maksymalnej listy.
     */
    public static int argmax(List<Double> signal){
        return signal.indexOf(max(signal));
    }
    /**
     * Znajduje indeks maksymalnej wartości w liście obiektów Integer.
     * @param signal Lista w której szukamy indeksu maksimum.
     * @return Indeks wartości maksymalnej listy.
     */
    public static int iargmax(List<Integer> signal){
        return signal.indexOf(imax(signal));
    }
}
