package com.sleeptalk.filip.sleeptalk;

/**
 *Instancja klasy reprezentuje liczbę zespoloną.
 *Posiada również metody statyczne umożliwiające podstawowe operacje na liczbach zespolonych.
 *Created by filip on 02.05.16.
 */
public class ComplexNumber {

    double real;
    double img;

    /**
     * Konstruktor klasy ComplexNumber
     * @param real - Część rzeczywista liczby zespolonej.
     * @param complex - Część urojona liczby zespolonej.
     */
    public ComplexNumber(double real, double complex) {
        this.real = real;
        this.img = complex;
    }

    /**
     * Metoda służy do dodawania dwóch liczb zespolonych.
     * @param first Pierwszy składnik.
     * @param next Pierwszy składnik.
     * @return Suma liczb zespolonych
     */
    public static ComplexNumber add(ComplexNumber first, ComplexNumber next){
        return new ComplexNumber(first.real + next.real, first.img + next.img);
    }

    /**
     * Metoda obliczająca eksponentę z liczby zespolonej.
     * @param arg - Liczba zespolona.
     * @return Eksponenta
     */
    public static ComplexNumber exp(ComplexNumber arg) {
        double r = Math.exp(arg.real);
        return new ComplexNumber(r * Math.cos(arg.img), r * Math.sin(arg.img));
    }

    /**
     * Metoda służy do mnożenia dwóch liczb zespolonych.
     * @param first Pierwszy czynnik.
     * @param next Drugi czynnik.
     * @return Iloczyn dwóch liczb zespolonych.
     */
    public static ComplexNumber multiply(ComplexNumber first, ComplexNumber next){
        double newReal = first.real*next.real - first.img*next.img;
        double newImag = first.real*next.img + first.img*next.real;
        return new ComplexNumber(newReal, newImag);
    }

    /**
     * Metoda zwraca część urojoną liczby zespolonej.
     * @param z Liczba zespolona.
     * @return Część urojona liczby zespolonej.
     */
    public static double imag(ComplexNumber z){
        return z.img;
    }

    /**
     * Metoda zwraca część rzeczywistą liczby zespolonej.
     * @param z Liczba zespolona.
     * @return Część rzeczywista liczby zespolonej.
     */
    public static double real(ComplexNumber z){
        return z.real;
    }

    /**
     * Metoda zwraca moduł liczby zespolonej.
     * @param z Liczba zespolona.
     * @return Moduł liczby zespolonej.
     */
    public static double abs(ComplexNumber z){
        return Math.sqrt(Math.pow(z.real, 2) + Math.pow(z.img,2));
    }

    /**
     * Metoda zapisuje liczbę zespoloną w postaci Stringa.
     * @return String zawierający liczbę zespoloną w postaci algebraicznej.
     */
    public String toString() {
        return this.real + " + " + this.img + "j";
    }
}