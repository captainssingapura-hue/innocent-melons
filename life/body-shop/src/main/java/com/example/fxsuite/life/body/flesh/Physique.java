package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;

import java.util.List;
import java.util.Optional;

/**
 * A body as it is seen, made whole: how it is proportioned (its height, how long its legs and arms, how far apart its
 * shoulder and hip joints, as shares of its height), its bones a man's or a woman's, and its flesh sculpted over them
 * ({@link Tube}). A man and a woman of a model's build, long in the leg; representative, set from memory, not anyone's.
 */
public enum Physique {
    MAN("A man, a model's build", new Proportions(188, 1.0, 0.55, 0.445, 0.232, 0.096), 0),
    WOMAN("A woman, a model's build", new Proportions(178, 0.92, 0.565, 0.44, 0.205, 0.106), 1);

    private final String said;
    private final Proportions proportions;
    private final double female;

    Physique(final String said, final Proportions proportions, final double female) {
        this.said = said;
        this.proportions = proportions;
        this.female = female;
    }

    public String said() {
        return said;
    }

    /** How it is proportioned. */
    public Proportions proportions() {
        return proportions;
    }

    /** How far its bones are a woman's: 0 a man's, 1 a woman's. */
    public double female() {
        return female;
    }

    /** Its limbs' flesh, every tube of it, as on the right. */
    public List<Tube> tubes() {
        return this == MAN ? ManFlesh.INSTANCE.tubes() : WomanFlesh.INSTANCE.tubes();
    }

    /** Its torso's shape, whole. */
    public Torso torso() {
        return this == MAN ? ManFlesh.INSTANCE.torso() : WomanFlesh.INSTANCE.torso();
    }

    /**
     * Whether part {@code p}'s bones are under its flesh, and so not drawn: the trunk's and the shoulders', under the
     * torso; a limb's under its tube.
     */
    public boolean hidden(final Part p) {
        final Part right = p.side() < 0 ? p.other() : p;
        return switch (right) {
            case PELVIS, ABDOMEN, CHEST, NECK, RIGHT_GIRDLE -> true;
            default -> on(right).isPresent();
        };
    }

    /** The tube on part {@code p}, as on the right (a left part's its right's), if it has one. */
    public Optional<Tube> on(final Part p) {
        final Part right = p.side() < 0 ? p.other() : p;
        return tubes().stream().filter(t -> t.part() == right).findFirst();
    }
}
