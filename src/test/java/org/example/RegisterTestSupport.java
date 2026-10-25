package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

// Shared by the RegisterSystem2 tests. Not a test class itself.
final class RegisterTestSupport {

    private RegisterTestSupport() {
    }

    // Runs RegisterSystem2 with the given registry and commands, returns everything it printed.
    static String run(ArticleRegistry registry, String... commands) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            String input = String.join("\n", commands) + "\nEXIT\n";
            // System.in must be set before the register is created, it reads it in its constructor
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new RegisterSystem2(registry, new Account(0)).start();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
