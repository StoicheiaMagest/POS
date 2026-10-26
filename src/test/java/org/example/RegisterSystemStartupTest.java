package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.example.RegisterTestSupport.runMain;
import static org.junit.jupiter.api.Assertions.*;

// Starting RegisterSystem2 loads its articles from a file: the path in the first argument, or
// articles.txt in the folder the program is started from. A missing or invalid file prints an
// error naming the file and stops before any command is read.
public class RegisterSystemStartupTest {

    @TempDir
    Path folder;

    private String articleFile(String name, String content) throws IOException {
        Path file = folder.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    @Test
    void startsWithTheArticlesInTheFileGivenAsFirstArgument() throws IOException {
        String path = articleFile("my-articles.txt", """
                1;UNIT;Red;1000;FOOD
                2;UNIT;Blue;2500;STANDARD
                3;WEIGHED;Green;4000;BOOK
                """);

        String output = runMain(new String[] {path}, "ARTICLES");

        assertTrue(output.contains("Register starting"), output);
        assertTrue(output.contains("1  Red  11,2 SEK\n2  Blue  31,25 SEK\n3  Green  42,4 SEK/kg"), output);
    }

    @Test
    void theArticlesFromTheFileCanBeScannedAndPaidFor() throws IOException {
        String path = articleFile("my-articles.txt", "1;UNIT;Red;1000;FOOD\n");

        String output = runMain(new String[] {path}, "NEW", "SCAN 1", "PAY");

        assertTrue(output.contains("Red  11,2 SEK\nTotalt  11,2 SEK"), output);
    }

    @Test
    void discountsComeFromTheFile() throws IOException {
        String path = articleFile("my-articles.txt", "7;UNIT;Red;400;FOOD;3FOR2\n");

        String output = runMain(new String[] {path}, "NEW", "SCAN 7", "SCAN 7", "SCAN 7", "LIST");

        assertTrue(output.contains("3 for 2 Red  -4,48 SEK\nTotalt  8,96 SEK"), output);
    }

    @Test
    void articlesNotInTheFileAreUnknown() throws IOException {
        String path = articleFile("my-articles.txt", "1;UNIT;Red;1000;FOOD\n");

        String output = runMain(new String[] {path}, "NEW", "SCAN 111");

        assertTrue(output.contains("Error, Article not found: 111"), output);
    }

    @Test
    void readsNamesWithSwedishLettersAsUtf8() throws IOException {
        String path = articleFile("my-articles.txt", "1;UNIT;Smör;1000;FOOD\n");

        String output = runMain(new String[] {path}, "ARTICLES");

        assertTrue(output.contains("1  Smör  11,2 SEK"), output);
    }

    @Test
    void anEmptyFileStartsTheRegisterWithNoArticles() throws IOException {
        String path = articleFile("empty.txt", "# no articles yet\n");

        String output = runMain(new String[] {path}, "ARTICLES", "NEW", "SCAN 1");

        assertTrue(output.contains("Register starting"), output);
        assertTrue(output.contains("Error, Article not found: 1"), output);
        assertFalse(output.contains("111"), output); // none of the articles in articles.txt
    }

    @Test
    void aMissingFilePrintsAnErrorNamingTheFileAndStops() {
        String path = folder.resolve("does-not-exist.txt").toString();

        String output = runMain(new String[] {path}, "ARTICLES");

        assertTrue(output.contains("Error, could not read article file '" + path + "'"), output);
        assertFalse(output.contains("Register starting"), output);
        assertFalse(output.contains("Register shutting down") && output.contains("Error, Article"), output);
    }

    @Test
    void anInvalidFilePrintsTheLineErrorAndStops() throws IOException {
        String path = articleFile("bad.txt", """
                1;UNIT;Red;1000;FOOD
                2;UNIT;Blue;2500;FODD
                """);

        String output = runMain(new String[] {path}, "ARTICLES");

        assertTrue(output.contains("Error, article file '" + path + "' is invalid: Line 2: unknown VAT rate 'FODD'"),
                output);
        assertFalse(output.contains("Register starting"), output);
    }

    @Test
    void noCommandIsReadWhenTheFileIsBad() throws IOException {
        String path = articleFile("bad.txt", "not an article\n");

        String output = runMain(new String[] {path}, "NEW", "SCAN 1", "ARTICLES");

        assertFalse(output.contains("New order started"), output);
        assertFalse(output.contains("Scanned"), output);
    }

    @Test
    void theShippedArticleFileIsValid() throws IOException {
        // articles.txt in the project folder is what the register reads when no argument is given
        ArticleRegistry registry;
        try (Scanner file = new Scanner(new File("articles.txt"), "UTF-8")) {
            registry = ArticleRegistryLoader.load(file);
        }

        assertEquals(DiscountType.THREE_FOR_TWO, registry.findByArticleNumber(111).getDiscountType());
        assertEquals(DiscountType.TWO_KR_OFF, registry.findByArticleNumber(222).getDiscountType());
        assertInstanceOf(WeighedItem.class, registry.findByArticleNumber(333));
        assertTrue(registry.getAllItems().size() >= 3); // the file may hold more articles than these
    }
}
