package com.example.fxsuite.building;

import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * How a thing is built by a civilization in an era: a function of its specification, the same structure from the same
 * specification every time, nothing else read and nothing changed. Its type says what it builds, whose way and when,
 * and what it builds from; the same kind of specification may be built from by more than one implementation.
 *
 * @param <T> the thing it builds: a leaf, a branch being built by nothing
 * @param <C> whose way of building
 * @param <E> when
 * @param <S> what it builds from
 */
public interface ThingImpl<T extends Leaf<?>, C extends Civilization, E extends Era<? super C>,
        S extends Spec<T, C, E>> {

    /** The thing it builds. */
    T thing();

    /** Whose way of building it is. */
    C civilization();

    /** When. */
    E era();

    /** {@code spec} built. */
    Structure build(S spec);
}
