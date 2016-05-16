package maccevedor.maveru;

import android.graphics.drawable.shapes.RectShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mauricio.acevedo on 14/05/16.
 */
public class Prueba1 extends RectShape{

    private List<Str>
    mSaludo = new ArrayList<>();
    public static final String  HOLA = "Hola Prueba1";


    //
    // CONSTRUCTORS
    //


    public Prueba1() {

    }

    public Prueba1(List<Str> saludo) {
        mSaludo = saludo;

    }

    public List<Str> getSaludo() {
        return mSaludo;
    }

    public void setSaludo(List<Str> saludo) {
        mSaludo = saludo;
    }

    @Override
    public boolean hasAlpha() {
        //return super.hasAlpha();
        return true;
    }

    @Override
    public String toString() {
        return "Prueba1{" +
                "mSaludo=" + mSaludo +
                '}';
    }

    public boolean add(Str object) {
        return mSaludo.add(object);
    }
}
