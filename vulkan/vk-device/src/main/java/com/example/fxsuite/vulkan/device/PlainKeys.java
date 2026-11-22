package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.Platform;
import org.lwjgl.system.SharedLibrary;

import static org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window;

/**
 * A window's keys kept plain. Where characters of some scripts are typed by composing them from several key
 * presses, the system sets an input method between the keys and whatever has them, which gathers the presses,
 * shows what it is composing, and takes some keys for itself, Shift among them. A window that is drawn in and
 * steered by its keys wants every press as it is made: so the input method is parted from the window, for as long
 * as the window lasts. Other windows keep theirs.
 *
 * <p>Windows has this to part, and it is parted there; elsewhere nothing is done.
 */
record PlainKeys(Kept<Of.Glass> glass) {

    private static final String WINDOWS_INPUT_METHODS = "imm32";
    private static final long NONE = 0;

    /** The input method parted from the window. One that will not be parted is refused, by name. */
    void kept() {
        if (Platform.get() != Platform.WINDOWS) {
            return;
        }
        try (SharedLibrary methods = APIUtil.apiCreateLibrary(WINDOWS_INPUT_METHODS)) {
            final long parting = APIUtil.apiGetFunctionAddress(methods, "ImmAssociateContextEx");
            if (JNI.invokePPI(glfwGetWin32Window(glass.raw()), NONE, 0, parting) == 0) {
                throw new Refused("ImmAssociateContextEx", "the input method could not be parted from "
                        + glass.name());
            }
        }
    }

    /** Whether the window has no input method: asked of the system, which says which one it has. */
    boolean plain() {
        if (Platform.get() != Platform.WINDOWS) {
            return true;
        }
        try (SharedLibrary methods = APIUtil.apiCreateLibrary(WINDOWS_INPUT_METHODS)) {
            final long window = glfwGetWin32Window(glass.raw());
            final long has = JNI.invokePP(window, APIUtil.apiGetFunctionAddress(methods, "ImmGetContext"));
            if (has != NONE) {
                JNI.invokePPI(window, has, APIUtil.apiGetFunctionAddress(methods, "ImmReleaseContext"));
            }
            return has == NONE;
        }
    }
}
