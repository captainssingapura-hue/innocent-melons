package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.road.surface.Section;
import melons.innocent.road.surface.Stretch;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * A straight bridge, {@code length} long, its line along, its middle at nought, standing on {@code ground}: a deck of
 * concrete {@code depth} deep, as wide as the road it carries and a parapet {@link #PARAPET} wide either side, the
 * road's surface on it ({@link Stretch}), {@code level} high; a parapet along each edge, {@link #HIGH} over the deck;
 * at each of {@code piers}, so far along it, a cap of concrete across under the deck, on one wide column under its
 * middle or two under its sides ({@link Piers}), each column standing from the ground under it up to the cap, as tall
 * as the ground there leaves it; at each end an abutment across under the deck, from the ground at its lowest there up
 * to the deck. On level ground every column alike; on ground that is not, each its own height.
 *
 * @param section what it carries across
 * @param length  how long it is
 * @param level   how high its deck's top is, the road laid on it
 * @param depth   how deep its deck is
 * @param piers   how far along it each pier stands
 * @param kind    how its piers stand
 * @param ground  what it stands on
 */
public record Bridge(Section section, double length, double level, double depth, List<Double> piers, Piers kind,
                     Ground ground) {

    /** How wide a parapet is, and how high over the deck; how long a piece of the deck is. */
    static final double PARAPET = 50;
    static final double HIGH = 110;
    static final double PIECE = 1_000;
    /** A pier's cap: how deep, how long along, how far in from the deck's edges it stops. */
    static final double CAP_DEEP = 150;
    static final double CAP_ALONG = 250;
    static final double CAP_IN = 100;
    /** A single column, across and along; twin columns, square, and how far in from the deck's edges they stand. */
    static final double SINGLE_ACROSS = 300;
    static final double SINGLE_ALONG = 200;
    static final double TWIN = 160;
    static final double TWIN_IN = 300;
    /** How thick an abutment is, along. */
    static final double ABUTMENT = 150;

    public Bridge {
        piers = List.copyOf(piers);
        if (!(length > 0) || !(depth > 0) || kind == null || ground == null) {
            throw new IllegalArgumentException("a bridge some way long and deep, its piers of some kind, on ground");
        }
    }

    /** How far its deck reaches from its line either way, its parapets with it. */
    public double half() {
        return section.wide() / 2 + PARAPET;
    }

    /** It as a thing built: its road come onto at its far end, on its deck. */
    public Structure structure() {
        final List<Part> parts = new ArrayList<>(new Stretch(section, length, level).parts());
        for (double z = -length / 2; z < length / 2 - 1e-6; z += PIECE) {
            final double z1 = Math.min(z + PIECE, length / 2);
            parts.add(part(new Elements.Decking(), "deck", -half(), half(), z, z1, level - depth, level));
            parts.add(part(new Elements.Parapet(), "parapet", -half(), -half() + PARAPET, z, z1, level,
                    level + HIGH));
            parts.add(part(new Elements.Parapet(), "parapet", half() - PARAPET, half(), z, z1, level, level + HIGH));
        }
        piers.forEach(z -> parts.addAll(pier(z)));
        parts.add(abutment(length / 2 - ABUTMENT, length / 2));
        parts.add(abutment(-length / 2, -length / 2 + ABUTMENT));
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(),
                new Stretch(section, length, level).structure().waysIn());
    }

    /** The pier {@code z} along: its cap, and its column or columns down to the ground. */
    private List<Part> pier(final double z) {
        final double under = level - depth;
        final double cap = under - CAP_DEEP;
        final List<Part> parts = new ArrayList<>();
        parts.add(part(new Elements.Beam(), "pier cap", -half() + CAP_IN, half() - CAP_IN, z - CAP_ALONG / 2,
                z + CAP_ALONG / 2, cap, under));
        if (kind == Piers.SINGLE) {
            parts.add(column(0, SINGLE_ACROSS, z, SINGLE_ALONG, cap));
        } else {
            final double x = half() - TWIN_IN;
            parts.add(column(-x, TWIN, z, TWIN, cap));
            parts.add(column(x, TWIN, z, TWIN, cap));
        }
        return parts;
    }

    /** A column {@code across} by {@code along} round ({@code x}, {@code z}), from the ground there to {@code top}. */
    private Part column(final double x, final double across, final double z, final double along, final double top) {
        return part(new Elements.Column(), "column", x - across / 2, x + across / 2, z - along / 2, z + along / 2,
                ground.height(x, z), top);
    }

    /** An abutment from {@code z0} to {@code z1} along, across under the deck, from the ground at its lowest to it. */
    private Part abutment(final double z0, final double z1) {
        final double z = (z0 + z1) / 2;
        final double foot = Math.min(ground.height(0, z), Math.min(ground.height(-half(), z),
                ground.height(half(), z)));
        return part(new Elements.Abutment(), "abutment", -half(), half(), z0, z1, foot, level - depth);
    }

    private Part part(final Leaf<?> element, final String name, final double x0, final double x1, final double z0,
                      final double z1, final double y0, final double y1) {
        return new Part(element, Timbers.INSTANCE.block(name, Role.STRUCTURE, Material.CONCRETE, x0, x1, z0, z1, y0,
                y1));
    }
}
