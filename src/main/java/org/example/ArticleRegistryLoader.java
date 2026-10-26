package org.example;

import java.util.Scanner;

// Reads an article file into an ArticleRegistry. Format, one article per line:
// number;type;name;price;vat[;discount]
//   number   whole number greater than zero
//   type     UNIT or WEIGHED
//   price    whole öre excluding VAT, per kg for WEIGHED
//   vat      FOOD, STANDARD or BOOK
//   discount optional: 3FOR2 or 2KR_OFF (3FOR2 is not allowed on a WEIGHED article)
// Blank lines and lines starting with # are ignored. A bad line rejects the whole file with an
// IllegalArgumentException whose message starts with "Line <number>:".
public class ArticleRegistryLoader {

    private static final String SEPARATOR = ";";

    public static ArticleRegistry load(Scanner source) {
        ArticleRegistry registry = new ArticleRegistry();
        int lineNumber = 0;
        while (source.hasNextLine()) {
            lineNumber++;
            String line = source.nextLine().strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Item item = parseLine(lineNumber, line);
            if (registry.findByArticleNumber(item.getArticleNumber()) != null) {
                throw error(lineNumber, "duplicate article number " + item.getArticleNumber());
            }
            registry.addItem(item.getArticleNumber(), item);
        }
        return registry;
    }

    private static Item parseLine(int lineNumber, String line) {
        String[] fields = line.split(SEPARATOR, -1); // -1 keeps an empty last field
        if (fields.length < 5 || fields.length > 6) {
            throw error(lineNumber, "expected 5 or 6 fields separated by '" + SEPARATOR
                    + "' but found " + fields.length + ": " + line);
        }
        int number = parseNumber(lineNumber, fields[0].strip());
        boolean weighed = parseIsWeighed(lineNumber, fields[1].strip());
        String name = fields[2].strip();
        if (name.isEmpty()) {
            throw error(lineNumber, "name must not be empty");
        }
        int price = parsePrice(lineNumber, fields[3].strip());
        Vat vat = parseVat(lineNumber, fields[4].strip());
        DiscountType discount = fields.length == 6
                ? parseDiscount(lineNumber, fields[5].strip(), weighed)
                : DiscountType.NONE;

        if (weighed) {
            return new WeighedItem(name, price, vat, 0, number, discount);
        }
        return new UnitPricedArticle(name, price, vat, number, discount);
    }

    private static int parseNumber(int lineNumber, String text) {
        int number;
        try {
            number = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw error(lineNumber, "article number '" + text + "' is not a whole number");
        }
        if (number <= 0) {
            throw error(lineNumber, "article number must be positive");
        }
        return number;
    }

    private static boolean parseIsWeighed(int lineNumber, String text) {
        if (text.equalsIgnoreCase("UNIT")) {
            return false;
        }
        if (text.equalsIgnoreCase("WEIGHED")) {
            return true;
        }
        throw error(lineNumber, "unknown type '" + text + "', expected UNIT or WEIGHED");
    }

    private static int parsePrice(int lineNumber, String text) {
        int price;
        try {
            price = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw error(lineNumber, "price '" + text + "' is not a whole number of öre");
        }
        if (price <= 0) {
            throw error(lineNumber, "price must be greater than zero, found '" + text + "'");
        }
        return price;
    }

    private static Vat parseVat(int lineNumber, String text) {
        try {
            return Vat.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw error(lineNumber, "unknown VAT rate '" + text + "'");
        }
    }

    private static DiscountType parseDiscount(int lineNumber, String text, boolean weighed) {
        if (text.isEmpty()) {
            return DiscountType.NONE;
        }
        if (text.equalsIgnoreCase("3FOR2")) {
            if (weighed) {
                throw error(lineNumber, "3FOR2 cannot be used on a weighed article");
            }
            return DiscountType.THREE_FOR_TWO;
        }
        if (text.equalsIgnoreCase("2KR_OFF")) {
            return DiscountType.TWO_KR_OFF;
        }
        throw error(lineNumber, "unknown discount '" + text + "'");
    }

    private static IllegalArgumentException error(int lineNumber, String message) {
        return new IllegalArgumentException("Line " + lineNumber + ": " + message);
    }
}
