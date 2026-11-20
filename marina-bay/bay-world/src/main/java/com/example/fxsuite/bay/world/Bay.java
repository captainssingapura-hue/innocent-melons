package com.example.fxsuite.bay.world;

import com.example.fxsuite.avatar.buildings.Standing;
import com.example.fxsuite.avatar.lifts.Built;
import com.example.fxsuite.bay.buildings.Refined;
import com.example.fxsuite.bay.land.MarinaBay;
import com.example.fxsuite.avatar.look.DrawnDemo;
import com.example.fxsuite.avatar.look.RoadScenery;
import com.example.fxsuite.avatar.look.Scenery;
import com.example.fxsuite.avatar.sim.Course;

import java.util.List;

/**
 * Marina Bay as a demo of its own ({@link MarinaBay}): its land and its water, what stands on it, who is about, where
 * the avatar starts, its ground not ruled; its roads laid as roads are laid now, stood on, drawn in stretches as they
 * are seen ({@link RoadScenery}); its refined towers ({@link Refined}) standing in it, each where it stands, whole, to
 * be walked into, broken into, ridden up. Known by its name, {@code MARINA_BAY}, its runs kept and replayed.
 */
public final class Bay implements DrawnDemo {

    /** The bay. */
    public static final Bay DEMO = new Bay();

    /** Its refined towers where each stands; each built whole: made the first time the bay is laid, and kept. */
    private static List<Standing> towers;
    private static List<Built> built;

    private Bay() {
    }

    /**
     * The bay told to the engine, by the bay itself, being a project of its own: the engine's own worlds first, then
     * this demo by its name, to be found and offered. As often as asked; told once.
     */
    public static void tell() {
        com.example.fxsuite.avatar.Game.tellWorlds();
        com.example.fxsuite.avatar.sim.Demos.register(DEMO);
    }

    @Override
    public String name() {
        return "MARINA_BAY";
    }

    @Override
    public String title() {
        return "Marina Bay";
    }

    @Override
    public String description() {
        return "The bay as it lies, round the three towers and the park on their roofs: its land, its sea, the bay"
                + " and the river's mouth behind the barrage, the gardens' lakes, its roads and their bridges;"
                + " its refined towers standing in it, offices and homes.";
    }

    @Override
    public double startYaw() {
        return 90;
    }

    @Override
    public boolean recordable() {
        return true;
    }

    /** Its ground as the bay makes it; then each of its refined towers where it stands, drawn by its own keys. */
    @Override
    public void lay(final Course.Layout world) {
        MarinaBay.lay(world);
        final List<Standing> standing = towers();
        for (int k = 0; k < standing.size(); k++) {
            standing.get(k).register(built.get(k));
            world.pieces(standing.get(k).pieces(built.get(k)));
        }
    }

    /** Its refined towers where each stands, each built whole the first time it is asked for. */
    static synchronized List<Standing> towers() {
        if (towers == null) {
            towers = Refined.standing();
            built = towers.stream().map(Standing::built).toList();
        }
        return towers;
    }

    /** Its roads' pavements and markings, each drawn once where one lies over another at one level, as they are seen. */
    @Override
    public Scenery scenery() {
        return RoadScenery.of(MarinaBay.roads());
    }
}
