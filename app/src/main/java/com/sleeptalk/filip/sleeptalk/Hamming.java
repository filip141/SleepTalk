package com.sleeptalk.filip.sleeptalk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementacja okna Hamminga.
 * Klasa zawiera implementacje interfejsu okna, reprezentuje ona okno Hamminga.
 * Created by filip on 03.05.16.
 */
public class Hamming implements Window{

    private List<Double> windowCoeffs;

    /**
     * Konstruktor klasy Hamming.
     */
    public Hamming(){
        windowCoeffs = new ArrayList<>();
    }

    /**
     * Metoda tworzy okno Hamminga.
     * @param size Rozmiar okna.
     */
    @Override
    public void build(int size) {
        double coeff;
        for(int n = 0; n < size; n++){
            coeff = 0.54 - 0.46*Math.cos(2*Math.PI*n/(size - 1));
            windowCoeffs.add(coeff);
        }
    }

    /**
     * Metoda służy do wymnażania sygnału przez okno Hamminga.
     * @param signal Sygnał poddawany przemnażaniu.
     * @return Sygnał po operacji okienkowania.
     */
    @Override
    public List<Double> multiplyWithSignal(List<Double> signal) {
        List<Double> result = new ArrayList<>();
        Iterator<Double> it1 = windowCoeffs.iterator();
        Iterator<Double> it2 = signal.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            result.add(it1.next()*it2.next());
        }
        return result;
    }
}
