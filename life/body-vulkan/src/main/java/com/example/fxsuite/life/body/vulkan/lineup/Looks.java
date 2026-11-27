package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * Whole looks a robot may be given at once, each a paintwork ({@link Paintwork}), to be changed piece by piece after:
 * as built; and a few others, each its shell, its panels, its frame and its housings, its visor and rods as built.
 */
public enum Looks {

    AS_BUILT("As built"),
    GUNMETAL_AND_GOLD("Gunmetal and gold"),
    RACING_RED("Racing red"),
    MIDNIGHT("Midnight blue"),
    RESCUE("Rescue white and orange"),
    POLISHED("Polished metal");

    private final String said;

    Looks(final String said) {
        this.said = said;
    }

    /** The paintwork it is. */
    public Paintwork work() {
        final Paintwork built = new Paintwork();
        return switch (this) {
            case AS_BUILT -> built;
            case GUNMETAL_AND_GOLD -> coated(built, coat(new Materials.MetallicPaint(), 0x3a3f46),
                    coat(new Materials.Gold(), 0), coat(new Materials.Gunmetal(), 0), coat(new Materials.Gold(), 0));
            case RACING_RED -> coated(built, coat(new Materials.GlossPaint(), 0xb3121c),
                    coat(new Materials.GlossPaint(), 0x141414), coat(new Materials.Titanium(), 0),
                    coat(new Materials.Gunmetal(), 0));
            case MIDNIGHT -> coated(built, coat(new Materials.MetallicPaint(), 0x1b2a4a),
                    coat(new Materials.GlossPaint(), 0xe0b04a), coat(new Materials.Gunmetal(), 0),
                    coat(new Materials.Rubber(), 0));
            case RESCUE -> coated(built, coat(new Materials.GlossPaint(), 0xf2f2ee),
                    coat(new Materials.GlossPaint(), 0xff6a13), coat(new Materials.Aluminium(), 0),
                    coat(new Materials.Rubber(), 0));
            case POLISHED -> coated(built, coat(new Materials.Aluminium(), 0), coat(new Materials.Gold(), 0),
                    coat(new Materials.Titanium(), 0), coat(new Materials.Gunmetal(), 0));
        };
    }

    /** {@code built} with its shell, its panels, its frame and its housings coated as told. */
    private Paintwork coated(final Paintwork built, final Paintwork.Coat shell, final Paintwork.Coat panel,
                             final Paintwork.Coat frame, final Paintwork.Coat housing) {
        return built.with(Paint.SHELL, shell).with(Paint.PANEL, panel).with(Paint.FRAME, frame)
                .with(Paint.HOUSING, housing);
    }

    /** A coat of {@code m}, painted {@code rgb} if it is a paint. */
    private Paintwork.Coat coat(final Material m, final int rgb) {
        return new Paintwork.Coat(m, new Vec3((rgb >> 16 & 0xff) / 255.0, (rgb >> 8 & 0xff) / 255.0,
                (rgb & 0xff) / 255.0));
    }

    @Override
    public String toString() {
        return said;
    }
}
