package melons.innocent.terrain;

import com.example.fxsuite.taxonomy.Leaf;

/**
 * A feature lying within a {@code boundary} of the map's lines, closed round: its last spot its first. A wood, a
 * village's fields. Within is told by how many times a line from a spot out to the east crosses the boundary.
 *
 * @param label    its label
 * @param kind     the kind of thing it is
 * @param hue      the colour it is shown in
 * @param boundary the lines round it, ending where they begin
 */
public record Area(String label, Leaf<?> kind, Hue hue, Path boundary) implements Feature {

    public Area {
        if (!boundary.spots().getFirst().equals(boundary.spots().getLast()) || boundary.spots().size() < 4) {
            throw new IllegalArgumentException(label + ": an area closed round by three lines at least");
        }
    }

    @Override
    public Coverage coverage(final Terrain terrain) {
        final Traced traced = new Traced(boundary, terrain.map());
        return at -> traced.inside(at.x(), at.z());
    }
}
