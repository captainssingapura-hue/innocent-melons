package com.example.fxsuite.taxonomy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What the compiler refuses of a tree, proved by compiling it: a node well placed compiles; a branch at the second
 * level under a root, a leaf under a leaf, a node with two parents, and a node whose parent is not the one it says, do
 * not.
 */
class RefusedByTheCompilerTest {

    private static final String HEAD = "import com.example.fxsuite.taxonomy.*;\nclass T {\n"
            + "record Root() implements L0 {}\n"
            + "record Control() implements L1<Root> { public Root parent() { return new Root(); } }\n"
            + "record Button() implements L2<Control> { public Control parent() { return new Control(); } }\n"
            + "record Primary() implements Leaf<Button> { public Button parent() { return new Button(); } }\n";

    @TempDir
    Path out;

    private boolean compiles(final String body) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final String classes = Path.of(Node.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                .replaceFirst("^/([A-Za-z]:)", "$1")).toString();
        final JavaFileObject source = new SimpleJavaFileObject(URI.create("string:///T.java"),
                JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                return HEAD + body + "\n}\n";
            }
        };
        return compiler.getTask(null, null, diagnostic -> { }, List.of("-cp", classes, "-d", out.toString()), null,
                List.of(source)).call();
    }

    @Test
    void aNodeWellPlacedCompiles() {
        assertTrue(compiles(""), "the tree itself");
        assertTrue(compiles("record Card() implements Leaf<Control> {"
                + " public Control parent() { return new Control(); } }"),
                "a leaf under a branch at any level");
    }

    @Test
    void aNodeInTheWrongPlaceDoesNot() {
        assertFalse(compiles("record Deep() implements L2<Root> { public Root parent() { return new Root(); } }"),
                "a branch at the second level under a root");
        assertFalse(compiles("record Under() implements Leaf<Primary> {"
                + " public Primary parent() { return new Primary(); } }"),
                "a leaf under a leaf");
        assertFalse(compiles("record Two() implements Leaf<Button>, L1<Root> {"
                + " public Button parent() { return new Button(); } }"),
                "a node with two parents");
        assertFalse(compiles("record Lies() implements Leaf<Button> {"
                + " public Control parent() { return new Control(); } }"),
                "a node whose parent is not the one it says");
    }
}
