package melons.innocent.building.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * A slab block's cover, of its bays from one end to the other, so deep behind its corridor, its corridor so wide: in
 * lightweight walls between the pillars and the cores' walls ({@link Infill}), {@link #COVER} thick. Each bay windowed
 * across its back, and solid behind each core; a party wall on each line between a bay and its neighbour. Each home
 * along the access balcony windowed across its face on it, its door in that face, in the middle of it. Each corner home,
 * at an end, has the balcony's end too: its face out at the balcony's edge, windowed, and across the balcony a wall
 * facing along it, toward the block's middle, its door in the middle of that; its end wall, the block's, windowed. The
 * balcony between the corner homes guarded by a parapet along its edge. Every door {@link #DOOR} wide.
 *
 * @param bays     its bays, from one end to the other
 * @param deep     how deep it is behind its corridor
 * @param corridor how wide its corridor is
 */
record SlabCover(List<GenericSlabBlock.Bay> bays, double deep, double corridor) {

    static final double COVER = 10;
    static final double DOOR = 100;

    SlabCover {
        bays = List.copyOf(bays);
    }

    /** Its cover, between what is {@code standing}: the pillars and the cores' walls. */
    List<FloorPlan.Cover> of(final List<Box> standing) {
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        final List<Box> around = new ArrayList<>(standing);
        for (int k = 0; k < bays.size(); k++) {
            if (!bays.get(k).core()) {
                covers.add(door(k, around));
            }
        }
        final Infill infill = new Infill(around);
        final GenericSlabBlock.Bay first = bays.getFirst();
        final GenericSlabBlock.Bay last = bays.getLast();
        for (int k = 0; k < bays.size(); k++) {
            final GenericSlabBlock.Bay bay = bays.get(k);
            covers.addAll(side(infill, bay.x0(), -deep, bay.x1(), -deep, bay.core() ? FloorPlan.Kind.SOLID
                    : FloorPlan.Kind.WINDOWED, bay.core() ? "back wall behind a core" : "back wall"));
            if (!bay.core() && bay != first && bay != last) {
                covers.addAll(side(infill, bay.x1(), 0, bay.x0(), 0, FloorPlan.Kind.WINDOWED, "front wall"));
            }
            if (k > 0) {
                covers.addAll(infill.along(new Infill.Side(bay.x0(), -deep, bay.x0(), 0, FloorPlan.Kind.SOLID,
                        "party wall"), 0, COVER));
            }
        }
        // the corner homes: out to the balcony's edge, across it facing in, and the block's ends
        covers.addAll(side(infill, first.x1(), corridor, first.x0(), corridor, FloorPlan.Kind.WINDOWED,
                "corner's front wall"));
        covers.addAll(side(infill, last.x1(), corridor, last.x0(), corridor, FloorPlan.Kind.WINDOWED,
                "corner's front wall"));
        covers.addAll(side(infill, first.x1(), 0, first.x1(), corridor, FloorPlan.Kind.SOLID, "corner's wall"));
        covers.addAll(side(infill, last.x0(), corridor, last.x0(), 0, FloorPlan.Kind.SOLID, "corner's wall"));
        covers.addAll(side(infill, first.x0(), corridor, first.x0(), -deep, FloorPlan.Kind.WINDOWED, "end wall"));
        covers.addAll(side(infill, last.x1(), -deep, last.x1(), corridor, FloorPlan.Kind.WINDOWED, "end wall"));
        covers.add(new FloorPlan.Cover(new FloorPlan.Wall(first.x1(), corridor - COVER / 2, last.x0(),
                corridor - COVER / 2, COVER, "access balcony's parapet"), FloorPlan.Kind.PARAPET));
        return covers;
    }

    /**
     * The door of the home in bay {@code k}, and where it stands added to {@code around}, so the cover goes round it:
     * in the middle of its face on the balcony, or, a corner home's, in the middle of its wall across the balcony.
     */
    private FloorPlan.Cover door(final int k, final List<Box> around) {
        final GenericSlabBlock.Bay bay = bays.get(k);
        final double half = DOOR / 2;
        final double mid = corridor / 2;
        if (k == 0) {
            around.add(new Box(bay.x1() - COVER, mid - half, bay.x1(), mid + half));
            return new FloorPlan.Cover(new FloorPlan.Wall(bay.x1() - COVER / 2, mid - half, bay.x1() - COVER / 2,
                    mid + half, COVER, "corner's door"), FloorPlan.Kind.DOOR);
        }
        if (k == bays.size() - 1) {
            around.add(new Box(bay.x0(), mid - half, bay.x0() + COVER, mid + half));
            return new FloorPlan.Cover(new FloorPlan.Wall(bay.x0() + COVER / 2, mid + half, bay.x0() + COVER / 2,
                    mid - half, COVER, "corner's door"), FloorPlan.Kind.DOOR);
        }
        final double middle = (bay.x0() + bay.x1()) / 2;
        around.add(new Box(middle - half, -COVER, middle + half, 0));
        return new FloorPlan.Cover(new FloorPlan.Wall(middle + half, -COVER / 2, middle - half, -COVER / 2, COVER,
                "front door"), FloorPlan.Kind.DOOR);
    }

    /** The cover along a side from ({@code x0}, {@code z0}) to ({@code x1}, {@code z1}), inside on its left. */
    private List<FloorPlan.Cover> side(final Infill infill, final double x0, final double z0, final double x1,
                                       final double z1, final FloorPlan.Kind kind, final String name) {
        return infill.along(new Infill.Side(x0, z0, x1, z1, kind, name), COVER / 2, COVER);
    }
}
