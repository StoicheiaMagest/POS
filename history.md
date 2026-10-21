# Project History

A narrative, high-level account of each development session, newest first.
AGENTS.md §7 requires this file to be updated at the end of every session
with a summary of what was accomplished.

---

## Session 6 — 2026-10-21 (late evening): Guided TUI — natural shopping, tap-to-pay

The owner asked for a friendlier terminal experience with one guiding
principle: *no knowledge in the head* — every state must print the commands
that matter there and now. Follow-ups shaped the details while work was in
flight: a blank line after each output block; `enter store` should *magically*
hand over a basket **and** a working payment card; `use card` must feel like
holding a card over a reader (never type an id — ids became backend-only);
and splitting must reflect reality, so charging several cards automatically
was explicitly rejected in favour of an announced `split order <n>` step.

Seven TDD cycles, each spec-anchored and pushed through
`scripts/commit-push.sh`: blank-line spacing plus a welcome banner that
advertises `enter store`/`run pos` (new conventions C7/C8); the automatic
basket on entry (old "create basket first" refusals deleted as unreachable);
the 500 SEK welcome card (the card-less state disappeared, so its message
and test went too); contextual command lists at entry, at the register and
at the end of scanning; tap-style `use card` where one tap charges exactly
one card — the backend picks the first that covers the charge — with the
amount form `use card 30.00` for partial taps; `split order <n>` dividing
the due into whole-cent parts (extra cents to the first parts) paid by
successive taps, refusing re-splits, late splits, discounts after splitting
and amount-taps mid-split; and `print receipt` for re-viewing a finished
purchase's receipt.

Incidents worth recording: the owner had to point out (repeatedly) that
commits must go through `scripts/commit-push.sh` — that rule is now written
into AGENTS.md §6 so it cannot be missed again; VAT half-up rounding made a
10 kg cheese line land on 1990.02 rather than 1990.00, caught by the suite;
and two early test bugs were caught by watching them fail for the *wrong*
reason (mutating the basket after `to pos`, asserting between two taps);
and right after the session's wrap-up the owner found that the membership
prompts all appeared *after* the answers — unflushed `print` calls, fixed by
flushing each prompt before reading (reproduced by a flush-recording test).
ACs 6 and 23–26 were rewritten to the new behaviour and ACs 30–31 added;
the suite grew from 108 to 125 tests, all green. A scripted end-to-end run
of `./start` shows a first-time visitor shopping, tapping once and printing
the receipt without ever consulting `help`.

## Session 5 — 2026-10-21 (evening): Barcode rework and scanAll simplification

The owner flagged `PurchaseOccasion.scanAll` as messy and asked for small,
modular methods with the barcode logic pushed into the scanner, modelling a
scale-printout label whose barcode carries both article and weight. Along the
way the owner set two constraints: no data types or interfaces may change,
and extraction of barcode information must happen inside methods (substring
or modular arithmetic — either is fine).

Two TDD cycles delivered it. The `BarcodeScanner` (whose `scan(int)`
signature was kept) now decodes barcodes positionally: values below 100 000
are plain article numbers, larger ones carry grams in their last five digits
(`900300` = apple at 300 g), with the decoding split into one-line private
helpers and a matching `barcodeOf(Item)` encoder beside them. A new small
class `ScanLedger` took over the grouping and running-total bookkeeping, so
`scanAll` collapsed to the plain for-loop the owner envisioned. All 108
tests green; no existing test had to change (so no refactor-note); spec §4.10
anchored in the same commit. This session also introduced `history.md` and
the rule that it is updated at the end of every session.

## Session 4 — 2026-10-21: Full TDD implementation of POS v1

With the spec approved, the whole system was implemented in ~30 small
red-green-commit cycles on `pos-system`, each pushed via a new
`scripts/commit-push.sh` helper that refuses to run off the protected branch
and gates every commit on a fully green `mvn test`. The owner's mid-session
decision separated the scanner from the basket: `Scanner` became
`BarcodeScanner`, owned by the purchase occasion, resolving barcodes only.

The domain was built first (barcode scanner, article descriptions and name
handles, the seeded twelve-article registry, basket grouping/removal, the
consolidated `PaymentCard`, the extended `Customer` with membership and
status, the new `Store`, price formatting, the occasion's scan/discount/
split-payment flow, and a single English receipt renderer), then the command
layer: a pure, testable `PosSession` state machine holding every command and
its exact output strings, wrapped by a thin injectable-I/O `RegisterSystem`.
The CLI was completed command by command — basket handling, register flow,
membership prompts with strict birthday validation, random card issuing,
payments, seeded-reproducible `run pos` — ending with end-to-end happy and
failure paths, the 10 kg weigh cap, a live-verified `./start` launcher, and
the spec marked implemented. All 29 acceptance criteria are covered by named
tests (106 green at close).

Behaviour-changing refactors that forced existing tests to move were logged
as they happened in `refactor-notes.md` (nine incidents), per the owner's
standing request, and each breaking commit says so in its message. The
AGENTS backlog was updated: junk imports, the Scanner rename, the Card/
PaymentCard and receipt duplicates, and system wiring are done; README
conflict markers, stray practice files and the surefire config remain open.

## Session 3 — 2026-10-21: Spec authored and agreed

The owner's Swedish draft brief was turned into an English specification at
`specs/pos-system.md`: a state machine (BOOT → IN_STORE → AT_REGISTER →
DONE), a full command reference with exact output strings, the twelve-article
assortment, a reuse-vs-gap map against every existing class, edge cases,
out-of-scope list (cash and coupons excluded — card-only payments) and 29
numbered acceptance criteria, each independently testable. Five assumptions
(A1–A5: decimal-point prices, weighed-remove semantics, default card amount,
VAT-inclusive display, seeded randomness for the auto mode) were flagged for
owner confirmation. The money reference folder was reviewed but kept as
reference only; the integer-cents convention stands.

## Session 2 — 2026-10-21: Working contract established

The repo's way of working was written down in `AGENTS.md`: spec-driven
development with strict TDD (red first, watched fail, minimal green), Sun
Java conventions, integer-cent money, commit hygiene tied to acceptance
criteria, and hard branch-safety rules for the `pos-system` branch. The
branch guard plugin was verified live.

## Session 1 — earlier: Repository analysis and guardrails

The existing skeleton (basket, items, VAT, accounts, cards, registry) was
analysed, pre-existing issues catalogued into a backlog, and a branch-guard
plugin installed so no agent can switch or push to protected branches. The
`pos-system` working branch was created and pushed.
