package melons.innocent.doors.swing;

/**
 * A leaf made as its design says, from the leaf's own measures: stiles down each edge and rails across its head and
 * foot for a wooden one, and between them glass in lights or wood in raised panels; one sheet of glass with its
 * patches and pulls for a glass one. The widths of its members are a joiner's usual ones, and Claude's.
 *
 * @param joinery where its pieces are put
 * @param size    how big the leaf is
 */
record Leaves(Joinery joinery, Size size) {

    /** How wide a stile and a rail are, the foot rail, a muntin between panels, and a bar between lights. */
    private static final double STILE = 11;
    private static final double RAIL = 11;
    private static final double FOOT = 20;
    private static final double MUNTIN = 10;
    private static final double BAR = 2.5;
    /** How far a panel's raised field stands in from its edges. */
    private static final double FIELD = 3.5;

    /** The leaf of a door with glass panels. */
    void glazed(final Design.Glazed design) {
        final Wood wood = design.wood();
        final double thick = size.thick();
        final double far = size.wide() - STILE;
        final double head = size.high() - RAIL;
        framed(wood);
        double sill = FOOT;
        if (design.kick() > 0) {
            // a rail at the kick's height, and under it a panel set back from both faces
            final double kick = Math.max(FOOT + 10, design.kick() * size.high());
            joinery.timber(wood, STILE, far, kick, kick + RAIL, thick);
            joinery.timber(wood, STILE, far, FOOT, kick, 0.45 * thick);
            sill = kick + RAIL;
        }
        for (int bar = 1; bar < design.across(); bar++) {
            final double at = STILE + (far - STILE) * bar / design.across();
            joinery.timber(wood, at - BAR / 2, at + BAR / 2, sill, head, 0.8 * thick);
        }
        for (int bar = 1; bar < design.up(); bar++) {
            final double at = sill + (head - sill) * bar / design.up();
            joinery.timber(wood, STILE, far, at - BAR / 2, at + BAR / 2, 0.76 * thick);
        }
        joinery.pane(STILE, far, sill, head);
        lever(design.fittings());
    }

    /** The leaf of a solid wooden door. */
    void solid(final Design.Solid design) {
        final Wood wood = design.wood();
        final double thick = size.thick();
        if (design.flush()) {
            joinery.timber(wood, 0, size.wide(), 0, size.high(), thick);
            lever(design.fittings());
            return;
        }
        framed(wood);
        final double wide = (size.wide() - 2 * STILE - (design.across() - 1) * MUNTIN) / design.across();
        final double high = (size.high() - FOOT - RAIL - (design.up() - 1) * RAIL) / design.up();
        for (int row = 0; row < design.up(); row++) {
            final double low = FOOT + row * (high + RAIL);
            if (row > 0) {
                joinery.timber(wood, STILE, size.wide() - STILE, low - RAIL, low, thick);
            }
            for (int column = 0; column < design.across(); column++) {
                final double out = STILE + column * (wide + MUNTIN);
                if (column > 0) {
                    joinery.timber(wood, out - MUNTIN, out, low, low + high, thick);
                }
                // the panel, set back, and its field, raised again
                joinery.timber(wood, out, out + wide, low, low + high, 0.45 * thick);
                joinery.timber(wood, out + FIELD, out + wide - FIELD, low + FIELD, low + high - FIELD, 0.75 * thick);
            }
        }
        lever(design.fittings());
    }

    /** The leaf of a glass door: the sheet, a patch at its hinge side's foot and head, and a pull to each face. */
    void frameless(final Design.Frameless design) {
        final Fittings fittings = design.fittings();
        joinery.glass(0, size.wide(), 0, size.high(), size.thick());
        joinery.patch(fittings, 0, 16, 0, 5, 1);
        joinery.patch(fittings, 0, 16, size.high() - 5, size.high(), 1);
        // a long pull standing off each face on two short posts
        final double at = size.wide() - 9;
        joinery.furniture(fittings, at - 1.1, at + 1.1, 82, 127, 4, 6.2);
        joinery.furniture(fittings, at - 0.8, at + 0.8, 88, 89.6, 0, 4);
        joinery.furniture(fittings, at - 0.8, at + 0.8, 119.4, 121, 0, 4);
    }

    /** The stiles down each edge, and the rails across the head and the foot. */
    private void framed(final Wood wood) {
        joinery.timber(wood, 0, STILE, 0, size.high(), size.thick());
        joinery.timber(wood, size.wide() - STILE, size.wide(), 0, size.high(), size.thick());
        joinery.timber(wood, STILE, size.wide() - STILE, 0, FOOT, size.thick());
        joinery.timber(wood, STILE, size.wide() - STILE, size.high() - RAIL, size.high(), size.thick());
    }

    /** A lever handle to each face, on the free stile a metre up: its rose, its neck, and the lever itself. */
    private void lever(final Fittings fittings) {
        final double at = size.wide() - STILE / 2;
        joinery.furniture(fittings, at - 2.8, at + 2.8, 99, 104.6, 0, 0.8);
        joinery.furniture(fittings, at - 0.9, at + 0.9, 100.9, 102.7, 0.8, 4.4);
        joinery.furniture(fittings, at - 12, at + 0.9, 100.9, 102.7, 4.4, 6.2);
    }
}
