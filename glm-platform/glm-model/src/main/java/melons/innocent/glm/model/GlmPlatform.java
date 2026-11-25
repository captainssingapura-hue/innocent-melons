package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * The GLM platform, both its axles as far as each is made: the rear axle, its underbody the root; the underbody's
 * front welded to it; and the front axle on that. Made to a car's figures.
 *
 * @param proportions the car's figures
 */
public record GlmPlatform(Proportions proportions) {

    /** A large SUV's. */
    public GlmPlatform() {
        this(new Proportions());
    }

    /** It, as a platform to be put together. */
    public Platform platform() {
        final Platform rear = new RearAxle(proportions).platform();
        final FrontAxle front = new FrontAxle(proportions);
        final Part body = front.body();
        final List<Connection> joined = new ArrayList<>(rear.connections());
        joined.add(new Connection("front underbody", rear.root(), "front underbody", body, "rest of the underbody",
                Joining.WELDED));
        joined.addAll(front.joined(body));
        return new Platform("the GLM platform", rear.root(), joined);
    }
}
