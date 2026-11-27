package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Fingerprint;
import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.fat.FatPattern;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.skin.Skins;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Boots;
import com.example.fxsuite.life.body.wear.Plate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A robot's pieces as they are kept and as they are drawn: each kept said of by its part and what it is of, its paint,
 * by their names; read back as they were. What every robot is built over: the body shop's male model, lean, 188 cm,
 * as it is bound, its skin made the first time it is asked for and kept for the next; and the riding boots each
 * robot wears, shaped over that skin and whatever it wears under them. And what makes a robot as it is now: the
 * body shop's code.
 */
public final class RobotPieces {

    private static final int PART = 0;
    private static final int PAINT = 1;

    private final Skins skins = new Skins(FatPattern.LEAN);
    private String code;
    private Flesh skin;

    /** {@code made}, each piece as it is kept. */
    public List<Piece> kept(final List<RobotParts.Piece> made) {
        return made.stream().map(p -> new Piece(List.of(p.part().name(), p.paint().name()), p.mesh().points(),
                p.mesh().triangles())).toList();
    }

    /** {@code kept}, each piece as it was made: its part and its paint read from what is said of it. */
    public List<RobotParts.Piece> made(final List<Piece> kept) {
        return kept.stream().map(p -> new RobotParts.Piece(Part.valueOf(p.labels().get(PART)),
                Paint.valueOf(p.labels().get(PAINT)), new Mesher.Mesh(p.points(), p.triangles()))).toList();
    }

    /** The body every robot is built as, as it is bound. */
    public Rig bound() {
        return new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);
    }

    /** The male model's skin, made the first time it is asked for, {@code told} so, and kept for the next. */
    public synchronized Flesh skin(final Consumer<String> told) {
        if (skin == null) {
            told.accept("the skin the robots are built over");
            skin = skins.of(Proportions.MODEL);
        }
        return skin;
    }

    /** How the skins are made, and what is beneath what is worn over them. */
    public Skins skins() {
        return skins;
    }

    /**
     * A pair of riding boots, shaped over the model's skin and over {@code under}, the hard pieces worn beneath them;
     * their leather in the boots' paint, their soles in the soles'.
     */
    public List<RobotParts.Piece> boots(final List<Plate> under, final Consumer<String> told) {
        final Flesh s = skin(told);
        told.accept("the riding boots");
        final List<Beneath> beneath = under.stream().map(Boots.INSTANCE::under).toList();
        final Boots.Made made = Boots.INSTANCE.of(skins.beneath(s), beneath, bound());
        final List<RobotParts.Piece> pieces = new ArrayList<>();
        made.leather().forEach(p -> pieces.add(new RobotParts.Piece(p.part(), Paint.BOOT, p.mesh())));
        made.soles().forEach(p -> pieces.add(new RobotParts.Piece(p.part(), Paint.SOLE, p.mesh())));
        return pieces;
    }

    /**
     * What makes a robot as it is now, as a word ({@link Fingerprint}): the body shop's code, worked out the first
     * time it is asked, and {@code said}.
     */
    public synchronized String madeBy(final String said) {
        if (code == null) {
            code = new Fingerprint().of(List.of(RobotParts.class), "");
        }
        return code + " " + said;
    }
}
