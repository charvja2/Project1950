package cz.cvut.fel.cyber.dca.engine.core;

/**
 * Created by Jan on 27. 10. 2015.
 */
public interface Loopable<I,O> {

    public O loop(I input);
}
