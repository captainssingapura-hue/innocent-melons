package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;
import melons.innocent.vehiclelab.wheels.RimDesign;

import java.util.ArrayList;
import java.util.List;

/**
 * The GLM platform, whole: its body, its root, cast as one piece, or, where a car brings its own, that car's body,
 * every mount in the same place; the rear axle fixed under it behind and the front axle ahead; where it has one, the
 * engine in its bay, on its mounts on the front subframe; and the car's own body's panels on it. Made to a car's
 * figures and the body's own, the body the part that makes it one car or another.
 *
 * @param proportions the car's figures
 * @param figures     the body's own
 * @param bay         the engine in its bay; or none, null, the platform bare
 * @param bodywork    the car's own body; or none, null, the cast body
 */
public record GlmPlatform(Proportions proportions, BodyFigures figures, EngineBay bay, Bodywork bodywork) {

    /** A large SUV's, its V6 in it, covered. */
    public GlmPlatform() {
        this(new Proportions(), new BodyFigures(), new EngineBay());
    }

    /** Made to {@code proportions} and {@code figures}, bare: no engine in it. */
    public GlmPlatform(final Proportions proportions, final BodyFigures figures) {
        this(proportions, figures, null);
    }

    /** Made to {@code proportions} and {@code figures}, {@code bay} its engine, no body panels on it. */
    public GlmPlatform(final Proportions proportions, final BodyFigures figures, final EngineBay bay) {
        this(proportions, figures, bay, null);
    }

    /** It, as a platform to be put together. */
    public Platform platform() {
        final FrontPoints front = new FrontPoints(proportions);
        final Part body = bodywork == null ? new CastBody(new BodyLines(proportions, figures, front,
                new RearPoints(proportions))).part() : bodywork.root(proportions);
        final RimDesign rim = bodywork == null ? RimDesign.SPOKED : bodywork.rim();
        final List<Connection> joined = new ArrayList<>(new RearAxle(proportions, rim).joined(body));
        joined.addAll(new FrontAxle(proportions, rim).joined(body));
        if (bay != null) {
            final Part strut = joined.stream().map(Connection::carried).filter(p -> p.has("engine mount"))
                    .findFirst().orElseThrow();
            joined.addAll(bay.joined(strut, front));
        }
        if (bodywork != null) {
            joined.addAll(bodywork.joined(body));
        }
        return new Platform("the GLM platform", body, joined);
    }
}
