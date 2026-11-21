package com.example.fxsuite.vulkan.layout;

/** One value of a layout: what it is called, in the shader as in Java, and what kind of value it is. */
public record Field(String name, Kind kind) {

    public Field {
        if (!name.matches("[a-z][A-Za-z0-9]*")) {
            throw new IllegalArgumentException("a field's name is a word the shader's language takes: " + name);
        }
    }
}
