package melons.innocent.glm.existences;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Engine parts: the kinds of part an engine needs, by what each does in it. The blocks the cylinders are bored in and
 * the crank is carried by; the cranks, what turns and what goes up and down; the heads and what works the valves in
 * them; the covers; the electrics; the drives, from the crank to the cams and, in a motorcycle, on to its gearbox; and
 * the fluids, its oil and its water. Each group is split twice: the GLM's own, generic to every engine its one
 * modular design casts; and the V2's, the motorcycle twin's own kinds, where its parts are not the GLM's. The V2 takes
 * the GLM's kinds wherever they serve it: its pistons, rods, valves and plugs are theirs.
 *
 * <p>Only the kinds the engines need are here, made so far or wanted next. Each node is a record, its parent in its
 * type; what each means is beside this class, in {@code EngineParts.md}.
 */
public record EngineParts() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("engine parts", new EnginePart(), List.of(
                new InlineBlock(), new VeeBlock(), new BoxerBlock(), new CylinderLiner(),
                new TwinCrankcase(), new CylinderBarrel(),
                new Crankshaft(), new ConnectingRod(), new Piston(), new GudgeonPin(),
                new SplitPinCrankshaft(), new PrimaryBalancer(), new SecondaryBalancer(),
                new CylinderHead(), new CamTower(), new Camshaft(), new Valve(), new ValveSpring(),
                new TwinCamHead(), new CamPhaser(), new HydraulicTappet(), new SodiumValve(),
                new HeadCover(), new TimingCover(), new TrimCover(),
                new CamCover(), new ClutchCover(), new AlternatorCover(),
                new CoilPack(), new SparkPlug(),
                new Alternator(), new StarterMotor(),
                new TimingChain(),
                new CamChain(), new PrimaryDrive(), new WetClutch(), new Gearbox(),
                new OilSump(), new OilPump(), new WaterPump(),
                new OilCooler()),
                Meanings.INSTANCE.beside(EngineParts.class));
    }

    /** The root: a part of an engine. */
    public record EnginePart() implements L0 {
    }

    // Blocks: what the cylinders are bored in and the crank is carried by
    public record Blocks() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmBlocks() implements L2<Blocks> { public Blocks parent() { return new Blocks(); } }
    public record InlineBlock() implements Leaf<GlmBlocks> { public GlmBlocks parent() { return new GlmBlocks(); } }
    public record VeeBlock() implements Leaf<GlmBlocks> { public GlmBlocks parent() { return new GlmBlocks(); } }
    public record BoxerBlock() implements Leaf<GlmBlocks> { public GlmBlocks parent() { return new GlmBlocks(); } }
    public record CylinderLiner() implements Leaf<GlmBlocks> { public GlmBlocks parent() { return new GlmBlocks(); } }
    public record V2Blocks() implements L2<Blocks> { public Blocks parent() { return new Blocks(); } }
    public record TwinCrankcase() implements Leaf<V2Blocks> { public V2Blocks parent() { return new V2Blocks(); } }
    public record CylinderBarrel() implements Leaf<V2Blocks> { public V2Blocks parent() { return new V2Blocks(); } }

    // Cranks: what turns with the crank, and what goes up and down on it
    public record Cranks() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmCranks() implements L2<Cranks> { public Cranks parent() { return new Cranks(); } }
    public record Crankshaft() implements Leaf<GlmCranks> { public GlmCranks parent() { return new GlmCranks(); } }
    public record ConnectingRod() implements Leaf<GlmCranks> { public GlmCranks parent() { return new GlmCranks(); } }
    public record Piston() implements Leaf<GlmCranks> { public GlmCranks parent() { return new GlmCranks(); } }
    public record GudgeonPin() implements Leaf<GlmCranks> { public GlmCranks parent() { return new GlmCranks(); } }
    public record V2Cranks() implements L2<Cranks> { public Cranks parent() { return new Cranks(); } }
    public record SplitPinCrankshaft() implements Leaf<V2Cranks> { public V2Cranks parent() { return new V2Cranks(); } }
    public record PrimaryBalancer() implements Leaf<V2Cranks> { public V2Cranks parent() { return new V2Cranks(); } }
    public record SecondaryBalancer() implements Leaf<V2Cranks> { public V2Cranks parent() { return new V2Cranks(); } }

    // Heads: what closes the cylinders, and what works the valves in it
    public record Heads() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmHeads() implements L2<Heads> { public Heads parent() { return new Heads(); } }
    public record CylinderHead() implements Leaf<GlmHeads> { public GlmHeads parent() { return new GlmHeads(); } }
    public record CamTower() implements Leaf<GlmHeads> { public GlmHeads parent() { return new GlmHeads(); } }
    public record Camshaft() implements Leaf<GlmHeads> { public GlmHeads parent() { return new GlmHeads(); } }
    public record Valve() implements Leaf<GlmHeads> { public GlmHeads parent() { return new GlmHeads(); } }
    public record ValveSpring() implements Leaf<GlmHeads> { public GlmHeads parent() { return new GlmHeads(); } }
    public record V2Heads() implements L2<Heads> { public Heads parent() { return new Heads(); } }
    public record TwinCamHead() implements Leaf<V2Heads> { public V2Heads parent() { return new V2Heads(); } }
    public record CamPhaser() implements Leaf<V2Heads> { public V2Heads parent() { return new V2Heads(); } }
    public record HydraulicTappet() implements Leaf<V2Heads> { public V2Heads parent() { return new V2Heads(); } }
    public record SodiumValve() implements Leaf<V2Heads> { public V2Heads parent() { return new V2Heads(); } }

    // Covers: what closes the engine over what works inside it
    public record Covers() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmCovers() implements L2<Covers> { public Covers parent() { return new Covers(); } }
    public record HeadCover() implements Leaf<GlmCovers> { public GlmCovers parent() { return new GlmCovers(); } }
    public record TimingCover() implements Leaf<GlmCovers> { public GlmCovers parent() { return new GlmCovers(); } }
    public record TrimCover() implements Leaf<GlmCovers> { public GlmCovers parent() { return new GlmCovers(); } }
    public record V2Covers() implements L2<Covers> { public Covers parent() { return new Covers(); } }
    public record CamCover() implements Leaf<V2Covers> { public V2Covers parent() { return new V2Covers(); } }
    public record ClutchCover() implements Leaf<V2Covers> { public V2Covers parent() { return new V2Covers(); } }
    public record AlternatorCover() implements Leaf<V2Covers> { public V2Covers parent() { return new V2Covers(); } }

    // Electrics: what fires the charge, makes the current and turns the engine over
    public record Electrics() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmElectrics() implements L2<Electrics> { public Electrics parent() { return new Electrics(); } }
    public record CoilPack() implements Leaf<GlmElectrics> {
        public GlmElectrics parent() { return new GlmElectrics(); }
    }
    public record SparkPlug() implements Leaf<GlmElectrics> {
        public GlmElectrics parent() { return new GlmElectrics(); }
    }
    public record V2Electrics() implements L2<Electrics> { public Electrics parent() { return new Electrics(); } }
    public record Alternator() implements Leaf<V2Electrics> { public V2Electrics parent() { return new V2Electrics(); } }
    public record StarterMotor() implements Leaf<V2Electrics> {
        public V2Electrics parent() { return new V2Electrics(); }
    }

    // Drives: what carries the crank's turning to the cams, and to what the engine drives
    public record Drives() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmDrives() implements L2<Drives> { public Drives parent() { return new Drives(); } }
    public record TimingChain() implements Leaf<GlmDrives> { public GlmDrives parent() { return new GlmDrives(); } }
    public record V2Drives() implements L2<Drives> { public Drives parent() { return new Drives(); } }
    public record CamChain() implements Leaf<V2Drives> { public V2Drives parent() { return new V2Drives(); } }
    public record PrimaryDrive() implements Leaf<V2Drives> { public V2Drives parent() { return new V2Drives(); } }
    public record WetClutch() implements Leaf<V2Drives> { public V2Drives parent() { return new V2Drives(); } }
    public record Gearbox() implements Leaf<V2Drives> { public V2Drives parent() { return new V2Drives(); } }

    // Fluids: what keeps the oil round the engine and the water through it
    public record Fluids() implements L1<EnginePart> { public EnginePart parent() { return new EnginePart(); } }
    public record GlmFluids() implements L2<Fluids> { public Fluids parent() { return new Fluids(); } }
    public record OilSump() implements Leaf<GlmFluids> { public GlmFluids parent() { return new GlmFluids(); } }
    public record OilPump() implements Leaf<GlmFluids> { public GlmFluids parent() { return new GlmFluids(); } }
    public record WaterPump() implements Leaf<GlmFluids> { public GlmFluids parent() { return new GlmFluids(); } }
    public record V2Fluids() implements L2<Fluids> { public Fluids parent() { return new Fluids(); } }
    public record OilCooler() implements Leaf<V2Fluids> { public V2Fluids parent() { return new V2Fluids(); } }
}
