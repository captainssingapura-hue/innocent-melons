package com.example.fxsuite.qingming.world;

import com.example.fxsuite.avatar.look.DrawnDemo;
import com.example.fxsuite.avatar.look.RoadScenery;
import com.example.fxsuite.avatar.look.Scenery;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.qingming.land.Reach;

/**
 * The reach of the Bian River from the Rainbow Bridge to the city gate, as a demo of its own ({@link Reach}): its land,
 * the river cut into it, its water, its streets, lanes and towpaths; the ground the bridge and the gate will stand on,
 * a plain deck where the bridge will be. Its buildings, its boats and its people are to come. Known by its name,
 * {@code QINGMING}, its runs kept and replayed.
 */
public final class Qingming implements DrawnDemo {

    /** The reach. */
    public static final Qingming DEMO = new Qingming();

    private Qingming() {
    }

    /**
     * The reach told to the engine, by the world itself, being a project of its own: the engine's own worlds first,
     * then this demo by its name, to be found and offered. As often as asked; told once.
     */
    public static void tell() {
        com.example.fxsuite.avatar.Game.tellWorlds();
        com.example.fxsuite.avatar.sim.Demos.register(DEMO);
    }

    @Override
    public String name() {
        return "QINGMING";
    }

    @Override
    public String title() {
        return "Along the River at Qingming";
    }

    @Override
    public String description() {
        return "The reach of the Bian River from the Rainbow Bridge to the city gate, after Zhang Zeduan's scroll: its"
                + " plain, the river and its banks, its streets and towpaths, the ground for the bridge and the gate."
                + " Buildings, boats and people to come.";
    }

    @Override
    public double startYaw() {
        return Reach.START_YAW;
    }

    @Override
    public boolean recordable() {
        return true;
    }

    @Override
    public void lay(final Course.Layout world) {
        Reach.lay(world);
    }

    /** Its streets' surfaces, drawn in stretches as they are seen. */
    @Override
    public Scenery scenery() {
        return RoadScenery.of(Reach.roads());
    }
}
