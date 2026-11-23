package melons.innocent.demos.village;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.texture.stone.CoursedStone;
import com.example.fxsuite.texture.stone.Footed;
import com.example.fxsuite.texture.stone.RubbleWall;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * The village, as a place to walk and to fly over: eight homes along a lane. Its yards are walled, by turns, with
 * adobe on a footing of rubble, with the old grey brick on courses of stone, and with rubble all through. All the
 * yards of one kind of wall are one form with one look, drawn in one call; every house is the one half melon, drawn
 * once for each. Before the village is its lake: a kerb of rubble, and still water within it, each drawn in one call
 * with the surface its material has. Off the lake's end is a car park of concrete with a hundred cars standing in
 * it ({@link CarParkDrawn}), whose windows are seen through and so are drawn after everything else; and off its
 * other end a small wood of bare trees, and west of the towers a forest of four thousand, drawn as one wood
 * ({@link GroveDrawn}). What is in the way is the same walls, the same houses, the same kerb, the same cars and
 * the same trees' trunks.
 */
public final class VillagePlace implements Place {

    private static final double RUBBLE_FOOT = 45;
    private static final double STONE_FOOT = 61.6;

    private final Village village = new Village(8);
    private final List<Mat4> houses = village.houses();
    private final Obstacles inTheWay = village.inTheWay();
    private final List<Formed> made = new ArrayList<>();
    private final List<Formed> yards = new ArrayList<>();
    private final List<Formed> lake = new ArrayList<>();
    private final Formed melon;
    /** The car park and its cars, once made: null only while the place is being made. */
    private CarParkDrawn carPark;
    private GroveDrawn grove;

    /** The village, on {@code card}, its layouts as {@code laid} says. */
    public VillagePlace(final Card card, final Laid laid) {
        final Watermelon watermelon = new Watermelon();
        try {
            final List<Surface> wallings = wallings();
            for (int walling = 0; walling < wallings.size(); walling++) {
                final Forming forming = new Forming(new Vec3(0, 0, 0));
                for (final Village.Home home : village.homesteads()) {
                    if (home.walling() == walling) {
                        home.yard().formed(forming);
                    }
                }
                yards.add(kept(new Formed(card, new Look(laid, wallings.get(walling)), forming.formed())));
            }
            final Forming towered = new Forming(new Vec3(0, 0, 0));
            village.towers().forEach(tower -> tower.walls().formed(towered));
            yards.add(kept(new Formed(card, new Look(laid, village.towers().getFirst().laid()), towered.formed())));
            this.melon = kept(new Formed(card, new Look(laid, watermelon.rind()), watermelon.halved(96)));
            final Shades shades = new Shades();
            final Forming kerb = new Forming(village.lake().stands());
            village.lake().kerbed(kerb);
            lake.add(kept(new Formed(card, new Look(laid, shades.of(new Materials.Rubble())), kerb.formed())));
            lake.add(kept(new Formed(card, new Look(laid, shades.of(new Materials.Water())), village.lake().sheet())));
            this.carPark = new CarParkDrawn(card, laid, village.carPark(), village.woodCars());
            this.grove = new GroveDrawn(card, laid, village.woods());
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** What the yards are walled with, by turns: adobe on rubble, grey brick on stone, and rubble. */
    private List<Surface> wallings() {
        return List.of(
                new Footed(Brickwork.ADOBE.wall(), new RubbleWall().surface(), RUBBLE_FOOT).surface(),
                new Footed(Brickwork.CHINESE_GREY.wall(), new CoursedStone().wall().surface(), STONE_FOOT).surface(),
                new RubbleWall().surface());
    }

    private Formed kept(final Formed formed) {
        made.add(formed);
        return formed;
    }

    @Override
    public Obstacles inTheWay() {
        return inTheWay;
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        yards.forEach(yard -> yard.drawn(pass, new Mat4(), camera, 1));
        houses.forEach(stands -> melon.drawn(pass, stands, camera, 1));
        lake.forEach(part -> part.drawn(pass, new Mat4(), camera, 1));
        carPark.drawn(pass, camera);
        grove.drawn(pass, camera);
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        // the wood's far haze first, then the cars' windows: one before the other wherever they lie, which is wrong
        // only where a window and a crown's haze cross in the picture, and then only in which is laid over which
        grove.seenThrough(pass, camera);
        carPark.seenThrough(pass, camera);
    }

    @Override
    public void close() {
        if (grove != null) {
            grove.close();
            grove = null;
        }
        if (carPark != null) {
            carPark.close();
            carPark = null;
        }
        made.reversed().forEach(Formed::close);
        made.clear();
    }
}
