package com.example.fxsuite.vulkan.term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * An expression made ready to be worked out by Java many times over, at one sample after another: its steps set out
 * once, in the order they have to be done, the same thing done to the same things set out once. Each working then
 * goes down the steps, as the shader's code goes down its lines, and does nothing twice. It comes to exactly what
 * {@link Node#worked} comes to, sooner.
 */
public final class Reckoning {

    /** A thing done, told from another by what is done and the places of what it is done to: not by going down it. */
    private record Done(Op op, List<Integer> of) {
    }

    private final List<Node> steps = new ArrayList<>();
    private final List<int[]> made = new ArrayList<>();
    private final Map<Node, Integer> met = new IdentityHashMap<>();
    private final Map<Object, Integer> places = new HashMap<>();
    private final int last;

    /** {@code node}, made ready. */
    public Reckoning(final Node node) {
        last = placed(node);
    }

    /** What the expression comes to where what is given is as {@code sample} says. */
    public double[] at(final Sample sample) {
        final double[][] worked = new double[steps.size()][];
        for (int place = 0; place < worked.length; place++) {
            final Node step = steps.get(place);
            if (step instanceof Applied applied) {
                final List<double[]> of = new ArrayList<>(made.get(place).length);
                for (final int from : made.get(place)) {
                    of.add(worked[from]);
                }
                worked[place] = applied.op().worked(of);
            } else {
                worked[place] = step.worked(sample);
            }
        }
        return worked[last];
    }

    /** Where {@code node} is among the steps, set out now if it was not, what it is made of set out before it. */
    private int placed(final Node node) {
        final Integer known = met.get(node);
        if (known != null) {
            return known;
        }
        final int[] from = node instanceof Applied applied
                ? applied.of().stream().mapToInt(this::placed).toArray() : new int[0];
        final Object same = node instanceof Applied applied
                ? new Done(applied.op(), Arrays.stream(from).boxed().toList()) : node;
        final Integer place = places.get(same);
        if (place != null) {
            met.put(node, place);
            return place;
        }
        steps.add(node);
        made.add(from);
        places.put(same, steps.size() - 1);
        met.put(node, steps.size() - 1);
        return steps.size() - 1;
    }
}
