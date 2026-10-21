package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for the scan ledger: accumulates scanned sale items into grouped
 * display lines with running VAT-inclusive totals (spec &sect;4.10).
 */
public class ScanLedgerTest {

    @Test
    void groupsUnitArticlesIntoOneLineAndKeepsWeighedEntriesSeparate() {
        // Arrange: soy-milk 3496 incl, juice 2995 incl, apple 300 g 746 incl
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        ScanLedger ledger = new ScanLedger();

        // Act
        ledger.record(registry.findByName("soy-milk"));
        ledger.record(registry.findByName("orange-juice"));
        ledger.record(registry.findByName("orange-juice"));
        ledger.record(new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple"));

        // Assert
        List<ScanLine> lines = ledger.getLines();
        assertEquals(3, lines.size());
        assertEquals("1", lines.get(0).getQuantity());
        assertEquals("Alpro Soymilk", lines.get(0).getDescription());
        assertEquals(3496, lines.get(0).getRunningTotalCents());
        assertEquals("2", lines.get(1).getQuantity());
        assertEquals(3496 + 5990, lines.get(1).getRunningTotalCents());
        assertEquals("300g", lines.get(2).getQuantity());
        assertEquals(3496 + 5990 + 746, lines.get(2).getRunningTotalCents());
        assertEquals(3496 + 5990 + 746, ledger.getTotalCents());
    }

    @Test
    void emptyLedgerHasNoLinesAndZeroTotal() {
        ScanLedger ledger = new ScanLedger();

        assertEquals(List.of(), ledger.getLines());
        assertEquals(0, ledger.getTotalCents());
    }
}
