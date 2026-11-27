package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Fingerprint;
import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.RobotParts;

import java.util.List;

/**
 * A robot's pieces as they are kept and as they are drawn: each kept said of by its part and what it is of, its paint,
 * by their names; read back as they were. And what makes a robot as it is now: the body shop's code.
 */
public final class RobotPieces {

    private static final int PART = 0;
    private static final int PAINT = 1;

    private String code;

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
