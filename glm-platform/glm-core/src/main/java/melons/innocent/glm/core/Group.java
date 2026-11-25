package melons.innocent.glm.core;

/**
 * Which group of the platform a part belongs to: shown or hidden together, finished alike.
 */
public enum Group {
    UNDERBODY("Body"),
    SUBFRAMES("Subframes"),
    LINKS("Suspension links"),
    CARRIERS("Wheel carriers and hubs"),
    SPRINGS("Springs"),
    DAMPERS("Dampers"),
    BRAKES("Brake discs"),
    CALIPERS("Brake calipers"),
    ENGINE("Engine"),
    COVERS("Engine covers"),
    STEERING("Steering"),
    WHEELS("Wheels"),
    TYRES("Tyres");

    private final String title;

    Group(final String title) {
        this.title = title;
    }

    /** What it is called. */
    public String title() {
        return title;
    }
}
