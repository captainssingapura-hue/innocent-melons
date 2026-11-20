package com.example.fxsuite.life.body;

/**
 * A piece of clothing, declared: what it is, how it is cut and in what colours; nothing of whom it is on. Put on a
 * body ({@link Dressed}), it is made to that body: fitted to its contour, hung from it, moving with it. Any piece on any
 * body; an outfit is a few of them together ({@link Outfit}).
 */
public sealed interface Wear permits Wear.Robe, Wear.Sash, Wear.Leggings, Wear.Shoes {

    /** What it is called. */
    String name();

    /**
     * A long robe: its body from the waist up round the neck, fitted to the body's contour with {@code ease} to spare;
     * a crossed collar, its left lapel over its right, in {@code trim}; sleeves to the elbow and on, either wide bells
     * from the elbow ({@code wideSleeves}) or fitted to the wrist; its skirt hanging from the waist to {@code hem} over the
     * ground, flaring out by {@code flare}, clear of the legs.
     *
     * @param colour      its cloth
     * @param trim        its collar and its cuffs
     * @param ease        how loose, cm
     * @param hem         how high its hem over the ground, cm
     * @param flare       how far its skirt flares by the hem, as a share of the waist
     * @param wideSleeves whether its sleeves are wide bells from the elbow
     */
    record Robe(String colour, String trim, double ease, double hem, double flare, boolean wideSleeves)
            implements Wear {

        @Override
        public String name() {
            return "Robe";
        }
    }

    /**
     * A sash worn across the body, as a kasaya is: over the left shoulder, down across the chest to the right hip,
     * round and up the back; laid on whatever is under it.
     *
     * @param colour its cloth
     * @param wide   how wide, cm
     */
    record Sash(String colour, double wide) implements Wear {

        @Override
        public String name() {
            return "Sash";
        }
    }

    /** Leggings, close on the legs, from the hips to the ankles. */
    record Leggings(String colour) implements Wear {

        @Override
        public String name() {
            return "Leggings";
        }
    }

    /** Cloth shoes, close on the feet. */
    record Shoes(String colour) implements Wear {

        @Override
        public String name() {
            return "Shoes";
        }
    }
}
