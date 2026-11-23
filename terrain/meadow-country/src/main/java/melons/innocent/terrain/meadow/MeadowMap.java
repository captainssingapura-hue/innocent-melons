package melons.innocent.terrain.meadow;

import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.Contour;
import melons.innocent.terrain.Course;
import melons.innocent.terrain.Spot;
import melons.innocent.terrain.Topography;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The meadow's map of lines, what its land features lie along: the river's course, from the world's west edge to its
 * east, its middle two gentle waves laid over each other, a spot every 20 m; and the pond's shore, the hollow's
 * contour, closed round. Straight from spot to spot.
 */
public record MeadowMap() implements StatelessFunctionalObject {

    public static final MeadowMap INSTANCE = new MeadowMap();

    /** How far apart the river's spots are, in centimetres. */
    private static final double APART = 2_000;
    /** How high the hollow's contour is, round the pond. */
    private static final double HOLLOW = 400;

    /** The lines, joining the spots of the river's course and of the pond's shore of {@code ground}. */
    public Graph<Spot, Course> map(final Topography ground) {
        final Set<Spot> spots = new LinkedHashSet<>();
        final List<Edge<Spot, Course>> lines = new ArrayList<>();
        joined(river(ground), spots, lines);
        joined(pond(ground), spots, lines);
        return new Graph<>(List.copyOf(spots), lines);
    }

    /** The river's course, west to east across {@code ground}'s extent. */
    public List<Spot> river(final Topography ground) {
        final List<Spot> course = new ArrayList<>();
        for (double x = ground.extent().x0(); x <= ground.extent().x1(); x += APART) {
            course.add(new Spot(x, riverAt(x)));
        }
        return List.copyOf(course);
    }

    /** The pond's shore: {@code ground}'s hollow's contour, its first spot again at its end. */
    public List<Spot> pond(final Topography ground) {
        final Contour hollow = ground.contours().stream().filter(contour -> contour.height() == HOLLOW).findFirst()
                .orElseThrow(() -> new IllegalStateException("the meadow has no hollow at 4 m"));
        final List<Spot> shore = new ArrayList<>(hollow.loop().spots());
        shore.add(shore.getFirst());
        return List.copyOf(shore);
    }

    /** Where the river's middle is, along, at {@code x} across: two gentle waves laid over each other. */
    public double riverAt(final double x) {
        return 4_000 * Math.sin(x / 18_000 + 0.3) + 1_500 * Math.sin(x / 7_000 + 1.3) - 6_600;
    }

    /** {@code way}'s spots added to {@code spots}, each joined to the next by a straight line in {@code lines}. */
    private void joined(final List<Spot> way, final Set<Spot> spots, final List<Edge<Spot, Course>> lines) {
        spots.addAll(way);
        for (int i = 1; i < way.size(); i++) {
            lines.add(new Directed<>(way.get(i - 1), way.get(i), new Course.Straight()));
        }
    }
}
