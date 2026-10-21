package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Random;

/**
 * Console shell of the POS: prints the welcome banner, feeds input lines to
 * a {@link PosSession} and prints the responses. All behaviour lives in the
 * session; this class is pure edge I/O (spec &sect;4.1).
 */
public class RegisterSystem {

    private final BufferedReader in;
    private final PrintWriter out;
    private final PosSession session;

    public RegisterSystem(Reader in, Writer out) {
        this.in = new BufferedReader(in);
        this.out = new PrintWriter(out, true);
        this.session = new PosSession(
                ArticleRegistry.withStandardAssortment(), new Random());
    }

    public static void main(String[] args) {
        new RegisterSystem(new InputStreamReader(System.in),
                new java.io.OutputStreamWriter(System.out)).run();
    }

    /**
     * Runs the interactive loop until exit or end of input.
     */
    public void run() {
        out.println("Welcome to Kwik E-Smart, your favorite vegan grocery shop");
        out.println("Type 'help' to see available commands.");
        String line;
        try {
            while (session.isRunning() && (line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                out.println(session.execute(line));
            }
        } catch (IOException e) {
            out.println("Input error: " + e.getMessage());
        }
    }
}
