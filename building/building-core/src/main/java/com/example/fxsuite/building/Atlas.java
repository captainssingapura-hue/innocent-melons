package com.example.fxsuite.building;

import com.example.fxsuite.avatar.build.Sites;
import com.example.fxsuite.avatar.build.Town;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;
import com.example.fxsuite.historian.Years;
import com.example.fxsuite.taxonomy.Leaf;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A map of what stands in a world, declared: whose the world is, when, and in what year; then each thing by a place
 * and a specification. An implementation is bound once ({@link #with}), of the map's own civilization and era, as the
 * compiler checks; what it then makes is put by a name, a place and a specification of its kind, nothing more. What is
 * older than the map and standing still is bound apart ({@link #standing}), and checked by its years.
 *
 * @param <C> whose the world is
 * @param <E> when
 */
public final class Atlas<C extends Civilization, E extends Era<? super C>> {

    private final String name;
    private final C civilization;
    private final E era;
    private final Year year;
    private final List<Fixture> fixtures = new ArrayList<>();

    /** One thing on the map: what it is called, where it stands and which way it faces, what it is, and it built. */
    public record Fixture(String name, double x, double z, double yaw, Leaf<?> thing, Civilization civilization,
                          Era<?> era, Structure structure) {
    }

    /** What a bound implementation makes: put by a name, a place and a specification. */
    public interface Maker<S> {

        /**
         * One of it called {@code name}, its middle at ({@code x}, {@code z}), its front facing {@code yaw}, built
         * from {@code spec}.
         */
        Maker<S> at(String name, double x, double z, double yaw, S spec);
    }

    /** A map called {@code name}, of {@code civilization} in {@code era}, as it stood in {@code year}. */
    public Atlas(final String name, final C civilization, final E era, final Year year) {
        this.name = name;
        this.civilization = civilization;
        this.era = era;
        this.year = year;
    }

    /** {@code impl} bound: of the map's own civilization and era, so what it makes is of the map's time. */
    public <T extends Leaf<?>, S extends Spec<T, C, E>> Maker<S> with(final ThingImpl<T, C, E, S> impl) {
        return maker(impl);
    }

    /**
     * {@code impl} bound, of any civilization and era: what was built before the map's year and stands in it still,
     * or was built by others. That its era had begun by the map's year is checked ({@link #problems}).
     */
    public <T extends Leaf<?>, D extends Civilization, F extends Era<? super D>, S extends Spec<T, D, F>>
            Maker<S> standing(final ThingImpl<T, D, F, S> impl) {
        return maker(impl);
    }

    private <T extends Leaf<?>, D extends Civilization, F extends Era<? super D>, S extends Spec<T, D, F>>
            Maker<S> maker(final ThingImpl<T, D, F, S> impl) {
        return new Maker<>() {
            @Override
            public Maker<S> at(final String fixture, final double x, final double z, final double yaw, final S spec) {
                fixtures.add(new Fixture(fixture, x, z, yaw, impl.thing(), impl.civilization(), impl.era(),
                        impl.build(spec)));
                return this;
            }
        };
    }

    /** What it is called. */
    public String name() {
        return name;
    }

    public C civilization() {
        return civilization;
    }

    public E era() {
        return era;
    }

    /** The year it shows. */
    public Year year() {
        return year;
    }

    /** Everything on it, in the order it was put. */
    public List<Fixture> fixtures() {
        return List.copyOf(fixtures);
    }

    /**
     * Everything wrong with it, all of it; none if nothing is: its year outside its era or its civilization's years;
     * two things of one name; a thing that is no kind of building; a thing whose era had not begun by the map's year,
     * or whose civilization had not; and whatever is wrong with each thing as built ({@link Structure#problems}).
     */
    public List<String> problems() {
        final List<String> problems = new ArrayList<>();
        if (!era.spans(year)) {
            problems.add(name + ": " + Years.said(year) + " is not in the " + era.name());
        }
        if (!civilization.during(year)) {
            problems.add(name + ": " + civilization.name() + " was not there in " + Years.said(year));
        }
        final Set<String> names = new HashSet<>();
        for (final Fixture f : fixtures) {
            if (!names.add(f.name())) {
                problems.add("two things called " + f.name());
            }
            if (!Buildings.TREE.leaves().contains(f.thing())) {
                problems.add(f.name() + ": " + f.thing().title() + " is no kind of building");
            }
            if (f.era().from().isAfter(year)) {
                problems.add(f.name() + ": the " + f.era().name() + " had not begun by " + Years.said(year));
            }
            if (f.civilization().from().isAfter(year)) {
                problems.add(f.name() + ": " + f.civilization().name() + " was not there by " + Years.said(year));
            }
            f.structure().problems().forEach(p -> problems.add(f.name() + ": " + p));
        }
        return problems;
    }

    /**
     * Everything on it as pieces of a world, each standing on {@code land} where it is put, drawn by keys from
     * {@code firstKey} on in the order they were put: told to the drawing here, the implementations knowing nothing of
     * keys.
     */
    public List<Course.Piece> pieces(final Land land, final int firstKey) {
        final List<Fixture> all = fixtures();
        if (all.isEmpty()) {
            return List.of();
        }
        Sites.register(firstKey, firstKey + all.size(), k -> all.get(k).structure().solids());
        final List<Course.Piece> pieces = new ArrayList<>();
        for (int k = 0; k < all.size(); k++) {
            final Fixture f = all.get(k);
            pieces.add(Town.building(f.structure().solids(), new Place(f.x(), f.z(), land.height(f.x(), f.z()),
                    f.yaw()), firstKey + k, f.structure().shape(), 1));
        }
        return pieces;
    }
}
