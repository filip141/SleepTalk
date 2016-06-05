package com.sleeptalk.filip.sleeptalk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Klasa implementująca niestandardowy widok służący do wyświetlania animacji "Zzz".
 * Created by Mateusz on 2016-06-01.
 */
public class TextMove extends View {
    private Paint p,m;
    private int w,h;
    PointF[] letters;
    private int sign;

    /**
     * Konstruktor klasy TextMove.
     * @param context Kontekst aktywności.
     * @param attrs Zbiór atrybutów.
     */
    public TextMove(Context context, AttributeSet attrs) {
        super(context, attrs);
        sign=1;
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        m=new Paint(Paint.ANTI_ALIAS_FLAG);
        m.setColor(Color.parseColor("#D2D2D2"));
        m.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        m.setTextSize(55);

        letters = new PointF[]{new PointF(80,180),new PointF(90,150),new PointF(100,120),new PointF(110,90)};
    }

    /**
     * Metoda rysująca widok.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<letters.length;i++)
        {
            canvas.drawText("z",letters[i].x,letters[i].y,m);
        }
    }

    /**
     * Metoda zmieniająca położenie liter podczas animacji.
     */
    public void moveLetters() {
        for (int i=0;i<letters.length;i++)
        {
            if(letters[i].x<180 && letters[i].y>20)
            {
                if(sign==1)
                {
                    letters[i].set(letters[i].x,letters[i].y-3);
                }
                else
                {
                    letters[i].set(letters[i].x+3,letters[i].y);
                }
                sign=sign*(-1);
            }
            else
            {
                letters[i].set(80,180);
            }
        }
    }
}
