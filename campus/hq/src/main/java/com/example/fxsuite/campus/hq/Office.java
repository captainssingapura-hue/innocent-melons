package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Place;
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
    static final double TOP = Furniture.TOP;
    static final double LEG = Furniture.LEG;
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
        final List<Solid> f = new ArrayList<>(Furniture.table("desk", Material.TIMBER, Material.TIMBER, 0,
                deskBack() + DESK_DEEP / 2, DESK_WIDE, DESK_DEEP, DESK_HIGH, FLOOR));
        // its back toward the door: who sits faces the desk, and the back wall beyond it
        f.addAll(Furniture.chair("chair", Material.TIMBER, 0, chairAt(), Furniture.Facing.BACK, CHAIR, SEAT, CHAIR_BACK,
                FLOOR));
        return f;
    }

    /** What it is made of to the simulation: the hut's walls and roof, and its desk and chair in the way. */
    public List<Volume> shape() {
        final List<Volume> shape = new ArrayList<>(EarthHouse.of(hut().size()).shape());
        shape.add(Furniture.footprint(0, deskBack() + DESK_DEEP / 2, DESK_WIDE, DESK_DEEP, FLOOR, DESK_HIGH));
        shape.add(Furniture.footprint(0, chairAt(), CHAIR, CHAIR, FLOOR, SEAT));
        return shape;
    }
}
