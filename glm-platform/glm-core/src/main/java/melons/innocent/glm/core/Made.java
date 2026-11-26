package melons.innocent.glm.core;

/**
 * What a part is made of as it is designed, and whether its skin is bare, as a casting's is, so what it is made of is
 * seen; or coated, painted or dipped, so its coat is seen whatever it is made of.
 *
 * @param material what it is made of
 * @param bare     whether its skin is bare
 */
public record Made(Material material, boolean bare) {

    public Made {
        if (material == null) {
            throw new IllegalArgumentException("a part is made of something");
        }
    }
}
