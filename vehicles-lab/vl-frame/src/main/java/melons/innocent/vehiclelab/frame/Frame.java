package melons.innocent.vehiclelab.frame;

import java.util.ArrayList;
import java.util.List;

/**
 * Anything built of rigid tubes joined at their ends, as a graph: a motorcycle's frame first. Pure figures, in its
 * owner's own frame: {@code across} to its right, {@code along} ahead, {@code up} from the ground, in centimetres.
 * Its nodes are where its rods meet, each named and of a {@link Kind}; each rod runs straight from one node to
 * another and is a {@link Tube}, its own or the frame's. From these come its length of tube, its mass and where its
 * mass is, which rods meet at a node, whether it is one piece, how much it leans on its welds to keep its shape
 * ({@link #slack}), and what is wrong with it ({@link #problems}).
 *
 * @param name  what it is
 * @param nodes where its rods meet
 * @param rods  its rods
 * @param tube  the tube its rods are, unless a rod has its own
 */
public record Frame(String name, List<Node> nodes, List<Rod> rods, Tube tube) {

    /** Steel, in kilograms a cubic centimetre. */
    public static final double STEEL = 0.00785;

    /** What a node is: rods welded together there; bolted to what carries it; or a pivot. */
    public enum Kind { WELD, MOUNT, HINGE }

    /** A point of the frame, named. */
    public record Node(String name, double across, double along, double up, Kind kind) {

        /** One welded. */
        public Node(final String name, final double across, final double along, final double up) {
            this(name, across, along, up, Kind.WELD);
        }

        /** How far it is from {@code other}. */
        public double distance(final Node other) {
            return Math.sqrt(Math.pow(across - other.across, 2) + Math.pow(along - other.along, 2)
                    + Math.pow(up - other.up, 2));
        }

        /** The same, on the other side of the middle. */
        public Node mirrored() {
            return new Node(name, -across, along, up, kind);
        }
    }

    /** A round tube: its outside radius and its wall's thickness, in centimetres. */
    public record Tube(double radius, double wall) {

        public Tube {
            if (!(radius > 0) || !(wall > 0) || !(wall < radius)) {
                throw new IllegalArgumentException("a tube is thicker than its wall: " + radius + ", " + wall);
            }
        }

        /** How much steel a centimetre of it is, in square centimetres. */
        public double area() {
            final double inside = radius - wall;
            return Math.PI * (radius * radius - inside * inside);
        }
    }

    /** A rod from node {@code from} to node {@code to}, by their places among the nodes; its own tube, or null. */
    public record Rod(int from, int to, Tube tube) {

        /** One of the frame's tube. */
        public Rod(final int from, final int to) {
            this(from, to, null);
        }
    }

    /** A point, as a node is, without a name: where its mass is, say. */
    public record Point(double across, double along, double up) {
    }

    public Frame {
        nodes = List.copyOf(nodes);
        rods = List.copyOf(rods);
        if (tube == null) {
            throw new IllegalArgumentException(name + ": a tube for its rods");
        }
        for (final Rod rod : rods) {
            if (rod.from() < 0 || rod.to() < 0 || rod.from() >= nodes.size() || rod.to() >= nodes.size()) {
                throw new IllegalArgumentException(name + ": a rod to a node there is not: " + rod);
            }
        }
    }

    /** The tube {@code rod} is: its own, or the frame's. */
    public Tube tube(final Rod rod) {
        return rod.tube() != null ? rod.tube() : tube;
    }

    /** How long {@code rod} is. */
    public double length(final Rod rod) {
        return nodes.get(rod.from()).distance(nodes.get(rod.to()));
    }

    /** How much tube it takes, all told, in centimetres. */
    public double steel() {
        return rods.stream().mapToDouble(this::length).sum();
    }

    /** How heavy its rods are, all told, in kilograms. */
    public double mass() {
        return rods.stream().mapToDouble(this::mass).sum();
    }

    private double mass(final Rod rod) {
        return length(rod) * tube(rod).area() * STEEL;
    }

    /** Where its mass is: each rod's at its middle, as heavy as it is. */
    public Point centre() {
        double mass = 0;
        double across = 0;
        double along = 0;
        double up = 0;
        for (final Rod rod : rods) {
            final double m = mass(rod);
            final Node a = nodes.get(rod.from());
            final Node b = nodes.get(rod.to());
            mass += m;
            across += m * (a.across() + b.across()) / 2;
            along += m * (a.along() + b.along()) / 2;
            up += m * (a.up() + b.up()) / 2;
        }
        return mass == 0 ? new Point(0, 0, 0) : new Point(across / mass, along / mass, up / mass);
    }

    /** The rods that meet at node {@code node}, by their places among the rods. */
    public List<Integer> rodsAt(final int node) {
        final List<Integer> at = new ArrayList<>();
        for (int r = 0; r < rods.size(); r++) {
            if (rods.get(r).from() == node || rods.get(r).to() == node) {
                at.add(r);
            }
        }
        return at;
    }

    /** The place among the nodes of the one named {@code named}; -1 if none is. */
    public int node(final String named) {
        for (int n = 0; n < nodes.size(); n++) {
            if (nodes.get(n).name().equals(named)) {
                return n;
            }
        }
        return -1;
    }

    /** Whether every node is joined to every other through the rods: one piece, not loose ones. */
    public boolean connected() {
        if (nodes.isEmpty()) {
            return true;
        }
        final boolean[] reached = new boolean[nodes.size()];
        final List<Integer> next = new ArrayList<>(List.of(0));
        reached[0] = true;
        while (!next.isEmpty()) {
            final int at = next.removeLast();
            for (final Rod rod : rods) {
                final int other = rod.from() == at ? rod.to() : rod.to() == at ? rod.from() : -1;
                if (other >= 0 && !reached[other]) {
                    reached[other] = true;
                    next.add(other);
                }
            }
        }
        for (final boolean r : reached) {
            if (!r) {
                return false;
            }
        }
        return true;
    }

    /**
     * How many ways it could still move if every node were a pin rather than a weld, by Maxwell's count: each node
     * free to go three ways, but those bolted down, each rod taking one away, and, if nothing holds it, the six ways
     * it moves as a whole besides. Above nought, it keeps its shape only because its welds hold.
     */
    public int slack() {
        final long held = nodes.stream().filter(n -> n.kind() == Kind.MOUNT).count();
        final long free = nodes.size() - held;
        final int whole = held == 0 ? 6 : 0;
        return (int) Math.max(0, 3 * free - rods.size() - whole);
    }

    /**
     * What is wrong with it, if anything: a rod from a node to itself, two rods alike, a node no rod meets, two
     * nodes of one name, or in pieces.
     */
    public List<String> problems() {
        final List<String> wrong = new ArrayList<>();
        for (int r = 0; r < rods.size(); r++) {
            final Rod rod = rods.get(r);
            if (rod.from() == rod.to()) {
                wrong.add("a rod from " + nodes.get(rod.from()).name() + " to itself");
            }
            for (int s = r + 1; s < rods.size(); s++) {
                final Rod other = rods.get(s);
                if (Math.min(rod.from(), rod.to()) == Math.min(other.from(), other.to())
                        && Math.max(rod.from(), rod.to()) == Math.max(other.from(), other.to())) {
                    wrong.add("two rods between " + nodes.get(rod.from()).name() + " and "
                            + nodes.get(rod.to()).name());
                }
            }
        }
        for (int n = 0; n < nodes.size(); n++) {
            if (rodsAt(n).isEmpty()) {
                wrong.add("no rod meets " + nodes.get(n).name());
            }
            for (int m = n + 1; m < nodes.size(); m++) {
                if (nodes.get(n).name().equals(nodes.get(m).name())) {
                    wrong.add("two nodes named " + nodes.get(n).name());
                }
            }
        }
        if (!connected()) {
            wrong.add("in pieces: not every node is joined to the rest");
        }
        return wrong;
    }
}
