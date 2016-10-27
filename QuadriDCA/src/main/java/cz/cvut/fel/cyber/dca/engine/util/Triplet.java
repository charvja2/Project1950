package cz.cvut.fel.cyber.dca.engine.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 27.10.2016.
 */
public class Triplet<T> {

    private final List<T> triplet;

    public Triplet(T a, T b, T c) {
        triplet = new ArrayList<>();
        triplet.add(a);
        triplet.add(b);
        triplet.add(c);
    }

    public boolean contains(T t){
        return triplet.contains(t);
    }

    public boolean containsAll(T a, T b, T c){
        return triplet.contains(a)&&triplet.contains(b)&&triplet.contains(c);
    }



}
