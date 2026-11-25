package melons.innocent.doors.swing;

/**
 * How a swing door is designed, and with it what it is made of: one of a few designs, each saying only what is its
 * own. A door's design is in its type ({@link SwingDoor}), so what asks for a glass door cannot be handed a wooden
 * one.
 */
public sealed interface Design {

    /** What its hinges and handle are made of. */
    Fittings fittings();

    /** Whether it hangs in a frame of its own: jambs and a head. A sheet of glass on patches does not. */
    boolean framed();

    /**
     * The traditional door with glass panels: stiles and rails of wood, and between them lights of glass parted by
     * slender bars, over a solid panel at its foot where it has one.
     *
     * @param wood     how its wood is finished
     * @param fittings what its hinges and handle are made of
     * @param across   how many lights there are side by side
     * @param up       how many lights there are one over another
     * @param kick     how much of the leaf's height, from its foot, is solid: none, and it is glazed to the bottom
     */
    record Glazed(Wood wood, Fittings fittings, int across, int up, double kick) implements Design {

        public Glazed {
            if (across < 1 || across > 6 || up < 1 || up > 10 || !(kick >= 0 && kick <= 0.6)) {
                throw new IllegalArgumentException("lights in a door: " + across + " by " + up + " over " + kick);
            }
        }

        @Override
        public boolean framed() {
            return true;
        }
    }

    /**
     * The solid wood door: stiles and rails with raised panels between them, or one flush slab.
     *
     * @param wood     how its wood is finished
     * @param fittings what its hinges and handle are made of
     * @param across   how many panels there are side by side; none, and it is flush
     * @param up       how many panels there are one over another; none, and it is flush
     */
    record Solid(Wood wood, Fittings fittings, int across, int up) implements Design {

        public Solid {
            if (across < 0 || across > 4 || up < 0 || up > 6) {
                throw new IllegalArgumentException("panels in a door: " + across + " by " + up);
            }
        }

        /** Whether it is one flush slab, with no panels. */
        public boolean flush() {
            return across == 0 || up == 0;
        }

        @Override
        public boolean framed() {
            return true;
        }
    }

    /**
     * The modern glass door: one sheet of glass, held at its hinge side by a metal patch at its foot and its head,
     * with a long pull to each face, and no frame.
     *
     * @param fittings what its patches and pulls are made of
     */
    record Frameless(Fittings fittings) implements Design {

        @Override
        public boolean framed() {
            return false;
        }
    }
}
