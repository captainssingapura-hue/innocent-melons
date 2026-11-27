package melons.innocent.glm.existences;

import com.example.fxsuite.taxonomy.L3;
import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.glm.existences.EngineParts.V2Drives;

/**
 * The motorcycle twin's transmission, in its crankcase with it, among its drives ({@link EngineParts}): from the crank
 * by its primary drive, gear to gear, to its clutch; through its wet clutch to its gearbox; and through the gearbox's
 * six speeds out to the pulley its belt runs from. Each a group of its own, of the kinds it is made of.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in
 * {@code Transmission.md}. Their tree is the engine parts', {@link EngineParts#tree()}.
 */
public final class Transmission {

    private Transmission() {
    }

    // The primary drive: from the crank to the clutch, gear to gear
    public record PrimaryDrive() implements L3<V2Drives> { public V2Drives parent() { return new V2Drives(); } }
    public record ScissorGear() implements Leaf<PrimaryDrive> {
        public PrimaryDrive parent() { return new PrimaryDrive(); }
    }
    public record ClutchGear() implements Leaf<PrimaryDrive> {
        public PrimaryDrive parent() { return new PrimaryDrive(); }
    }

    // The wet clutch: what lets the engine turn without driving the gearbox
    public record WetClutch() implements L3<V2Drives> { public V2Drives parent() { return new V2Drives(); } }
    public record ClutchBasket() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }
    public record ClutchHub() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }
    public record FrictionPlate() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }
    public record SteelPlate() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }
    public record PressurePlate() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }
    public record ClutchSpring() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }
    public record SlipperRamp() implements Leaf<WetClutch> { public WetClutch parent() { return new WetClutch(); } }

    // The gearbox: two shafts, six pairs of gears, chosen in turn by its drum
    public record Gearbox() implements L3<V2Drives> { public V2Drives parent() { return new V2Drives(); } }
    public record Mainshaft() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record Countershaft() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record FixedGear() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record FreeGear() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record SlidingGear() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record ShiftDrum() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record ShiftFork() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record ShiftShaft() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
    public record OutputPulley() implements Leaf<Gearbox> { public Gearbox parent() { return new Gearbox(); } }
}
