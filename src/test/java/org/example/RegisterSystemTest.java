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
    void welcomeShowsTheMainWaysIn() {
        StringWriter out = runScript("exit");

        String text = out.toString();
        assertTrue(text.contains("enter store"),
                "welcome must advertise 'enter store': " + text);
        assertTrue(text.contains("run pos"),
                "welcome must advertise 'run pos': " + text);
    }

    @Test
    void everyOutputBlockIsFollowedByABlankLine() {
        StringWriter out = runScript("unknown thing", "exit");

        String text = out.toString();
        assertTrue(text.contains(
                        "To get started: 'enter store' or 'run pos'.\n\n"),
                "blank line after the welcome block: " + text);
        assertTrue(text.contains(
                        "Unknown command. Type 'help'.\n\n"),
                "blank line after every command response: " + text);
    }

    @Test
    void unknownCommandIsReportedAndProgramKeepsRunning() {
        StringWriter out = runScript("do something silly", "exit");

        String text = out.toString();
        assertTrue(text.contains("Unknown command. Type 'help'."), text);
        assertTrue(text.contains("Goodbye!"),
                "program must still reach the exit command");
    }

    @Test
    void membershipRegistrationPromptsAndCongratulates() {
        StringWriter out = runScript("enter store", "create basket",
                "add soy-milk", "to pos", "register membership",
                "Ann Example", "19900101", "900101-0000", "exit");

        String text = out.toString();
        assertTrue(text.contains("Please enter your name:"), text);
        assertTrue(text.contains("Please enter your birthday:"), text);
        assertTrue(text.contains("Please enter your personnr:"), text);
        assertTrue(text.contains(
                "Congratulations! You can now use discounts. Your member "
                        + "number is"), text);
    }

    @Test
    void membershipPromptsAppearBeforeEachAnswer() {
        String text = runRecordingScript("enter store", "add soy-milk",
                "to pos", "register membership", "Ann Example", "19900101",
                "900101-0000", "exit");

        int name = text.indexOf("Please enter your name:");
        int birthday = text.indexOf("Please enter your birthday:");
        int personnr = text.indexOf("Please enter your personnr:");
        assertTrue(name >= 0 && birthday > name && personnr > birthday, text);
        assertTrue(flushedBetween(text, name, birthday),
                "name prompt must flush before the answer is read: " + text);
        assertTrue(flushedBetween(text, birthday, personnr),
                "birthday prompt must flush before the answer is read: "
                        + text);
    }

    /** True when a recorded flush occurs strictly between two offsets. */
    private static boolean flushedBetween(String text, int from, int to) {
        int flush = text.indexOf("<flush>", from);
        return flush >= 0 && flush < to;
    }

    /** StringWriter that marks every flush in the recorded output. */
    private static class RecordingWriter extends StringWriter {
        @Override
        public void flush() {
            getBuffer().append("<flush>");
            super.flush();
        }
    }

    private String runRecordingScript(String... commands) {
        StringBuilder script = new StringBuilder();
        for (String command : commands) {
            script.append(command).append('\n');
        }
        RecordingWriter out = new RecordingWriter();
        new RegisterSystem(new StringReader(script.toString()), out).run();
        return out.toString();
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
