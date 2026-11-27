package melons.innocent.glm.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Motorcycle parts: the kinds of part a motorcycle is made of round its engine, by what each does in it. Its frames,
 * holding it together; its suspension; its controls; its wheels and its brakes; its final drive to the back wheel;
 * its bodywork; its intake, its exhaust and its cooling; and its lamps. Each group's kinds are any motorcycle's, and
 * where the V2's bike, the twin's, is made otherwise, a second group beside them of its own: its frames, which bolt to
 * its engine, and its bodywork, a cover over its airbox where a tank would be and its tank under its seat. Its engine
 * and gearbox are the engine parts' ({@link EngineParts}).
 *
 * <p>Only the kinds the bike needs are here. Each node is a record, its parent in its type; what each means is beside
 * this class, in {@code MotorcycleParts.md}.
 */
public record MotorcycleParts() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("motorcycle parts", new MotorcyclePart(), List.of(
                new SteeringHead(), new Swingarm(), new Subframe(), new EngineMount(),
                new TrellisFrame(), new MidStructure(),
                new ForkLeg(), new TopYoke(), new BottomYoke(), new RearShock(),
                new Handlebar(), new HandGrip(), new HandLever(), new FootPeg(), new FootPedal(), new Mirror(),
                new Gauge(),
                new CastWheel(), new Tyre(), new Axle(),
                new BrakeDisc(), new BrakeCaliper(), new MasterCylinder(),
                new DriveBelt(), new RearPulley(), new BeltGuard(),
                new Seat(), new FrontFender(), new RearFender(), new SideCover(),
                new AirboxCover(), new UnderseatTank(),
                new Airbox(), new AirFilter(),
                new HeaderPipe(), new Collector(), new Muffler(), new HeatShield(),
                new Radiator(), new RadiatorFan(), new CoolantTank(),
                new Headlamp(), new TailLamp(), new Indicator()),
                Meanings.INSTANCE.beside(MotorcycleParts.class));
    }

    /** The root: a part of a motorcycle. */
    public record MotorcyclePart() implements L0 {
    }

    // Frames: what holds the bike together and carries its rider, its engine and its wheels
    public record Frames() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoFrames() implements L2<Frames> { public Frames parent() { return new Frames(); } }
    public record SteeringHead() implements Leaf<MotoFrames> { public MotoFrames parent() { return new MotoFrames(); } }
    public record Swingarm() implements Leaf<MotoFrames> { public MotoFrames parent() { return new MotoFrames(); } }
    public record Subframe() implements Leaf<MotoFrames> { public MotoFrames parent() { return new MotoFrames(); } }
    public record EngineMount() implements Leaf<MotoFrames> { public MotoFrames parent() { return new MotoFrames(); } }
    public record V2Frames() implements L2<Frames> { public Frames parent() { return new Frames(); } }
    public record TrellisFrame() implements Leaf<V2Frames> { public V2Frames parent() { return new V2Frames(); } }
    public record MidStructure() implements Leaf<V2Frames> { public V2Frames parent() { return new V2Frames(); } }

    // Suspension: what lets the wheels follow the road
    public record Suspension() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoSuspension() implements L2<Suspension> { public Suspension parent() { return new Suspension(); } }
    public record ForkLeg() implements Leaf<MotoSuspension> {
        public MotoSuspension parent() { return new MotoSuspension(); }
    }
    public record TopYoke() implements Leaf<MotoSuspension> {
        public MotoSuspension parent() { return new MotoSuspension(); }
    }
    public record BottomYoke() implements Leaf<MotoSuspension> {
        public MotoSuspension parent() { return new MotoSuspension(); }
    }
    public record RearShock() implements Leaf<MotoSuspension> {
        public MotoSuspension parent() { return new MotoSuspension(); }
    }

    // Controls: what the rider steers, rides and is told by
    public record Controls() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoControls() implements L2<Controls> { public Controls parent() { return new Controls(); } }
    public record Handlebar() implements Leaf<MotoControls> {
        public MotoControls parent() { return new MotoControls(); }
    }
    public record HandGrip() implements Leaf<MotoControls> {
        public MotoControls parent() { return new MotoControls(); }
    }
    public record HandLever() implements Leaf<MotoControls> {
        public MotoControls parent() { return new MotoControls(); }
    }
    public record FootPeg() implements Leaf<MotoControls> {
        public MotoControls parent() { return new MotoControls(); }
    }
    public record FootPedal() implements Leaf<MotoControls> {
        public MotoControls parent() { return new MotoControls(); }
    }
    public record Mirror() implements Leaf<MotoControls> { public MotoControls parent() { return new MotoControls(); } }
    public record Gauge() implements Leaf<MotoControls> { public MotoControls parent() { return new MotoControls(); } }

    // Wheels: what it rolls on
    public record Wheels() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoWheels() implements L2<Wheels> { public Wheels parent() { return new Wheels(); } }
    public record CastWheel() implements Leaf<MotoWheels> { public MotoWheels parent() { return new MotoWheels(); } }
    public record Tyre() implements Leaf<MotoWheels> { public MotoWheels parent() { return new MotoWheels(); } }
    public record Axle() implements Leaf<MotoWheels> { public MotoWheels parent() { return new MotoWheels(); } }

    // Brakes: what slows its wheels
    public record Brakes() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoBrakes() implements L2<Brakes> { public Brakes parent() { return new Brakes(); } }
    public record BrakeDisc() implements Leaf<MotoBrakes> { public MotoBrakes parent() { return new MotoBrakes(); } }
    public record BrakeCaliper() implements Leaf<MotoBrakes> { public MotoBrakes parent() { return new MotoBrakes(); } }
    public record MasterCylinder() implements Leaf<MotoBrakes> {
        public MotoBrakes parent() { return new MotoBrakes(); }
    }

    // Final drives: from the gearbox's output to the back wheel
    public record FinalDrives() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoFinalDrives() implements L2<FinalDrives> {
        public FinalDrives parent() { return new FinalDrives(); }
    }
    public record DriveBelt() implements Leaf<MotoFinalDrives> {
        public MotoFinalDrives parent() { return new MotoFinalDrives(); }
    }
    public record RearPulley() implements Leaf<MotoFinalDrives> {
        public MotoFinalDrives parent() { return new MotoFinalDrives(); }
    }
    public record BeltGuard() implements Leaf<MotoFinalDrives> {
        public MotoFinalDrives parent() { return new MotoFinalDrives(); }
    }

    // Bodywork: what the rider sits on, and what covers the rest and keeps the road off it
    public record Bodywork() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoBodywork() implements L2<Bodywork> { public Bodywork parent() { return new Bodywork(); } }
    public record Seat() implements Leaf<MotoBodywork> { public MotoBodywork parent() { return new MotoBodywork(); } }
    public record FrontFender() implements Leaf<MotoBodywork> {
        public MotoBodywork parent() { return new MotoBodywork(); }
    }
    public record RearFender() implements Leaf<MotoBodywork> {
        public MotoBodywork parent() { return new MotoBodywork(); }
    }
    public record SideCover() implements Leaf<MotoBodywork> {
        public MotoBodywork parent() { return new MotoBodywork(); }
    }
    public record V2Bodywork() implements L2<Bodywork> { public Bodywork parent() { return new Bodywork(); } }
    public record AirboxCover() implements Leaf<V2Bodywork> { public V2Bodywork parent() { return new V2Bodywork(); } }
    public record UnderseatTank() implements Leaf<V2Bodywork> {
        public V2Bodywork parent() { return new V2Bodywork(); }
    }

    // Intakes: what the engine breathes in through
    public record Intakes() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoIntakes() implements L2<Intakes> { public Intakes parent() { return new Intakes(); } }
    public record Airbox() implements Leaf<MotoIntakes> { public MotoIntakes parent() { return new MotoIntakes(); } }
    public record AirFilter() implements Leaf<MotoIntakes> { public MotoIntakes parent() { return new MotoIntakes(); } }

    // Exhausts: what the burnt gas leaves by
    public record Exhausts() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoExhausts() implements L2<Exhausts> { public Exhausts parent() { return new Exhausts(); } }
    public record HeaderPipe() implements Leaf<MotoExhausts> {
        public MotoExhausts parent() { return new MotoExhausts(); }
    }
    public record Collector() implements Leaf<MotoExhausts> {
        public MotoExhausts parent() { return new MotoExhausts(); }
    }
    public record Muffler() implements Leaf<MotoExhausts> {
        public MotoExhausts parent() { return new MotoExhausts(); }
    }
    public record HeatShield() implements Leaf<MotoExhausts> {
        public MotoExhausts parent() { return new MotoExhausts(); }
    }

    // Cooling: what carries the engine's heat away in its water
    public record Cooling() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoCooling() implements L2<Cooling> { public Cooling parent() { return new Cooling(); } }
    public record Radiator() implements Leaf<MotoCooling> { public MotoCooling parent() { return new MotoCooling(); } }
    public record RadiatorFan() implements Leaf<MotoCooling> {
        public MotoCooling parent() { return new MotoCooling(); }
    }
    public record CoolantTank() implements Leaf<MotoCooling> {
        public MotoCooling parent() { return new MotoCooling(); }
    }

    // Lamps: what lights the way and shows the bike to others
    public record Lamps() implements L1<MotorcyclePart> {
        public MotorcyclePart parent() { return new MotorcyclePart(); }
    }
    public record MotoLamps() implements L2<Lamps> { public Lamps parent() { return new Lamps(); } }
    public record Headlamp() implements Leaf<MotoLamps> { public MotoLamps parent() { return new MotoLamps(); } }
    public record TailLamp() implements Leaf<MotoLamps> { public MotoLamps parent() { return new MotoLamps(); } }
    public record Indicator() implements Leaf<MotoLamps> { public MotoLamps parent() { return new MotoLamps(); } }
}
