package com.sleeptalk.filip.sleeptalk;


import android.os.Handler;

/**
 *  Klasa implementująca zachowanie animacji wyświetlanej w widoku TextMove.
 *  Created by Mateusz on 2016-06-01.
 */
public class TextMoveAnim implements Runnable {
    private Handler handler;
    private TextMove textMove;

    /**
     * Konstruktor klasy TextMoveAnim.
     * @param h Handler pozwalający na przesyłanie i przetwarzanie obiektów Runnable.
     * @param t Widok TextMove, której zachowanie jest animowane.
     */
    public TextMoveAnim(Handler h,TextMove t) {
        this.handler=h;
        this.textMove=t;
    }

    /**
     * Metoda tworząca i uruchamiająca wątek animacji.
     */
    @Override
    public void run() {
        while (true) {
            handler.post(new Runnable() {
                public void run() {
                    textMove.moveLetters();
                    textMove.invalidate();
                }
            });
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
