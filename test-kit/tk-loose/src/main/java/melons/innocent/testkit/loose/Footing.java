package melons.innocent.testkit.loose;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.texture.materials.Materials;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.surface.Facts;
import melons.innocent.core.surface.Passage;
import melons.innocent.core.surface.Patch;
import melons.innocent.core.surface.Underfoot;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Movers;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * What whoever walks the trial is standing on, asked as any mover asks it: of the trial's world at rest, whose
 * things are the providers. Each thing's surface is of the lab's material the thing is made of, the ground's of
 * turf; every surface is dry but the ramp's, which is in whatever state it has been put. It is asked, and said in
 * words; nothing yet moves otherwise for the answer.
 */
final class Footing implements Facts {

    /** How far under the feet a surface may be and still be what is stood on, in centimetres. */
    private static final double REACH = 60;

    private final Trial trial;
    private Node ramp = new Conditions.Dry();

    /** What is underfoot in {@code trial}. */
    Footing(final Trial trial) {
        this.trial = trial;
    }

    /** The states a surface may be put in, in the order the window lists them. */
    List<Node> conditions() {
        return List.of(new Conditions.Dry(), new Conditions.Wet(), new Conditions.Icy(), new Conditions.Loose(),
                new Conditions.Flooded());
    }

    /** The ramp's surface put in the state {@code condition}, from now on. */
    void conditioned(final Node condition) {
        ramp = condition;
    }

    @Override
    public Node material(final String provider, final Spot spot) {
        return Optional.ofNullable(trial.made().get(provider)).<Node>map(Stuff::material)
                .orElseGet(Materials.Turf::new);
    }

    @Override
    public Node condition(final String provider, final Spot spot) {
        return provider.equals(Scenes.FIXED) ? ramp : new Conditions.Dry();
    }

    /** The surface under feet at {@code feet}, in its provider's frame, with that frame; none, in the air. */
    Optional<Underfoot.Found> under(final Spot feet) {
        return new Underfoot(trial.world(), new Ground(), this).under(feet, REACH);
    }

    /** What is under feet at {@code feet}, in words: who provides it, its form, material and state, and its edge. */
    String said(final Optional<Spot> feet) {
        return feet.flatMap(this::under).map(found -> {
            final Patch patch = found.patch();
            final String form = patch.steep() < 0.5 ? patch.form().name()
                    : String.format(Locale.ENGLISH, "a %s of %.0f degrees", patch.form().name(), patch.steep());
            final String edge = Double.isInfinite(patch.within()) ? "no edge"
                    : String.format(Locale.ENGLISH, "%.0f cm to its edge", patch.within());
            return String.format(Locale.ENGLISH, "Underfoot: %s; %s, %s, %s; %s; a walker goes on it: %s",
                    patch.provider(), form, patch.material().name().replace('-', ' '), patch.condition().name(),
                    edge, new Passage(new Movers.Walker(), patch.form()).how());
        }).orElse("Underfoot: nothing within reach");
    }
}
