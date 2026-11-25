package melons.innocent.vehiclelab.motorcycle;

/**
 * What a finish is: a {@link Kind} of surface, and the colour it is where the kind takes one, as paint does, said as
 * the web says colours ({@code #rrggbb}). A kind that does not take one ({@link Kind#coloured}), a bare metal or
 * rubber, keeps its own whatever is said.
 *
 * @param kind   what kind of surface it is
 * @param colour its colour, where it takes one
 */
public record Coat(Kind kind, String colour) {

    /** What kind of surface a finish is: the material lab is asked for each, by whoever draws it. */
    public enum Kind {
        GLOSS_PAINT("Gloss paint", true),
        METALLIC_PAINT("Metallic paint", true),
        ENAMEL("Enamel", false),
        TITANIUM("Titanium", false),
        GUN_METAL("Gun metal", false),
        STEEL("Steel", false),
        BRONZE("Bronze", false),
        GOLD("Gold", false),
        RUBBER("Rubber", false),
        GLASS("Glass", false),
        LAMP("Lamp, lit", true);

        private final String title;
        private final boolean coloured;

        Kind(final String title, final boolean coloured) {
            this.title = title;
            this.coloured = coloured;
        }

        /** Whether it takes the colour said for it, as paint does and a lamp's light; or keeps its own. */
        public boolean coloured() {
            return coloured;
        }

        /** What it is called. */
        public String title() {
            return title;
        }
    }

    public Coat {
        if (kind == null || colour == null || !colour.matches("#[0-9a-fA-F]{6}")) {
            throw new IllegalArgumentException("a finish of some kind, its colour as #rrggbb: " + kind + ", " + colour);
        }
    }

    /** One of a kind that keeps its own colour. */
    public Coat(final Kind kind) {
        this(kind, "#ffffff");
    }

    /** How much red, green and blue its colour has, each from nought to one. */
    public double[] rgb() {
        final int value = Integer.parseInt(colour.substring(1), 16);
        return new double[] {(value >> 16 & 0xff) / 255.0, (value >> 8 & 0xff) / 255.0, (value & 0xff) / 255.0};
    }
}
