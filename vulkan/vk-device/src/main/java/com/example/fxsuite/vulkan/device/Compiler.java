package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.shade.Shader;

import java.nio.ByteBuffer;

/**
 * What turns a shader as written into the words the card takes. The one way through which shaders are compiled, so
 * the compiler behind it can be changed, to one Java writes itself, say, without anything else knowing.
 */
public interface Compiler {

    /**
     * {@code shader} compiled: the card's words, four bytes each, lowest byte first, from the first. A shader that
     * does not compile is refused, with what the compiler said of it.
     */
    ByteBuffer compiled(Shader shader);
}
