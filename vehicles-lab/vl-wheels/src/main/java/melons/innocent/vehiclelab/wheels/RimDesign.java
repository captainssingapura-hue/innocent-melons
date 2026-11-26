package melons.innocent.vehiclelab.wheels;

import melons.innocent.vehiclelab.forms.Shapes;

/** The rims there are to choose from, each by what it is called. */
public enum RimDesign {
    CLASSIC("Classic, eight windows"),
    SPOKED("Ten round spokes");

    private final String title;

    RimDesign(final String title) {
        this.title = title;
    }

    /** What it is called. */
    public String title() {
        return title;
    }

    /** A rim of this design, shaped with {@code shapes}. */
    public Rim rim(final Shapes shapes) {
        return switch (this) {
            case CLASSIC -> new ClassicRim(shapes);
            case SPOKED -> new SpokedRim(shapes);
        };
    }
}
