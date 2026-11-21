package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * Something done to other steps: what is done, what it is done to, in order, and how many numbers come of it. The
 * last is worked out from the first two when a step is made, and kept, so asking it of a long expression does not
 * go down the whole of it again. Working it out and folding it both go through each step once, however many places
 * a step is used in ({@link Reckoning}, {@link Folding}).
 */
public record Applied(Op op, List<Node> of, int size) implements Node {

    public Applied {
        of = List.copyOf(of);
        // asked now, so a thing done to the wrong shapes is refused where it is written, not where it is drawn
        if (size != op.size(of.stream().map(Node::size).toList())) {
            throw new IllegalArgumentException(op + " done to these does not come to " + size + " numbers");
        }
    }

    /** {@code op} done to {@code of}. */
    public Applied(final Op op, final List<Node> of) {
        this(op, of, op.size(of.stream().map(Node::size).toList()));
    }

    @Override
    public double[] worked(final Sample at) {
        return new Reckoning(this).at(at);
    }

    @Override
    public String written(final Writing into) {
        return op.written(of.stream().map(into::named).toList());
    }

    @Override
    public Node folded() {
        return new Folding().of(this);
    }
}
