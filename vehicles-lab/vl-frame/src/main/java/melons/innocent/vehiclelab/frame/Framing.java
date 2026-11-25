package melons.innocent.vehiclelab.frame;

import java.util.ArrayList;
import java.util.List;

/**
 * A frame put together by name: its nodes said one at a time, or in pairs either side of its middle, and its rods
 * between nodes by their names, so a frame reads as it is built. A pair named {@code name} is two nodes, {@code name,
 * left} and {@code name, right}; a rod between two pairs is two rods, one each side.
 */
public final class Framing {

    private final String name;
    private final Frame.Tube tube;
    private final List<Frame.Node> nodes = new ArrayList<>();
    private final List<Frame.Rod> rods = new ArrayList<>();

    /** A frame called {@code name}, its rods {@code tube} unless said otherwise. */
    public Framing(final String name, final Frame.Tube tube) {
        this.name = name;
        this.tube = tube;
    }

    /** A node of its own, on its middle or wherever {@code across} has it. */
    public Framing node(final String named, final double across, final double along, final double up,
                        final Frame.Kind kind) {
        nodes.add(new Frame.Node(named, across, along, up, kind));
        return this;
    }

    /** The same, welded. */
    public Framing node(final String named, final double across, final double along, final double up) {
        return node(named, across, along, up, Frame.Kind.WELD);
    }

    /** A pair of nodes, {@code across} out either side of its middle. */
    public Framing pair(final String named, final double across, final double along, final double up,
                        final Frame.Kind kind) {
        node(named + ", left", -across, along, up, kind);
        return node(named + ", right", across, along, up, kind);
    }

    /** The same, welded. */
    public Framing pair(final String named, final double across, final double along, final double up) {
        return pair(named, across, along, up, Frame.Kind.WELD);
    }

    /** A rod from the node named {@code from} to the one named {@code to}, of {@code made}, or null for its tube. */
    public Framing rod(final String from, final String to, final Frame.Tube made) {
        rods.add(new Frame.Rod(place(from), place(to), made));
        return this;
    }

    /** The same, of its tube. */
    public Framing rod(final String from, final String to) {
        return rod(from, to, null);
    }

    /** A rod each side, from the pair {@code from} to the pair, or the node, {@code to}. */
    public Framing rods(final String from, final String to, final Frame.Tube made) {
        for (final String side : List.of(", left", ", right")) {
            rod(from + side, place(to + side) >= 0 ? to + side : to, made);
        }
        return this;
    }

    /** The same, of its tube. */
    public Framing rods(final String from, final String to) {
        return rods(from, to, null);
    }

    /** A rod across, from the pair {@code named}'s left to its right. */
    public Framing across(final String named, final Frame.Tube made) {
        return rod(named + ", left", named + ", right", made);
    }

    /** The same, of its tube. */
    public Framing across(final String named) {
        return across(named, null);
    }

    /** The frame. */
    public Frame framed() {
        return new Frame(name, nodes, rods, tube);
    }

    /** The place among its nodes of the one named {@code named}; -1 if none is, refused where a rod wants it. */
    private int place(final String named) {
        for (int n = 0; n < nodes.size(); n++) {
            if (nodes.get(n).name().equals(named)) {
                return n;
            }
        }
        return -1;
    }
}
