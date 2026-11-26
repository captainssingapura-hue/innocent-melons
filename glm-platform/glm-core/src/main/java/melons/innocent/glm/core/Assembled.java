package melons.innocent.glm.core;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A platform put together: where each part's own frame stands in the platform's; where parts meet again by a
 * connection not walked, how far apart their points are; and the parts not reached from the root.
 *
 * @param placed    where each part placed stands
 * @param gaps      where parts meet again, and how far apart
 * @param unreached what was not reached
 */
public record Assembled(Map<PartAt, Vec3> placed, List<Gap> gaps, List<PartAt> unreached) {

    /** Where parts meet again by a connection not walked, and how far apart their points are, in centimetres. */
    public record Gap(String connection, double apart) {
    }

    public Assembled {
        placed = Map.copyOf(placed);
        gaps = List.copyOf(gaps);
        unreached = List.copyOf(unreached);
    }

    /** Where {@code part} stands; refused if it was not placed. */
    public Vec3 at(final PartAt part) {
        final Vec3 at = placed.get(part);
        if (at == null) {
            throw new IllegalArgumentException(part.said() + " was not placed");
        }
        return at;
    }

    /**
     * What is wrong with it, put together: a part not reached, or parts meeting again more than {@code within} apart.
     */
    public List<String> problems(final double within) {
        final List<String> wrong = new ArrayList<>();
        unreached.forEach(p -> wrong.add(p.said() + " is not reached from the root"));
        gaps.stream().filter(g -> g.apart() > within)
                .forEach(g -> wrong.add(String.format(java.util.Locale.ROOT, "%s: %.1f cm apart", g.connection(),
                        g.apart())));
        return wrong;
    }
}
