package melons.innocent.terrain.melon;

import com.example.fxsuite.existence.Earthworks;
import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Line;
import melons.innocent.terrain.Area;
import melons.innocent.terrain.Band;
import melons.innocent.terrain.Hue;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Path;
import melons.innocent.terrain.Sea;
import melons.innocent.terrain.Spot;
import melons.innocent.terrain.Steep;
import melons.innocent.terrain.Topography;
import melons.innocent.terrain.Watercourse;

import java.util.ArrayList;
import java.util.List;

/**
 * The Melon country's land features, laid over its ground in turn. Meadow over the plain and the low slopes, scrub
 * higher up, moss over the heights, sand along the waterline, bare crags where the ground is too steep to hold
 * anything; the Melon Wood on the east spur's lower flanks and the West Wood on the west ridge's; the village's
 * fields either side of it; the Melon River filling its bed to its banks, a metre deep over its thalweg; and Melon
 * Bay over all of the sea. Each labelled, of a kind of what exists, in a colour of its own. Laid over the country's
 * natural ground, its topographic map ({@link MelonTopography}), along its map's lines ({@link MelonCountry#map()}),
 * reading the ground and never changing it. Sizes in metres here.
 */
public record MelonFeatures() implements StatelessFunctionalObject {

    public static final MelonFeatures INSTANCE = new MelonFeatures();

    private static final double M = 100;

    /** The features, laid over the country's natural ground. */
    public LandFeatures features() {
        final Topography ground = MelonTopography.INSTANCE.topography();
        final MelonCountry map = MelonCountry.INSTANCE;
        return new LandFeatures(ground.name(), ground, map.map(), List.of(
                new Band("the Melon meadows", new Terrains.Meadow(), new Hue("#8cbf5a"), 1.5 * M, 60 * M),
                new Band("the scrub on the heights", new Terrains.Scrub(), new Hue("#9a9a62"), 60 * M, 350 * M),
                new Band("the high moss", new Terrains.Moss(), new Hue("#b2b096"), 350 * M, 2_000 * M),
                new Band("the beaches", new Terrains.Sand(), new Hue("#e2d4a0"), -2 * M, 1.5 * M),
                new Steep("the bare crags", new Terrains.Rock(), new Hue("#8a8580"), 0.7),
                new Area("the Melon Wood", new Terrains.Forest(), new Hue("#2f6b33"), new Path(areas().get(0))),
                new Area("the West Wood", new Terrains.Forest(), new Hue("#3a7a3c"), new Path(areas().get(1))),
                new Area("the village's west fields", new Earthworks.Field(), new Hue("#d2b85c"),
                        new Path(areas().get(2))),
                new Area("the village's east fields", new Earthworks.Field(), new Hue("#c49a48"),
                        new Path(areas().get(3))),
                new Watercourse("the Melon River", new Features.River(), new Hue("#3b7fc0"), new Path(map.river()),
                        new Line(new Knot(0, M), new Knot(5_000 * M, M)), 0, 30 * M),
                new Sea("Melon Bay", new Features.Bay(), new Hue("#2a6496"), new Path(map.shore()), 0)));
    }

    /**
     * The areas' boundaries, each closed round, as the country's map has their lines: the Melon Wood, the West Wood,
     * the village's west fields, its east fields.
     */
    public List<List<Spot>> areas() {
        return List.of(
                closed(1_700, 800, 2_700, 700, 2_950, 1_600, 2_500, 2_150, 1_900, 1_700),
                closed(-200, 1_500, 500, 1_400, 900, 2_300, 700, 3_300, 100, 2_600),
                closed(-750, -350, -200, -350, -200, 650, -750, 750),
                closed(200, -250, 470, -250, 520, 650, 250, 700));
    }

    /** The spots so many metres across and along, in turn, closed round back to the first. */
    private List<Spot> closed(final double... metres) {
        final List<Spot> spots = new ArrayList<>();
        for (int i = 0; i < metres.length; i += 2) {
            spots.add(new Spot(metres[i] * M, metres[i + 1] * M));
        }
        spots.add(spots.getFirst());
        return List.copyOf(spots);
    }
}
