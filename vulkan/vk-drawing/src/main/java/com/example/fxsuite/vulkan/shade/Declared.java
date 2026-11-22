package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.layout.Field;
import com.example.fxsuite.vulkan.layout.Layout;

import java.util.ArrayList;
import java.util.List;

/**
 * A layout as a shader declares it: never written by hand, so a shader cannot declare what Java does not fill. The
 * same layout is declared as what a vertex shader takes in, as what one shader hands on and the next takes, as a
 * block every frame fills, or as what is pushed with each thing drawn.
 */
public record Declared(Layout layout) {

    /** As what a vertex shader takes in from a mesh's points: each field at the place it has in the layout. */
    public List<String> taken() {
        return each("in");
    }

    /** The same, for a vertex shader that takes only the fields called {@code only}: each still at its own place. */
    public List<String> taken(final List<String> only) {
        only.forEach(layout::field);
        return each("in").stream().filter(line -> only.stream().anyMatch(name -> line.endsWith(" " + name + ";")))
                .toList();
    }

    /** As what a vertex shader hands on. */
    public List<String> handedOn() {
        return each("out");
    }

    /** As what a fragment shader takes from the vertex shader: the same fields at the same places. */
    public List<String> handedIn() {
        return each("in");
    }

    /** As a block filled once a frame, bound at {@code binding}, its fields reached through {@code held}. */
    public List<String> block(final int binding, final String held) {
        return braced("layout(std140, set = 0, binding = " + binding + ") uniform " + layout.name(), held);
    }

    /** As what is pushed with each thing drawn, its fields reached through {@code held}. */
    public List<String> pushed(final String held) {
        return braced("layout(push_constant) uniform " + layout.name(), held);
    }

    private List<String> each(final String way) {
        final List<String> lines = new ArrayList<>();
        for (int place = 0; place < layout.fields().size(); place++) {
            final Field field = layout.fields().get(place);
            lines.add("layout(location = " + place + ") " + way + " " + field.kind().written() + " " + field.name()
                    + ";");
        }
        return lines;
    }

    private List<String> braced(final String opening, final String held) {
        final List<String> lines = new ArrayList<>();
        lines.add(opening + " {");
        for (final Field field : layout.fields()) {
            lines.add("    " + field.kind().written() + " " + field.name() + ";");
        }
        lines.add("} " + held + ";");
        return lines;
    }
}
