package com.example.fxsuite.texture.materials;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.L2;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Materials: what made things are made of. Whatever is in the tree is already shaded: a material is added only
 * with how it is drawn, by whoever draws it, so a thing may be made of any leaf here and be drawn. A leaf is a
 * material as it is seen: two kinds of brick whose faces are nothing alike are two leaves, though both are fired
 * clay.
 *
 * <p>Only the materials something has so far been made of and drawn in are here; a material is added when a thing is
 * first made of it, and not before. Metals and liquids are {@link Mirroring}, and so are gloss paint and glass,
 * each said of itself; what gives light is {@link Glowing}.
 * Every leaf is a {@link Material}.
 *
 * <p>The lab owns this tree: which materials there are, and how each looks. It is a taxonomy like the trees of what
 * exists, and is kept here and not among them because only who can shade a material can add one. What else is
 * true of a material, how far it spans or how it sounds underfoot, is for whoever knows that to say of the same
 * leaf.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Materials.md}.
 */
public record Materials() {

    /** The tree, read from its leaves, each node with its meaning. */
    public Taxonomy tree() {
        return new Taxonomy("materials", new Matter(), List.of(new Gold(), new Bronze(),
                new Titanium(), new Gunmetal(), new Steel(), new CommonRedBrick(), new ChineseGreyBrick(),
                new Adobe(), new Rubble(), new DressedStone(), new Concrete(),
                new Enamel(), new Rubber(), new GlossPaint(), new MetallicPaint(), new Glass(), new LitGlass(),
                new Flame(),
                new Rind(), new Water(),
                new Turf(), new Topsoil(), new Sand(), new Gravel(), new Bedrock(), new Mud(),
                new Skin(), new Lip(), new Nail(), new Hair(), new Sclera(), new Iris(), new Cornea(),
                new ToothEnamel(), new Bone(), new Cartilage(), new Muscle(), new Tendon(), new Fat()),
                Meanings.INSTANCE.beside(Materials.class));
    }

    /** The root: what made things are made of. */
    public record Matter() implements L0 {
    }

    // Metal: worked from ore
    public record Metal() implements L1<Matter>, Mirroring { public Matter parent() { return new Matter(); } }
    public record Gold() implements Leaf<Metal>, Material { public Metal parent() { return new Metal(); } }
    public record Bronze() implements Leaf<Metal>, Material { public Metal parent() { return new Metal(); } }
    public record Titanium() implements Leaf<Metal>, Material { public Metal parent() { return new Metal(); } }
    public record Gunmetal() implements Leaf<Metal>, Material { public Metal parent() { return new Metal(); } }
    public record Steel() implements Leaf<Metal>, Material { public Metal parent() { return new Metal(); } }

    // Masonry: laid piece on piece
    public record Masonry() implements L1<Matter> { public Matter parent() { return new Matter(); } }
    public record CommonRedBrick() implements Leaf<Masonry>, Material {
        public Masonry parent() { return new Masonry(); }
    }
    public record ChineseGreyBrick() implements Leaf<Masonry>, Material {
        public Masonry parent() { return new Masonry(); }
    }
    public record Adobe() implements Leaf<Masonry>, Material { public Masonry parent() { return new Masonry(); } }
    public record Rubble() implements Leaf<Masonry>, Material { public Masonry parent() { return new Masonry(); } }
    public record Concrete() implements Leaf<Masonry>, Material {
        public Masonry parent() { return new Masonry(); }
    }
    public record DressedStone() implements Leaf<Masonry>, Material {
        public Masonry parent() { return new Masonry(); }
    }

    // Synthetic: made by a process
    public record Synthetic() implements L1<Matter> { public Matter parent() { return new Matter(); } }
    public record Enamel() implements Leaf<Synthetic>, Material {
        public Synthetic parent() { return new Synthetic(); }
    }
    public record Rubber() implements Leaf<Synthetic>, Material {
        public Synthetic parent() { return new Synthetic(); }
    }
    public record GlossPaint() implements Leaf<Synthetic>, Material, Mirroring {
        public Synthetic parent() { return new Synthetic(); }
    }
    public record MetallicPaint() implements Leaf<Synthetic>, Material, Mirroring {
        public Synthetic parent() { return new Synthetic(); }
    }
    public record Glass() implements Leaf<Synthetic>, Material, Mirroring {
        public Synthetic parent() { return new Synthetic(); }
    }

    // Luminous: gives light of its own
    public record Luminous() implements L1<Matter>, Glowing { public Matter parent() { return new Matter(); } }
    public record LitGlass() implements Leaf<Luminous>, Material { public Luminous parent() { return new Luminous(); } }
    public record Flame() implements Leaf<Luminous>, Material { public Luminous parent() { return new Luminous(); } }

    // Grown: what grew
    public record Grown() implements L1<Matter> { public Matter parent() { return new Matter(); } }
    public record Rind() implements Leaf<Grown>, Material { public Grown parent() { return new Grown(); } }

    // Liquid: what flows, and lies level
    public record Liquid() implements L1<Matter>, Mirroring { public Matter parent() { return new Matter(); } }
    public record Water() implements Leaf<Liquid>, Material { public Liquid parent() { return new Liquid(); } }

    // Ground: what the land is covered with
    public record Ground() implements L1<Matter> { public Matter parent() { return new Matter(); } }
    public record Turf() implements Leaf<Ground>, Material { public Ground parent() { return new Ground(); } }
    public record Topsoil() implements Leaf<Ground>, Material { public Ground parent() { return new Ground(); } }
    public record Sand() implements Leaf<Ground>, Material { public Ground parent() { return new Ground(); } }
    public record Gravel() implements Leaf<Ground>, Material { public Ground parent() { return new Ground(); } }
    public record Bedrock() implements Leaf<Ground>, Material { public Ground parent() { return new Ground(); } }
    public record Mud() implements Leaf<Ground>, Material { public Ground parent() { return new Ground(); } }

    // Bodily: what a human body is made of; what is seen of a person, and what is under the skin
    public record Bodily() implements L1<Matter> { public Matter parent() { return new Matter(); } }
    public record Outward() implements L2<Bodily> { public Bodily parent() { return new Bodily(); } }
    public record Inward() implements L2<Bodily> { public Bodily parent() { return new Bodily(); } }
    public record Skin() implements Leaf<Outward>, Material { public Outward parent() { return new Outward(); } }
    public record Lip() implements Leaf<Outward>, Material { public Outward parent() { return new Outward(); } }
    public record Nail() implements Leaf<Outward>, Material { public Outward parent() { return new Outward(); } }
    public record Hair() implements Leaf<Outward>, Material { public Outward parent() { return new Outward(); } }
    public record Sclera() implements Leaf<Outward>, Material { public Outward parent() { return new Outward(); } }
    public record Iris() implements Leaf<Outward>, Material { public Outward parent() { return new Outward(); } }
    public record Cornea() implements Leaf<Outward>, Material, Mirroring {
        public Outward parent() { return new Outward(); }
    }
    public record ToothEnamel() implements Leaf<Outward>, Material {
        public Outward parent() { return new Outward(); }
    }
    public record Bone() implements Leaf<Inward>, Material { public Inward parent() { return new Inward(); } }
    public record Cartilage() implements Leaf<Inward>, Material { public Inward parent() { return new Inward(); } }
    public record Muscle() implements Leaf<Inward>, Material { public Inward parent() { return new Inward(); } }
    public record Tendon() implements Leaf<Inward>, Material { public Inward parent() { return new Inward(); } }
    public record Fat() implements Leaf<Inward>, Material { public Inward parent() { return new Inward(); } }
}
