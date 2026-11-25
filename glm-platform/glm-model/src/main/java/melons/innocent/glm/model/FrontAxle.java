package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;

import java.util.List;

/**
 * The GLM platform's front axle, so far as it is made: the underbody's front, and the front subframe fixed to it,
 * bushed behind and bolted ahead. Its links, its carriers and its wheels to come. Said as on the right, the left its
 * mirror.
 *
 * @param proportions the car's figures
 */
public record FrontAxle(Proportions proportions) {

    /** A large SUV's. */
    public FrontAxle() {
        this(new Proportions());
    }

    /** It, as a platform to be put together, the underbody's front its root. */
    public Platform platform() {
        final Part body = body();
        return new Platform("the GLM platform's front axle", body, joined(body));
    }

    /** The underbody's front. */
    Part body() {
        return new FrontBody(new FrontPoints(proportions)).part();
    }

    /** How its parts are joined to {@code body}, in the order they are walked. */
    List<Connection> joined(final Part body) {
        return new FrontSubframe(new FrontPoints(proportions)).joined(body);
    }
}
