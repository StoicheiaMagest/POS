# Refactor notes

Running log of places where implementing the spec (specs/pos-system.md)
changed existing behaviour and forced existing tests to be updated.
One entry per incident: what changed, which tests were touched, why it is OK.

- 2026-10-21 — Scanner -> BarcodeScanner (owner decision: scanner lives in
  the purchase occasion and only resolves barcodes). Deleted `Scanner.java`,
  `ScannerTest.java`, `BasketScannerIntegrationTest.java`; their behaviour
  (scanner adding to a basket) is explicitly out of the new spec. Replaced by
  `BarcodeScannerTest`. Commit 6208b6c.
- 2026-10-21 — `Item` constructor gained `fullDescription` (spec §6). All
  existing construction sites live in tests, so `UnitPricedArticleTest`,
  `WeighedItemTest`, `ScaleTest`, `ReceiptTest` were updated mechanically
  (extra argument only; assertions unchanged). Commit 5c6be7f.
- 2026-10-21 — `ArticleRegistry.addItem` now also indexes by name handle, so
  the pre-existing `ArticleRegistryTest.findByArticleNumberReturnsRegisteredItem`
  needed its `mock(Item.class)` to stub `getName()` (it previously never was
  called). Assertion unchanged.
- 2026-10-21 — `Card` + `PaymentCard` consolidated into one `PaymentCard`
  (id, issuer, linked account) per spec §6 backlog decision. Deleted
  `Card.java`/`CardTest.java`; the pay-success/pay-failure behaviour of
  `CardTest` is preserved in the rewritten `PaymentCardTest` (now with real
  `Account`s instead of mocks).
