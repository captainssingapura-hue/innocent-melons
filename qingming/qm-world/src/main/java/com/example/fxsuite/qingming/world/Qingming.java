package com.example.fxsuite.qingming.world;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import com.example.fxsuite.avatar.look.DrawnDemo;
import com.example.fxsuite.avatar.look.RoadScenery;
import com.example.fxsuite.avatar.look.Scenery;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.qingming.land.Reach;
import com.example.fxsuite.qingming.structures.Fixtures;

/**
 * The reach of the Bian River from the Rainbow Bridge to the city gate, as a demo of its own ({@link Reach}): its
 * land, the river cut into it, its water, its streets and towpaths; what stands in it, as its map declares
 * ({@link Fixtures}): the Rainbow Bridge over the river, the shops along the street; the ground the city's wall and
 * its gate will stand on. Its boats and its people are to come. Known by its name, {@code QINGMING}, its runs kept and
 * replayed.
 */
public final class Qingming implements DrawnDemo, StatelessFunctionalObject {

    /** The reach. */
    public static final Qingming DEMO = new Qingming();

    private Qingming() {
    }

    /**
     * The reach told to the engine, by the world itself, being a project of its own: the engine's own worlds first,
     * then this demo by its name, to be found and offered. As often as asked; told once.
     */
    public void tell() {
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
                + " plain, the river and its banks, its streets and towpaths, the Rainbow Bridge over the river, the"
                + " shops along the street, the ground for the gate. Boats and people to come.";
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
        Reach.INSTANCE.lay(world);
        world.pieces(Fixtures.INSTANCE.pieces());
    }

    /** Its streets' surfaces, drawn in stretches as they are seen. */
    @Override
    public Scenery scenery() {
        return RoadScenery.of(Reach.INSTANCE.roads());
    }
}
