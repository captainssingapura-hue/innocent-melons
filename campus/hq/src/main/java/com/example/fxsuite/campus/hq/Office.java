package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;

import java.util.ArrayList;
import java.util.List;

/**
 * An office on the campus: one of the meadow's huts ({@link Village.House}), where it stands, as it is built, numbered;
 * furnished as an office: a desk against its back wall, facing its door, and a chair at it. In the hut's own frame, as
 * its solids are: across, along (its front, its door, ahead), up; centimetres.
 *
 * @param number its number on the campus, from 1
 * @param village which of the village's huts it is ({@link Village#VILLAGE}, from 0)
 */
public record Office(int number, int village) {

    /** Its desk: so wide across, so deep, so high; its top so thick; its legs so square. */
    static final double DESK_WIDE = 120;
    static final double DESK_DEEP = 60;
    static final double DESK_HIGH = 75;
    static final double TOP = 4;
    static final double LEG = 6;
    /** Its chair: its seat so square and so high, its back so high; so far from the desk's edge. */
    static final double CHAIR = 45;
    static final double SEAT = 45;
    static final double CHAIR_BACK = 90;
    static final double CHAIR_OFF = 5;
    /** How far its desk's back corners stand clear of its walls. */
    static final double CLEAR = 8;
    /** How high its floor of beaten earth is: what stands on it, stands on that. */
    static final double FLOOR = 4;

    public Office {
        if (number < 1 || village < 0 || village >= Village.VILLAGE.size()) {
            throw new IllegalArgumentException("an office numbered from 1, one of the village's huts");
        }
    }

    /** What it is called. */
    public String name() {
        return "Office " + number;
    }

    /** The hut it is. */
    public Village.House hut() {
        return Village.VILLAGE.get(village);
    }

    /** Where it stands, facing as its door does. */
    public Place place() {
        return hut().place();
    }

    /** How far its walls' inside is from its middle. */
    public double inside() {
        return hut().size().width() / 2 - hut().size().thick();
    }

    /** Where its desk's back is, along: as near its back wall as lets its back corners stand clear of it. */
    double deskBack() {
        final double r = inside() - CLEAR;
        return -Math.sqrt(r * r - DESK_WIDE / 2 * (DESK_WIDE / 2));
    }

    /** Where its chair's middle is, along: before its desk, toward its door. */
    double chairAt() {
        return deskBack() + DESK_DEEP + CHAIR_OFF + CHAIR / 2;
    }

    /** All of it: the hut as it is built, and its desk and chair in it. */
    public List<Solid> solids() {
        final List<Solid> all = new ArrayList<>(Village.solids(village));
        all.addAll(furniture());
        return all;
    }

    /** Its desk and chair, of timber: the desk's top on four legs; the chair's seat on four legs, its back behind it. */
    public List<Solid> furniture() {
        final List<Solid> f = new ArrayList<>();
        final double back = deskBack();
        final double front = back + DESK_DEEP;
        final double w = DESK_WIDE / 2;
        f.add(box("desk, top", -w, w, back, front, DESK_HIGH - TOP, DESK_HIGH));
        for (final double x : new double[] {-w, w - LEG}) {
            for (final double y : new double[] {back, front - LEG}) {
                f.add(box("desk, leg", x, x + LEG, y, y + LEG, FLOOR, DESK_HIGH - TOP));
            }
        }
        final double c = CHAIR / 2;
        final double at = chairAt();
        f.add(box("chair, seat", -c, c, at - c, at + c, SEAT - TOP, SEAT));
        for (final double x : new double[] {-c, c - LEG / 2}) {
            for (final double y : new double[] {at - c, at + c - LEG / 2}) {
                f.add(box("chair, leg", x, x + LEG / 2, y, y + LEG / 2, FLOOR, SEAT - TOP));
            }
        }
        // its back toward the door: who sits faces the desk, and the back wall beyond it
        f.add(box("chair, back", -c, c, at + c - LEG / 2, at + c, SEAT, CHAIR_BACK));
        return f;
    }

    /** What it is made of to the simulation: the hut's walls and roof, and its desk and chair in the way. */
    public List<Volume> shape() {
        final List<Volume> shape = new ArrayList<>(EarthHouse.of(hut().size()).shape());
        shape.add(rectangle(DESK_WIDE / 2, deskBack(), deskBack() + DESK_DEEP, DESK_HIGH));
        shape.add(rectangle(CHAIR / 2, chairAt() - CHAIR / 2, chairAt() + CHAIR / 2, SEAT));
        return shape;
    }

    /** A box in the way, {@code half} either side of its middle across, from {@code y0} to {@code y1} along, from its floor to {@code top}. */
    private static Prism rectangle(final double half, final double y0, final double y1, final double top) {
        return new Prism(List.of(new Vec2(-half, y0), new Vec2(half, y0), new Vec2(half, y1), new Vec2(-half, y1)),
                FLOOR, top);
    }

    /** A box of timber, square to the hut, its corners in the order a solid's are: across, along, up. */
    private static Solid box(final String name, final double x0, final double x1, final double y0, final double y1,
                             final double z0, final double z1) {
        final List<Point> corners = new ArrayList<>(8);
        for (int k = 0; k < 8; k++) {
            corners.add(new Point((k & 1) == 0 ? x0 : x1, (k & 2) == 0 ? y0 : y1, (k & 4) == 0 ? z0 : z1));
        }
        return new Solid(name, Role.DRESSING, Material.TIMBER, corners);
    }
}
