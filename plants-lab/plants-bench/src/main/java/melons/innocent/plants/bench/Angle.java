package melons.innocent.plants.bench;

/**
 * A way of looking asked for in the bench's window, by how far above the subject the eye is: straight down, as near
 * as the view goes; half way up; or low, a few degrees over the ground. Said on the bench's bus; the card's thread
 * turns its view to it once, as the next frame begins, and the mouse moves it from there.
 */
public enum Angle {
    ABOVE("From above", 88),
    HALF("At 45°", 45),
    LOW("Low", 4);

    private final String title;
    private final double pitch;

    Angle(final String title, final double pitch) {
        this.title = title;
        this.pitch = pitch;
    }

    /** What its button says. */
    public String title() {
        return title;
    }

    /** How far above the subject the eye is, in degrees. */
    public double pitch() {
        return pitch;
    }
}
