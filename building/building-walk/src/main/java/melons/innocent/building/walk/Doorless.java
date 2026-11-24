package melons.innocent.building.walk;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Elements;

import java.util.List;

/**
 * A building with its doors left out, as a walk has it: neither drawn nor in the way. No door opens yet, so in a
 * walk every doorway stands open, or nothing behind a door could be walked into; and a door drawn shut would hide
 * the avatar from the eye that follows it in. All else is as the building says: its solids, and its own shape, what
 * of it is in the way, stood on and walked into.
 *
 * @param structure the building
 */
public record Doorless(Structure structure) {

    /** What of it is in the way: its own shape, but for what stands for its doors. */
    public List<Volume> shape() {
        final List<Volume> doors = StandIns.prisms(structure.solids(new Elements.Door()));
        return structure.shape().stream().filter(volume -> !doors.contains(volume)).toList();
    }

    /** What of it is drawn: its solids, but for its doors. */
    public List<Solid> solids() {
        return structure.parts().stream().filter(part -> !(part.element() instanceof Elements.Door))
                .map(Structure.Part::solid).toList();
    }
}
