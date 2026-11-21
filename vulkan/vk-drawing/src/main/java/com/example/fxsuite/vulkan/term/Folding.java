package com.example.fxsuite.vulkan.term;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * An expression having whatever of it is made of constants alone worked out already. Each step is folded once,
 * however many other steps are made of it, and a step nothing of which changed is kept as the very step it was: so an
 * expression that uses one thing in many places is no bigger folded than it was, and no slower to fold.
 */
final class Folding {

    private final Map<Node, Node> folded = new IdentityHashMap<>();

    /** {@code node}, folded. */
    Node of(final Node node) {
        if (!(node instanceof Applied applied)) {
            return node;
        }
        final Node known = folded.get(node);
        if (known != null) {
            return known;
        }
        final List<Node> of = applied.of().stream().map(this::of).toList();
        final Node made = made(applied, of);
        folded.put(node, made);
        return made;
    }

    private Node made(final Applied applied, final List<Node> of) {
        if (of.stream().allMatch(Constant.class::isInstance)) {
            return new Constant(applied.op().worked(of.stream().map(each -> each.worked(new Sample())).toList()));
        }
        for (int which = 0; which < of.size(); which++) {
            if (of.get(which) != applied.of().get(which)) {
                return new Applied(applied.op(), of);
            }
        }
        return applied;
    }
}
