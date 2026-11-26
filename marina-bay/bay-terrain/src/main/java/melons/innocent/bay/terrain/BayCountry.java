package melons.innocent.bay.terrain;

import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;
import melons.innocent.terrain.Band;
import melons.innocent.terrain.Course;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Feature;
import melons.innocent.terrain.Hue;
import melons.innocent.terrain.Lake;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Loop;
import melons.innocent.terrain.Path;
import melons.innocent.terrain.Region;
import melons.innocent.terrain.Spot;
import melons.innocent.terrain.Topography;
import melons.innocent.terrain.Work;
import melons.innocent.terrain.Worked;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Marina Bay's land on the terrain, as made: its ground found level at 0 m, the towers' ground; the sea, the bay, its
 * channel and the river's mouth dredged 8 m down, their bank 12 m wide within their shores, so each shore is the top
 * of its bank; the gardens' lakes dug 2 m down, their bank 6 m wide within theirs. Over it, the land; the sea and the
 * bay's water 3 m under the land; and the lakes' half a metre under it, each by its name. As the bay's own land lays
 * it ({@link com.example.fxsuite.bay.land.MarinaBay#ground}), its ground in made works rather than basins.
 *
 * @param lines the bay's lines
 */
public record BayCountry(BayLines lines) {

    private static final double M = 100;

    /** The bay's own. */
    public BayCountry() {
        this(new BayLines());
    }

    /** The country: its works over its ground, its features over them. */
    public LandFeatures features() {
        final Extent world = lines.world();
        final Topography found = new Topography("Marina Bay", world, 0, List.of(), List.of(), List.of());
        final Map<String, List<Spot>> lakes = lines.lakes();
        final List<Loop> dug = lakes.values().stream().map(Loop::new).toList();
        final Worked made = new Worked(found, List.of(
                new Work("the sea, the bay and the river, dredged",
                        new Region(List.of(new Loop(lines.land()), new Loop(lines.basin())), true), -8 * M, 12 * M,
                        8.0 / 12, Double.POSITIVE_INFINITY),
                new Work("the gardens' lakes, dug", new Region(dug, false), -2 * M, 6 * M, 2.0 / 6,
                        Double.POSITIVE_INFINITY)));
        final List<Spot> edge = List.of(new Spot(world.x0(), world.z0()), new Spot(world.x1(), world.z0()),
                new Spot(world.x1(), world.z1()), new Spot(world.x0(), world.z1()), new Spot(world.x0(), world.z0()));
        final List<List<Spot>> ways = new ArrayList<>(List.of(edge));
        final List<Feature> laid = new ArrayList<>(List.of(
                new Band("the land", new Terrains.Meadow(), new Hue("#8cbf5a"), -1 * M, 100 * M),
                new Lake("the sea and the bay", new Features.Bay(), new Hue("#2a6496"), new Path(edge), -3 * M)));
        lakes.forEach((name, loop) -> {
            final List<Spot> shore = new ArrayList<>(loop);
            shore.add(loop.getFirst());
            ways.add(shore);
            laid.add(new Lake(name, new Features.Lake(), new Hue("#4a8fc8"), new Path(shore), -0.5 * M));
        });
        return new LandFeatures(found.name(), made, map(ways), laid);
    }

    /** The map whose lines join each of {@code ways}' spots to the next, straight. */
    private Graph<Spot, Course> map(final List<List<Spot>> ways) {
        final Set<Spot> spots = new LinkedHashSet<>();
        final List<Edge<Spot, Course>> joined = new ArrayList<>();
        for (final List<Spot> way : ways) {
            spots.addAll(way);
            for (int i = 1; i < way.size(); i++) {
                joined.add(new Directed<>(way.get(i - 1), way.get(i), new Course.Straight()));
            }
        }
        return new Graph<>(List.copyOf(spots), joined);
    }
}
