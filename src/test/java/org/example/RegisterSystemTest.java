
package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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

    private String runRegisterSystem(String commands) {
        Scanner input = new Scanner(commands);
        RegisterSystem register = new RegisterSystem(input);

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
