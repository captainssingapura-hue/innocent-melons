package melons.innocent.building.generic;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A storey's cover, of {@code infill}: each run, from the storey's floor to the underside of the slab over it, as
 * its kind is. A windowed run a sill wall {@code sill} high, over it a window as wide as the run up to {@code head},
 * its glass in a frame of metal and parted by mullions no more than {@link #PANE} apart, and over the window a band of
 * wall up to the slab; a solid run wall all the way up; a parapet a wall {@code parapet} high and no more, an open edge
 * guarded. In centimetres.
 *
 * @param sill    how high a window's sill is over the floor
 * @param head    how high a window's head is over the floor
 * @param parapet how high a parapet is over the floor
 * @param infill  what its walls are of
 */
public record Covered(double sill, double head, double parapet, Material infill) {

    /** How wide a window's panes are, at most, its mullions between them; how thick its frame and its glass. */
    static final double PANE = 150;
    static final double FRAME = 5;
    static final double GLASS = 2;

    /** Sills at 90 cm, heads at 2.1 m, parapets 1 m, as flats commonly have them; its walls of {@code infill}. */
    public Covered(final Material infill) {
        this(90, 210, 100, infill);
    }

    /** Run {@code cover}, from {@code floor} to {@code ceiling}, the underside of the slab over it. */
    public List<Part> parts(final FloorPlan.Cover cover, final double floor, final double ceiling) {
        final Along along = new Along();
        final FloorPlan.Wall run = cover.wall();
        final List<Part> parts = new ArrayList<>();
        switch (cover.kind()) {
            case SOLID -> parts.add(new Part(new Elements.Wall(), along.whole(run.name(), Role.WALL, infill,
                    run, floor, ceiling)));
            case PARAPET -> parts.add(new Part(new Elements.Parapet(), along.whole(run.name(), Role.DRESSING,
                    infill, run, floor, floor + parapet)));
            case WINDOWED -> {
                parts.add(new Part(new Elements.Wall(), along.whole(run.name() + " under its window", Role.WALL,
                        infill, run, floor, floor + sill)));
                parts.add(new Part(new Elements.Wall(), along.whole(run.name() + " over its window", Role.WALL,
                        infill, run, floor + head, ceiling)));
                parts.addAll(window(along, run, floor + sill, floor + head));
            }
        }
        return parts;
    }

    /** A window as wide as {@code run}, from {@code low} to {@code high}: its frame, its mullions and its panes. */
    private List<Part> window(final Along along, final FloorPlan.Wall run, final double low, final double high) {
        final List<Part> parts = new ArrayList<>();
        final double length = run.length();
        final int panes = Math.max(1, (int) Math.ceil(length / PANE));
        parts.add(new Part(new Elements.Window(), along.box(run.name() + " window's sill", Role.DRESSING,
                Material.METAL, run, 0, length, 0, 2 * FRAME, low, low + FRAME)));
        parts.add(new Part(new Elements.Window(), along.box(run.name() + " window's head", Role.DRESSING,
                Material.METAL, run, 0, length, 0, 2 * FRAME, high - FRAME, high)));
        for (int i = 0; i <= panes; i++) {
            final double at = Math.clamp(i * length / panes, FRAME / 2, length - FRAME / 2);
            parts.add(new Part(new Elements.Window(), along.box(run.name() + " mullion", Role.DRESSING,
                    Material.METAL, run, at - FRAME / 2, at + FRAME / 2, 0, 2 * FRAME, low + FRAME, high - FRAME)));
        }
        for (int i = 0; i < panes; i++) {
            parts.add(new Part(new Elements.Window(), along.box(run.name() + " pane", Role.GLAZING, Material.GLASS,
                    run, i * length / panes + FRAME / 2, (i + 1) * length / panes - FRAME / 2, 0, GLASS,
                    low + FRAME, high - FRAME)));
        }
        return parts;
    }
}
