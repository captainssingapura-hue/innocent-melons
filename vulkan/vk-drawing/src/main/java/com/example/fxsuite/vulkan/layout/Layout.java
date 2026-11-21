package com.example.fxsuite.vulkan.layout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * What a run of bytes holds, described once: its name, and its fields in order. The bytes Java writes, what the
 * pipeline is told of them and what the shader declares are each made from this, so the three cannot disagree. Laid
 * out one of two ways: packed, each field straight after the one before, as a mesh's points are; or as a block, each
 * field set where the card's rule for blocks puts it.
 */
public record Layout(String name, List<Field> fields) {

    private static final int BLOCK = 16;

    public Layout {
        fields = List.copyOf(fields);
        if (!name.matches("[A-Z][A-Za-z0-9]*")) {
            throw new IllegalArgumentException("a layout's name is a capitalised word: " + name);
        }
        if (fields.isEmpty()) {
            throw new IllegalArgumentException(name + " holds nothing");
        }
        final Set<String> seen = new HashSet<>();
        for (final Field field : fields) {
            if (!seen.add(field.name())) {
                throw new IllegalArgumentException(name + " has two fields called " + field.name());
            }
        }
    }

    /** Its field called {@code name}; one it has not is refused, by name. */
    public Field field(final String name) {
        return fields.stream().filter(field -> field.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(this.name + " has no field called " + name));
    }

    /** Where {@code name} begins, packed: after every field before it, in bytes. */
    public int packedAt(final String name) {
        int at = 0;
        for (final Field field : fields) {
            if (field.name().equals(name)) {
                return at;
            }
            at += field.kind().bytes();
        }
        throw new IllegalArgumentException(this.name + " has no field called " + name);
    }

    /** How many bytes one of it takes, packed. */
    public int packedBytes() {
        return fields.stream().mapToInt(field -> field.kind().bytes()).sum();
    }

    /** Where {@code name} begins in a block: the first place after the fields before it that its kind may stand at. */
    public int blockAt(final String name) {
        int end = 0;
        for (final Field field : fields) {
            final int at = rounded(end, field.kind().multiple());
            if (field.name().equals(name)) {
                return at;
            }
            end = at + field.kind().bytes();
        }
        throw new IllegalArgumentException(this.name + " has no field called " + name);
    }

    /** How many bytes it takes as a block: to the end of its last field, rounded up as the card's rule rounds it. */
    public int blockBytes() {
        final Field last = fields.getLast();
        return rounded(blockAt(last.name()) + last.kind().bytes(), BLOCK);
    }

    private int rounded(final int bytes, final int multiple) {
        return (bytes + multiple - 1) / multiple * multiple;
    }
}
