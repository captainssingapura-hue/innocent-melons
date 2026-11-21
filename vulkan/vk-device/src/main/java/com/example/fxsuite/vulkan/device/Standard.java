package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.shade.Shader;
import com.example.fxsuite.vulkan.shade.Stage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.util.shaderc.Shaderc.*;

/**
 * The standard compiler of shaders (shaderc), run inside the program: no file is read and none is written. Each
 * shader is compiled for Vulkan 1.3 and made as quick as the compiler can make it. Needs no card.
 */
public final class Standard implements Compiler {

    private static final String BEGINS = "main";

    @Override
    public ByteBuffer compiled(final Shader shader) {
        final long compiler = shaderc_compiler_initialize();
        final long asked = shaderc_compile_options_initialize();
        final ByteBuffer code = MemoryUtil.memUTF8(shader.code(), false);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            shaderc_compile_options_set_target_env(asked, shaderc_target_env_vulkan, shaderc_env_version_vulkan_1_3);
            shaderc_compile_options_set_optimization_level(asked, shaderc_optimization_level_performance);
            final int kind = shader.stage() == Stage.VERTEX ? shaderc_vertex_shader : shaderc_fragment_shader;
            final long made = shaderc_compile_into_spv(compiler, code, kind,
                    stack.UTF8(shader.stage().name()), stack.UTF8(BEGINS), asked);
            try {
                return words(made);
            } finally {
                shaderc_result_release(made);
            }
        } finally {
            MemoryUtil.memFree(code);
            shaderc_compile_options_release(asked);
            shaderc_compiler_release(compiler);
        }
    }

    /** What was compiled, as Java's own copy of the card's words; or the compiler's refusal. */
    private ByteBuffer words(final long made) {
        if (made == 0) {
            throw new Refused("shaderc_compile_into_spv", "nothing came back");
        }
        if (shaderc_result_get_compilation_status(made) != shaderc_compilation_status_success) {
            throw new Refused("shaderc_compile_into_spv", shaderc_result_get_error_message(made));
        }
        final ByteBuffer words = shaderc_result_get_bytes(made);
        final ByteBuffer copy = ByteBuffer.allocate(words.remaining()).order(ByteOrder.LITTLE_ENDIAN);
        copy.put(words).rewind();
        return copy;
    }
}
