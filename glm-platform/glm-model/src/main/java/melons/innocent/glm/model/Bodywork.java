package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;

import java.util.List;

/**
 * A car's own body on the platform, built as the car's is: the root everything else is fixed to, every mount the
 * chassis is fixed by on it where the platform puts them; and its panels, each a part of its own, as they are fixed
 * to it.
 */
public interface Bodywork {

    /** Its root, made to the platform's {@code proportions}. */
    Part root(Proportions proportions);

    /** Its panels, as each is fixed to {@code root} or to one another. */
    List<Connection> joined(Part root);
}
