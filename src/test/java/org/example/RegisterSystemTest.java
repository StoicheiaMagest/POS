
package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterSystemTest {

    @Test
    void exits_when_user_enters_exit() {
        String output = runRegisterSystem("EXIT\n");

        assertTrue(output.contains("Register starting"));
        assertTrue(output.contains("The following commands are availible:"));
        assertTrue(output.contains("* Exit"));
        assertTrue(output.contains("Register shutting down"));
    }

    @Test
    void prints_error_when_user_enters_unknown_command() {
        String output = runRegisterSystem("UNKNOWN\nEXIT\n");

        assertTrue(output.contains("Error, try again!"));
        assertTrue(output.contains("Register shutting down"));
    }

    @Test
    void handles_scan_item_command() {
        String output = runRegisterSystem("SCAN ITEM\nEXIT\n");

        assertTrue(output.contains("Register starting"));
        assertTrue(output.contains("Register shutting down"));
        assertTrue(!output.contains("Error, try again!"));
    }

    @Test
    void runs_demo_when_user_enters_demo() {
        boolean[] demoRan = {false};

        String output = runRegisterSystem("DEMO\nEXIT\n", scanner -> demoRan[0] = true);

        assertTrue(demoRan[0]);
        assertTrue(output.contains("* Demo"));
        assertTrue(!output.contains("Error, try again!"));
    }

    @Test
    void demo_reads_its_input_from_the_registers_scanner() {
        String[] line = {null};

        String output = runRegisterSystem("DEMO\nhello\nEXIT\n", scanner -> line[0] = scanner.nextLine());

        assertEquals("hello", line[0]);
        assertTrue(output.contains("Register shutting down"));
    }

    @Test
    void demo_is_unknown_command_without_demo() {
        String output = runRegisterSystem("DEMO\nEXIT\n");

        assertTrue(output.contains("Error, try again!"));
        assertTrue(!output.contains("* Demo"));
    }

    private String runRegisterSystem(String commands) {
        return runRegisterSystem(commands, null);
    }

    private String runRegisterSystem(String commands, Consumer<Scanner> demo) {
        Scanner input = new Scanner(commands);
        RegisterSystem register = new RegisterSystem(input, demo);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            register.start();
        } finally {
            System.setOut(originalOut);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void main_starts_and_shuts_down_register() {
        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;

        java.io.ByteArrayInputStream input =
                new java.io.ByteArrayInputStream("EXIT\n".getBytes());

        java.io.ByteArrayOutputStream output =
                new java.io.ByteArrayOutputStream();

        try {
            System.setIn(input);
            System.setOut(new java.io.PrintStream(output));

            RegisterSystem.main(new String[]{});

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String result = output.toString();

        assertTrue(result.contains("Register starting"));
        assertTrue(result.contains("Register shutting down"));
    }

}
