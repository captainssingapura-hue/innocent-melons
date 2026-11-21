package com.example.fxsuite.life.body;

import java.util.List;

/**
 * What a piece of a body is coloured as, and how it takes the light: a robot's shell (lofted as skin first), its
 * panel bands, its joint rings and its visor, its frame (made of bones, its radius apart) and the housings at its
 * joints, its pistons' barrels and rods; what it carries; a skeleton's bone as it is, its teeth and its cartilage; and
 * the marks of its balance. Each its own colour by default, the courier's for a robot's, any of them changed by a
 * palette ({@link Palette}).
 */
public enum Paint {
    SKIN("Skin", "#d3d1c7", Finish.MATTE),
    BONE("Bone", "#b4b2a9", Finish.MATTE),
    RADIUS("Radius", "#f0997b", Finish.MATTE),
    SHELL("Shell", "#e9e4da", Finish.GLOSS),
    PANEL("Panel bands", "#2f8f9d", Finish.GLOSS),
    JOINT_RING("Joint rings", "#2a2e35", Finish.GLOSS),
    VISOR("Visor", "#7df9ff", Finish.GLOW),
    FRAME("Frame", "#b8bec4", Finish.METAL),
    HOUSING("Joint housings", "#3a3f46", Finish.METAL),
    BARREL("Piston barrels", "#2f8f9d", Finish.METAL),
    ROD("Piston rods", "#e2e6e9", Finish.METAL),
    GUN("Gun", "#2b2e33", Finish.METAL),
    GUN_FITTINGS("Gun fittings", "#4a4f57", Finish.METAL),
    AMMUNITION("Ammunition box", "#4b5320", Finish.MATTE),
    STRAP("Strap", "#3b2f25", Finish.MATTE),
    FLASH("Muzzle flash", "#fff3b0", Finish.GLOW),
    IVORY("Bone, as it is", "#e6dcc6", Finish.MATTE),
    TEETH("Teeth", "#f3efe3", Finish.GLOSS),
    CARTILAGE("Cartilage", "#c9d6d2", Finish.GLOSS),
    MARK_MASS("Centre of mass", "#e24b4a", Finish.MATTE),
    MARK_SUPPORT("Support", "#ba7517", Finish.MATTE),
    MARK_FALLING("Falling", "#a32d2d", Finish.MATTE),
    MARK_TARGET("Kept over", "#1d9e75", Finish.MATTE);

    /** How a colour takes the light: flat, with a soft sheen, as polished metal, or lit from within. */
    public enum Finish { MATTE, GLOSS, METAL, GLOW }

    /** The colours a robot is drawn in, that may be changed. */
    public static final List<Paint> ROBOT = List.of(SHELL, PANEL, JOINT_RING, VISOR, FRAME, HOUSING, BARREL, ROD);
    /** The colours what it carries is drawn in: its gun and the strap it hangs from. */
    public static final List<Paint> GEAR = List.of(GUN, GUN_FITTINGS, AMMUNITION, STRAP);

    private final String label;
    private final String colour;
    private final Finish finish;

    Paint(final String label, final String colour, final Finish finish) {
        this.label = label;
        this.colour = colour;
        this.finish = finish;
    }

    public String label() {
        return label;
    }

    /** Its colour unless a palette says otherwise: the interactive model's, or the courier's. */
    public String colour() {
        return colour;
    }

    public Finish finish() {
        return finish;
    }
}
