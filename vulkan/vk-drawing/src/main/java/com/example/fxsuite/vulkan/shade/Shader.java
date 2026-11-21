package com.example.fxsuite.vulkan.shade;

/** A shader as written: which of the two it is, and its code in the shader's language, ready to be compiled. */
public record Shader(Stage stage, String code) {
}
