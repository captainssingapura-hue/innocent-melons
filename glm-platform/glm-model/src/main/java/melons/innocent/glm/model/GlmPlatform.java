package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * The GLM platform, whole: its body, cast as one piece, its root; the rear axle fixed under it behind and the front
 * axle ahead; and, where it has one, the engine in its bay, on its mounts on the front subframe. Made to a car's
 * figures and the body's own, the body the part that makes it one car or another.
 *
 * @param proportions the car's figures
 * @param figures     the body's own
 * @param bay         the engine in its bay; or none, null, the platform bare
 */
public record GlmPlatform(Proportions proportions, BodyFigures figures, EngineBay bay) {

    /** A large SUV's, its V6 in it, covered. */
    public GlmPlatform() {
        this(new Proportions(), new BodyFigures(), new EngineBay());
    }

    /** Made to {@code proportions} and {@code figures}, bare: no engine in it. */
    public GlmPlatform(final Proportions proportions, final BodyFigures figures) {
        this(proportions, figures, null);
    }

    /** It, as a platform to be put together. */
    public Platform platform() {
        final FrontPoints front = new FrontPoints(proportions);
        final Part body = new CastBody(new BodyLines(proportions, figures, front, new RearPoints(proportions))).part();
        final List<Connection> joined = new ArrayList<>(new RearAxle(proportions).joined(body));
        joined.addAll(new FrontAxle(proportions).joined(body));
        if (bay != null) {
            final Part strut = joined.stream().map(Connection::carried).filter(p -> p.has("engine mount"))
                    .findFirst().orElseThrow();
            joined.addAll(bay.joined(strut, front));
        }
        return new Platform("the GLM platform", body, joined);
    }
}
