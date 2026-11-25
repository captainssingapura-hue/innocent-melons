package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * The GLM platform, whole: its body, cast as one piece, its root; the rear axle fixed under it behind and the front
 * axle ahead. Made to a car's figures and the body's own, the body the part that makes it one car or another.
 *
 * @param proportions the car's figures
 * @param figures     the body's own
 */
public record GlmPlatform(Proportions proportions, BodyFigures figures) {

    /** A large SUV's. */
    public GlmPlatform() {
        this(new Proportions(), new BodyFigures());
    }

    /** It, as a platform to be put together. */
    public Platform platform() {
        final Part body = new CastBody(new BodyLines(proportions, figures, new FrontPoints(proportions),
                new RearPoints(proportions))).part();
        final List<Connection> joined = new ArrayList<>(new RearAxle(proportions).joined(body));
        joined.addAll(new FrontAxle(proportions).joined(body));
        return new Platform("the GLM platform", body, joined);
    }
}
