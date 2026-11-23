package com.example.fxsuite.life.body;

import java.util.Map;
import java.util.Optional;

/**
 * How deep each group of the body's parts is drawn ({@link BodyGroup}, {@link Layer}): as {@code chosen} says for the
 * groups it names; the rest as the whole is drawn, its bones, its muscles on them, or its skin; a robot's, as a robot.
 * So the raw skull may be seen on the body's skin, say.
 *
 * @param chosen the groups drawn otherwise than the whole, and how
 */
public record Layers(Map<BodyGroup, Layer> chosen) {

    /** How deep {@code p} is drawn, the whole drawn as {@code kind}; none, a robot's part. */
    public Optional<Layer> of(final Part p, final Puppet.Kind kind) {
        for (final Map.Entry<BodyGroup, Layer> e : chosen.entrySet()) {
            if (e.getKey().has(p)) {
                return Optional.of(e.getValue());
            }
        }
        return switch (kind) {
            case SKELETON -> Optional.of(Layer.SKELETON);
            case MUSCLES -> Optional.of(Layer.MUSCLES);
            case SKIN -> Optional.of(Layer.SKIN);
            case EXPOSED, SHELLED -> Optional.empty();
        };
    }
}
