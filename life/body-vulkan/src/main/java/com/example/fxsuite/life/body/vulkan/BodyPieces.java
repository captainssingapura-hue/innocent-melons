package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.BodyGroup;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Layer;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.form.Mesh;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The body's pieces as the card draws them: its skin, its muscles' bellies, their tendons and its bones, each cut by
 * the group of the body's parts it is of ({@link BodyGroup}), and each in the tissue it is drawn in. A group drawn as
 * skin shows its skin; as muscles, its muscles and its bones under them; as a skeleton, its bones; hidden, nothing.
 * In the body's frame, centimetres, up the way {@code y} grows. Its bones as the body is bound, each of the part it
 * moves with; its skin and muscles where their points are said to be, posed as the body is.
 */
public final class BodyPieces {

    /** How far tendon a muscle's triangle is, on the average of its corners, to be drawn as tendon. */
    private static final double TENDON = 0.5;

    /**
     * A piece: of the group {@code group}, shown when it is drawn as deep as one of {@code shownIn} says, in
     * {@code material}, its surface {@code mesh}.
     */
    public record Piece(BodyGroup group, Set<Layer> shownIn, Material material, Mesh mesh) {
    }

    /** A bone, {@code piece}, as the body is bound, moving with {@code part}. */
    public record Bone(Part part, Piece piece) {
    }

    /** {@code skin}, its points at {@code at}, three numbers each, cut by the groups. */
    public List<Piece> skin(final Flesh skin, final float[] at) {
        final Sheet sheet = new Sheet(at, skin.triangles());
        final BodyGroup[] of = groups(skin, sheet);
        final List<Piece> made = new ArrayList<>();
        for (final BodyGroup g : BodyGroup.values()) {
            add(made, g, EnumSet.of(Layer.SKIN), new Materials.Skin(), sheet.mesh(t -> of[t] == g));
        }
        return made;
    }

    /** {@code muscles}, their points at {@code at}, cut by the groups, their bellies apart from their tendons. */
    public List<Piece> muscles(final MuscleBody.Bound muscles, final float[] at) {
        final Flesh f = muscles.flesh();
        final Sheet sheet = new Sheet(at, f.triangles());
        final BodyGroup[] of = groups(f, sheet);
        final float[] tendon = muscles.tendon();
        final boolean[] isTendon = new boolean[sheet.triangleCount()];
        for (int t = 0; t < isTendon.length; t++) {
            isTendon[t] = (tendon[sheet.corner(t, 0)] + tendon[sheet.corner(t, 1)] + tendon[sheet.corner(t, 2)]) / 3
                    >= TENDON;
        }
        final List<Piece> made = new ArrayList<>();
        for (final BodyGroup g : BodyGroup.values()) {
            add(made, g, EnumSet.of(Layer.MUSCLES), new Materials.Muscle(),
                    sheet.mesh(t -> of[t] == g && !isTendon[t]));
            add(made, g, EnumSet.of(Layer.MUSCLES), new Materials.Tendon(),
                    sheet.mesh(t -> of[t] == g && isTendon[t]));
        }
        return made;
    }

    /** The bones of the body {@code bound} holds, as finely as {@code detail}, each in what it is made of. */
    public List<Bone> bones(final Rig bound, final Detail detail) {
        final List<Bone> made = new ArrayList<>();
        for (final Part p : Part.values()) {
            final Part right = p.side() < 0 ? p.other() : p;
            final Rig.Frame f = bound.frame(p);
            for (final PartBones.Piece piece : PartBones.INSTANCE.pieces(right, detail, 0)) {
                final float[] at = piece.mesh().points().clone();
                for (int i = 0; i < at.length; i += 3) {
                    final V3 v = f.place(new V3(at[i], at[i + 1], at[i + 2]));
                    at[i] = (float) v.x();
                    at[i + 1] = (float) v.y();
                    at[i + 2] = (float) v.z();
                }
                final Mesh mesh = new Sheet(at, piece.mesh().triangles()).mesh();
                if (!mesh.corners().isEmpty()) {
                    made.add(new Bone(p, new Piece(group(p), EnumSet.of(Layer.SKELETON, Layer.MUSCLES),
                            material(piece.paint()), mesh)));
                }
            }
        }
        return made;
    }

    /** {@code f}'s points as it is bound, three numbers each. */
    public float[] bound(final Flesh f) {
        final float[] at = new float[3 * f.size()];
        for (int i = 0; i < f.size(); i++) {
            final V3 v = f.point(i);
            at[3 * i] = (float) v.x();
            at[3 * i + 1] = (float) v.y();
            at[3 * i + 2] = (float) v.z();
        }
        return at;
    }

    /** {@code mesh} kept in {@code made} as a piece, if it has any triangle. */
    private void add(final List<Piece> made, final BodyGroup g, final Set<Layer> shownIn, final Material material,
                     final Mesh mesh) {
        if (!mesh.corners().isEmpty()) {
            made.add(new Piece(g, shownIn, material, mesh));
        }
    }

    /** What a bone painted {@code paint} is drawn in. */
    private Material material(final Paint paint) {
        return switch (paint) {
            case CARTILAGE -> new Materials.Cartilage();
            case TEETH -> new Materials.ToothEnamel();
            default -> new Materials.Bone();
        };
    }

    /** The group of {@code p}. */
    private BodyGroup group(final Part p) {
        for (final BodyGroup g : BodyGroup.values()) {
            if (g.has(p)) {
                return g;
            }
        }
        throw new IllegalArgumentException("a part of no group: " + p);
    }

    /**
     * The group each of {@code sheet}'s triangles is of: of the part it is known to be of, if {@code f}'s triangles
     * are each known to be of one; else of the group most of its corners follow most, or its first corner's, or, if
     * its corners follow nothing, the trunk's.
     */
    private BodyGroup[] groups(final Flesh f, final Sheet sheet) {
        final BodyGroup[] of = new BodyGroup[sheet.triangleCount()];
        if (f.inPatches()) {
            final Map<String, Integer> index = new HashMap<>();
            for (int t = 0; t < of.length; t++) {
                index.put(key(sheet.corner(t, 0), sheet.corner(t, 1), sheet.corner(t, 2)), t);
            }
            for (final BodyGroup g : BodyGroup.values()) {
                final int[] mine = f.patch(g::has).triangles();
                for (int k = 0; k < mine.length; k += 3) {
                    of[index.get(key(mine[k], mine[k + 1], mine[k + 2]))] = g;
                }
            }
            return of;
        }
        final Map<Part, BodyGroup> byPart = new EnumMap<>(Part.class);
        for (final Part p : Part.values()) {
            byPart.put(p, group(p));
        }
        for (int t = 0; t < of.length; t++) {
            final BodyGroup a = byPart.get(f.most(sheet.corner(t, 0)));
            final BodyGroup b = byPart.get(f.most(sheet.corner(t, 1)));
            final BodyGroup c = byPart.get(f.most(sheet.corner(t, 2)));
            of[t] = b != null && b == c ? b : a != null ? a : b != null ? b : c != null ? c : BodyGroup.TRUNK;
        }
        return of;
    }

    private String key(final int a, final int b, final int c) {
        return a + " " + b + " " + c;
    }
}
