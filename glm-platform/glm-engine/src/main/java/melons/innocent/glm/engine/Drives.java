package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * What an engine drives round wheels: its timing chains, turning its cams from its crank ({@link TimingDrive}); and a
 * twin's belt, turning its back wheel from its gearbox ({@link BeltDrive}). A chain or a belt runs round its wheels
 * rather than moving whole, so it is drawn as its links, or its teeth, each where it has run to: a chain's as far as
 * the crank has turned, a belt's as far as the bike has rolled.
 */
public final class Drives {

    private final TimingDrive timing;
    private final Optional<BeltDrive> belt;

    /** Its parts, made to {@code figures}. */
    Drives(final EngineFigures figures) {
        this.timing = new TimingDrive(figures);
        this.belt = figures.twin() ? Optional.of(new BeltDrive(figures)) : Optional.empty();
    }

    /** How its parts are joined to {@code block} and {@code crank}, in the order they are walked. */
    List<Connection> joined(final Part block, final Part crank) {
        final List<Connection> joined = new ArrayList<>(timing.joined(block, crank));
        belt.ifPresent(drive -> joined.addAll(drive.joined(block)));
        return joined;
    }

    /** Whether {@code part} runs round its wheels: a timing chain or a belt. */
    public boolean wrapped(final Part part) {
        return timing.chain(part) || belt.filter(drive -> drive.belt(part)).isPresent();
    }

    /** Whether {@code part} turns as the bike rolls: the belt's pulleys and the belt. */
    public boolean rolling(final Part part) {
        return belt.filter(drive -> drive.has(part)).isPresent();
    }

    /**
     * Where {@code part}'s links or teeth are, the crank turned {@code turned} degrees on from rest and the bike rolled
     * {@code rolled} forward; none if it runs round no wheels.
     */
    public List<Mat4> links(final Part part, final double turned, final double rolled) {
        if (timing.chain(part)) {
            return timing.links(part, turned);
        }
        return belt.filter(drive -> drive.belt(part)).map(drive -> drive.teeth(rolled)).orElse(List.of());
    }

    /** One of {@code part}'s links or teeth, in its own frame. */
    public Mesh link(final Part part) {
        return timing.chain(part) ? timing.link(part) : belt.orElseThrow().tooth();
    }

    /** How far {@code part} has turned, the bike having rolled {@code rolled} forward. */
    public Mat4 rolled(final Part part, final double rolled) {
        return belt.map(drive -> drive.moved(part, rolled)).orElse(new Mat4());
    }

    /** How many parts it has. */
    int count() {
        return timing.count() + belt.map(BeltDrive::count).orElse(0);
    }
}
