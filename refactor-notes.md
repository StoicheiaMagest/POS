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
- 2026-10-21 — `Customer` model replaced (spec §6): constructor is now
  (name, personId); membership is *joined* (`joinMembership`) rather than a
  constructor flag; senior status dropped as out of scope (§9). Old
  `CustomerTest` cases for the constructor flags were rewritten to cover the
  new behaviour. `PurchaseOccasionTest.createPurchase` only needed the new
  constructor call (its Swedish receipt tests are scheduled for replacement
  in the receipt-rendering cycle, spec §4.15).
- 2026-10-21 — `PurchaseOccasion` rebuilt around scanner ownership
  (owner decision): new constructor `(BarcodeScanner, Basket, Customer,
  Account)`, `scanAll()`/`isScanned()` per spec §4.10. The old Swedish
  `printReceipt()` and its three tests were deleted (superseded by the
  single English receipt renderer of spec §4.15, backlog dedup decision);
  replacement tests arrive with the receipt cycle. The untested legacy
  `applyDiscount(int)` is likewise dropped; membership-discount behaviour
  gets its own tested API in the discount cycle (spec §4.12).
- 2026-10-21 — `Receipt` rebuilt as the single English renderer of spec
  §4.15 (store name, scan lines with prices, discount line, total paid).
  Old constructor (receipt number/date/basket) and its `ReceiptTest` cases
  replaced; receipt number/timestamp are not part of v1 output.
- 2026-10-21 — `RegisterSystem` rewritten as a thin I/O shell with injectable
  Reader/Writer (spec §4.1, testability requirement). Its old empty
  placeholder test was replaced by real scripted-I/O tests; the old no-arg
  constructor and "Register starting" banner are gone.

