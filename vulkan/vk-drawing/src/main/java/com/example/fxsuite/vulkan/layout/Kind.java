package com.example.fxsuite.vulkan.layout;

/**
 * What kind of value a field holds: how it is written in the shader's language, how many numbers it is, and what its
 * place in a block has to be a multiple of, in bytes, by the rule the card lays a block out by. Every number is a
 * single, four bytes.
 */
public enum Kind {

    REAL("float", 1, 4),
    PAIR("vec2", 2, 8),
    /** Three numbers, set in a block where four would be: the rule's own oddity, kept here so no one else minds it. */
    TRIPLE("vec3", 3, 16),
    QUAD("vec4", 4, 16),
    MATRIX("mat4", 16, 16);

    private static final int NUMBER = 4;

    private final String written;
    private final int numbers;
    private final int multiple;

    Kind(final String written, final int numbers, final int multiple) {
        this.written = written;
        this.numbers = numbers;
        this.multiple = multiple;
    }

    /** Its name in the shader's language. */
    public String written() {
        return written;
    }

    public int numbers() {
        return numbers;
    }

    /** How many bytes its numbers take. */
    public int bytes() {
        return NUMBER * numbers;
    }

    /** What its place in a block is a multiple of, in bytes. */
    public int multiple() {
        return multiple;
    }
}
