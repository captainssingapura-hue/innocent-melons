package melons.innocent.terrain.meadow;

import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Line;
import melons.innocent.terrain.Band;
import melons.innocent.terrain.Hue;
import melons.innocent.terrain.Lake;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Path;
import melons.innocent.terrain.Topography;
import melons.innocent.terrain.Watercourse;

import java.util.List;

/**
 * The meadow's land features, laid over its ground in turn, reading it and never changing it: meadow over the valley
 * and its slopes up to 70 m, the moors above; the river a metre deep in its channel, filling it to its banks' tops and
 * no further; the pond in the hollow up the slope east of the village, filled to a metre under the hollow's rim. Each
 * labelled, of a kind of what exists, in a colour of its own. No roads yet.
 */
public record MeadowFeatures() implements StatelessFunctionalObject {

    public static final MeadowFeatures INSTANCE = new MeadowFeatures();

    private static final double M = 100;

    /** The features, laid over the meadow's ground. */
    public LandFeatures features() {
        final Topography ground = MeadowTopography.INSTANCE.topography();
        final MeadowMap map = MeadowMap.INSTANCE;
        return new LandFeatures(ground.name(), ground, map.map(ground), List.of(
                new Band("the meadows", new Terrains.Meadow(), new Hue("#8cbf5a"), -2 * M, 70 * M),
                new Band("the moors", new Terrains.Moss(), new Hue("#a39d6a"), 70 * M, 500 * M),
                new Watercourse("the river", new Features.River(), new Hue("#3b7fc0"), new Path(map.river(ground)),
                        new Line(new Knot(0, M), new Knot(20_000 * M, M)), Double.NEGATIVE_INFINITY, 8.5 * M),
                new Lake("the pond", new Features.Pond(), new Hue("#4a8fc8"), new Path(map.pond(ground)), 3 * M)));
    }
}
