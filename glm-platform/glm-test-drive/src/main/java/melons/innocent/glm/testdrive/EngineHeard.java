package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.Vehicle;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.enginebench.EngineSound;
import melons.innocent.glm.enginebench.EngineVoices;

import java.util.Optional;

/**
 * The engine in the car heard as it is test driven: the engine in its bay in its own voice, turbocharged, its turning
 * parts titanium ({@link EngineVoices}), at the revs the car's speed and its gearbox make ({@link AutoBox}), as open
 * as the throttle and working as hard; on the speakers as the engine bench plays it, or kept quiet. No engine in its
 * bay, nothing is heard.
 */
final class EngineHeard implements AutoCloseable {

    /** How loud it is played. */
    private static final double LOUD = 0.8;
    /** A centimetre a second, in metres a second. */
    private static final double METRES = 0.01;

    private final Optional<EngineSound> sound;
    private final AutoBox box;

    /** {@code engine} heard, or none; played, or kept {@code quiet}. */
    EngineHeard(final Optional<EngineFigures> engine, final boolean quiet) {
        final Engine voice = engine.map(e -> new EngineVoices().voiced(e, Material.TITANIUM, true))
                .orElse(Engine.V6);
        this.box = new AutoBox(Vehicle.SALOON.with(voice));
        this.sound = engine.map(e -> new EngineSound(voice, quiet));
        sound.ifPresent(s -> s.volume(LOUD));
    }

    /** Driven at {@code ahead} centimetres a second, the pedal at {@code pedal}, ahead positive. */
    void driven(final double ahead, final double pedal) {
        final double throttle = Math.max(0, pedal);
        box.step(ahead * METRES, throttle);
        sound.ifPresent(s -> s.set(box.rpm(), throttle, throttle));
    }

    /** How fast the engine turns. */
    double rpm() {
        return box.rpm();
    }

    /** The gear it is in, 1 the first. */
    int gear() {
        return box.gear();
    }

    @Override
    public void close() {
        sound.ifPresent(EngineSound::close);
    }
}
