package org.example;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the CLI shell of spec &sect;4: welcome banner, unknown-command
 * handling and exit, driven by scripted stdin/stdout (AC-3, AC-5).
 */
public class RegisterSystemTest {

    @Test
    void greetsOnStartAndSaysGoodbyeOnExit() {
        StringWriter out = runScript("exit");

        String text = out.toString();
        assertTrue(text.contains(
                        "Welcome to Kwik E-Smart, your favorite vegan grocery shop"),
                text);
        assertTrue(text.contains("Type 'help' to see available commands."),
                text);
        assertTrue(text.contains("Goodbye!"), text);
    }

    @Test
    void unknownCommandIsReportedAndProgramKeepsRunning() {
        StringWriter out = runScript("do something silly", "exit");

        String text = out.toString();
        assertTrue(text.contains("Unknown command. Type 'help'."), text);
        assertTrue(text.contains("Goodbye!"),
                "program must still reach the exit command");
    }

    private StringWriter runScript(String... commands) {
        StringBuilder script = new StringBuilder();
        for (String command : commands) {
            script.append(command).append('\n');
        }
        StringReader in = new StringReader(script.toString());
        StringWriter out = new StringWriter();
        new RegisterSystem(in, out).run();
        return out;
    }
}
