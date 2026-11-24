package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.Finish;
import com.example.fxsuite.existence.Elements;

/**
 * A building finished outside as {@code finish} says: its windows' frames and glass their colours; all else of it
 * painted, if it is to be, but its roof, which is waterproofed and seen by none, and its doors, timber as they are;
 * and what is already coloured as it is, its cores and its stairs, left so.
 *
 * @param finish how it is finished
 */
record Finished(Finish finish) {

    /** What its walls are of, filled in as it says. */
    Material infill() {
        return switch (finish.walling()) {
            case BRICK -> Material.BRICK;
            case PRECAST -> Material.PRECAST;
            case CONCRETE -> Material.CONCRETE;
            case STONE -> Material.STONE;
        };
    }

    /** {@code structure} finished. */
    Structure of(final Structure structure) {
        return new Structure(structure.parts().stream().map(p -> new Structure.Part(p.element(), of(p)))
                .toList(), structure.shape(), structure.rooms(), structure.waysIn(), structure.stairs());
    }

    /** {@code part}'s solid finished. */
    private Solid of(final Structure.Part part) {
        final Solid solid = part.solid();
        if (solid.tint().isPresent()) {
            return solid;
        }
        if (part.element().equals(new Elements.Window())) {
            return switch (solid.material()) {
                case GLASS -> solid.tinted(finish.glass());
                case METAL -> solid.tinted(finish.frames());
                default -> solid;
            };
        }
        if (part.element().equals(new Elements.Roof()) || part.element().equals(new Elements.Door())) {
            return solid;
        }
        return finish.paint().map(solid::tinted).orElse(solid);
    }
}
