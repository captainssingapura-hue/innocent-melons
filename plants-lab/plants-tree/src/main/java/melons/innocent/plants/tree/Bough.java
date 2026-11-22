package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * One bough of a tree, from where it leaves the bough it forks from to where it forks itself or ends as a twig:
 * the points it runs through, how thick it is at each from its middle to its bark, how many forks it is from the
 * trunk (the trunk is none), and which bough it forks from, by that bough's place among the tree's boughs (the
 * trunk forks from none, which is said as -1). In the tree's own frame: up is the second number, the ground is
 * nothing high, and the tree's foot is the frame's middle; in centimetres.
 */
public record Bough(List<Vec3> points, List<Double> thick, int forks, int from) {

    public Bough {
        points = List.copyOf(points);
        thick = List.copyOf(thick);
        if (points.size() < 2 || points.size() != thick.size()) {
            throw new IllegalArgumentException("a bough runs through two points or more, each with its thickness: "
                    + points.size() + " points and " + thick.size() + " thicknesses");
        }
    }

    /** Where it leaves the bough it forks from. */
    public Vec3 begins() {
        return points.getFirst();
    }

    /** Where it forks, or ends. */
    public Vec3 ends() {
        return points.getLast();
    }

    /** How long it is, along itself. */
    public double length() {
        double length = 0;
        for (int point = 1; point < points.size(); point++) {
            length += points.get(point).minus(points.get(point - 1)).length();
        }
        return length;
    }
}
