# SPEC — Kwik E-Smart POS System (v1)

Status: IMPLEMENTED (v1, 2026-10-21 — all 29 ACs covered by tests; see git
history of `pos-system`). Assumptions A1–A5 stand as defaults.
Sources: owner's draft brief (see commit history of this file). Money handling
in `money code (reference)/` is reference material only; its conventions are
not adopted.

---

## 1. Overview

A command-line point-of-sale simulator for a vegan grocery shop. It models two
perspectives:

1. **The customer's journey** — entering the store, picking items into a
   basket (shopping list), then walking to the cash register.
2. **The POS system** — a purchase occasion at the register where the basket
   is scanned, an optional membership discount is applied, and payment is made
   by bank card.

Payment is **card-only**: money moves as account-to-account transfers from the
customer's card-linked account to the store's bank account. There is no cash
and no coupon handling in v1.

The whole system (code, output, documentation) is written in **English**.

## 2. Global conventions

- **C1 Commands are case-insensitive** (`ADD SOY-MILK` == `add soy-milk`) and
  surrounding whitespace is ignored. Item handles are case-insensitive too.
- **C2 Money is integer cents** end-to-end (existing convention). Never
  floating point.
- **C3 Displayed prices include VAT.** Items internally store a VAT-exclusive
  base price plus a `Vat` rate (basis points, half-up rounding — existing
  enum); the customer-facing price shown by every command is the
  VAT-inclusive amount.
- **C4 Price display format:** two decimals with a decimal point and a space
  before the currency code, e.g. `34.95 SEK`. *(Assumption A1: the brief mixed
  `34,95` and `39.90`; English output ⇒ decimal point.)*
- **C5 Unknown commands** always print `Unknown command. Type 'help'.` and
  never crash the program.
- **C6 Out-of-state commands** (see §3) print a hint telling the user which
  command to run first, and change nothing.
- **C7 Output spacing:** every output block (welcome banner, response to a
  command) is followed by one blank line, so consecutive outputs stay readable.
- **C8 Guided navigation:** at every state the program prints the commands
  relevant *there and now* — the user never needs prior knowledge of the
  command set to find the next step (welcome banner, store entry, register).

## 3. System states

The program is a single state machine. A customer is either *in the store* or
*at the register* — commands are only valid in their state.

```
BOOT ──enter store──▶ IN_STORE ──to pos / to cash-register──▶ AT_REGISTER
  │                     │  ▲                                     │
  │                     │  └──(basket ops)                        │ payment fully received
  └──run pos────────────┴──────────────────▶ AT_REGISTER(SCANNED) ▼
                                              EXIT ◀──exit──── DONE (receipt printed)
```

| State | Valid commands |
|---|---|
| BOOT (program start, before a flow) | `help`, `enter store`, `run pos`, `exit` |
| IN_STORE | `help`, `create basket`, `list store-items`, `add`, `remove`, `list basket-items`, `create card`, `to pos`, `to cash-register`, `exit` |
| AT_REGISTER | `help`, `scan items`, `apply discount`, `register membership`, `payment`, `use card`, `create card` (per §4.14), `exit` |
| DONE | `help`, `enter store`, `run pos`, `exit` |

- A basket is picked up automatically on `enter store`, so basket commands
  work immediately in IN_STORE (`create basket` only resets it).
- `scan items`, `apply discount`, `payment` require the basket to have been
  scanned (`scan items`) first; `apply discount` and `payment` also require a
  non-empty basket.
- Once payment has started (first successful `use card`), `apply discount` is
  no longer allowed.

## 4. Commands — behaviour and exact output

### 4.1 Program start (`./start`)

A launcher script named `start` at the repository root compiles and runs the
main program from the project root, in the spirit of `mvn test`:

```
$ ./start
Welcome to Kwik E-Smart, your favorite vegan grocery shop
Type 'help' to see available commands.
To get started: 'enter store' or 'run pos'.
```

The banner advertises the two main ways in (C8) and is followed by a blank
line (C7).

No new Maven dependencies: the script may use plain `mvn compile` + `java -cp`.

### 4.2 `help`

Lists every command with a one-line description. (Exact layout free; all
commands must appear.)

### 4.3 `enter store`

Creates the store (`Kwik E-Smart`, with a store bank account) and a customer,
and places the customer **in the store** *with a basket already picked up*.
The customer has: name, birthday, person number, zero or more payment cards,
and is **not** a member by default.

Output: `Welcome to Kwik E-Smart! You have picked up a basket. Go ahead and
pick your items.`

(The experience is natural: entering the store hands you a basket; no extra
command is needed before `add`.)

### 4.4 `create basket`

Resets to a fresh empty basket for the current customer (IN_STORE only). A
basket already exists from `enter store`; this command is only needed to
start over.

Output: `Basket has been created`

Repeating it resets to a new empty basket.

### 4.5 `list store-items`

Prints the assortment (§5) as a table with columns
`Name`, `Full Description`, `Unit`, `Price`:

```
Name           Full Description            Unit      Price
soy-milk       Alpro Soymilk               1 pc      34.96 SEK
apple          Apple                       per kg    24.90 SEK
```

`Unit` is `1 pc` for unit-priced articles and `per kg` for weighed goods.

### 4.6 `add <item> [<quantity>]`

Adds one article to the basket.

- Unit-priced article: no quantity argument. `add soy-milk` →
  `1 Alpro Soymilk added to basket`
- Weighed article: quantity is grams. `add apple 200` →
  `200g Apple added to basket`
- Unknown item handle → `Unknown item. Use 'list store-items'.`
- Quantity given for a unit-priced article, or missing/non-positive for a
  weighed article → error message, nothing added.

### 4.7 `remove <item>`

Removes **one** of the article from the basket (unit items: one piece;
weighed items: the whole weighed entry — Assumption A2).

Output: `One Alpro Soymilk has been removed from basket`

If the item is not in the basket → `That item is not in your basket.`

### 4.8 `list basket-items`

Lists every distinct article on its own line with its quantity:

```
The basket has the following items:
1       Alpro Soymilk
2       Tropicana Orange Juice
300g    Apple
```

Identical unit-priced articles are grouped and counted; weighed entries are
shown with their weight. Empty basket → `Your basket is empty.`

### 4.9 `to pos` / `to cash-register` (aliases)

Moves the customer to the register and creates a **PurchaseOccasion** binding
customer, basket and store account. Customer status becomes *at the register*.

Output: `At store register. You can now scan the items or register membership`

### 4.10 `scan items`

Simulates scanning every basket item one by one (automatic in the backend).
Prints one line per scanned article with the running VAT-inclusive total,
then a closing line:

```
1 Alpro Soymilk scanned           Total: 34.96 SEK
2 Tropicana Orange Juice scanned  Total: 104.86 SEK
300g Apple scanned                Total: 112.33 SEK
All items scanned. Go to "payment" or "apply discount"
```

Re-running after a completed scan → `All items have already been scanned.`
Empty basket → `Your basket is empty.`

Scanning is **not** a Basket concern: the `PurchaseOccasion` owns a barcode
scanner (`BarcodeScanner`) which resolves a scanned barcode to the sale
article; the occasion records the resulting sales lines and running total.
The Basket only holds what the customer picked (owner decision).

A barcode is an integer with information at fixed digit positions, like a
scale printout: values below 100000 are plain article numbers; larger ones
carry the weight in grams in their last five digits (`900300` = article 9
at 300 g). The scanner decodes it positionally (mod arithmetic inside its
methods) and returns the concrete sale item.

### 4.11 `register membership`

Allowed only at the register (AT_REGISTER). Prompts interactively:

```
Please enter your name: <entered name>
Please enter your birthday: <yyyymmdd>
Please enter your personnr: <personnr>
Congratulations! You can now use discounts. Your member number is <number>!
```

- Birthday must parse as `yyyymmdd`; otherwise
  `Invalid birthday format. Use yyyymmdd.` and the command aborts unchanged.
- Person number: any non-empty input (v1).
- Member number: generated, format `M` + 6 digits.
- Already a member → `You are already a member.`

### 4.12 `apply discount`

Applies a **20 % membership discount** to the amount due. Requires a scanned,
non-empty basket and no payment started.

- Member: `Discount has succeeded. Enjoy 20% off your purchase!`
- Not a member: `Applying discount failed. You need to be a member`
- Already applied: `A discount has already been applied to this purchase.`

Rounding: half-up to the nearest cent (existing `applyDiscount` logic).

### 4.13 `payment` and `use card <card-id> [<amount>]`

Payment can only be made by card (account-to-account transfer to the store
account). Requires a scanned, non-empty basket.

- `payment` → prints `Use card` followed by the customer's cards:
  `<id> <issuer> (balance: X.XX SEK)` and the amount due. If the customer has
  no card, it prints `You have no payment card. Use 'create card'.`
- `use card <card-id>` → pays the full remaining amount due:
  - Sufficient balance → transfer executes; print
    `Your card has enough balance`
  - Insufficient → **no** transfer; print
    `Your card has insufficient balance`
- `use card <card-id> <amount>` → partial payment of that amount (same two
  messages). Multiple cards may be combined until the due amount reaches zero.
- When the due amount reaches zero:

```
Payment completed. Thank you for shopping at Kwik E-Smart!
<receipt — see 4.15>
```

State becomes DONE; no further commands except starting a new flow or `exit`.

### 4.14 `create card [<amount>]`

Creates a payment card for the current customer with: a unique card id, a
**random issuer** among `MasterCard`, `Visa`, `Amex`, and a newly created bank
account holding `<amount>` (SEK, decimal input allowed; default 500.00 —
Assumption A3). Cards may be created in BOOT/IN_STORE/AT_REGISTER.

Output: `A new <issuer> card has been created with id <id>. Balance: X.XX SEK`

Cards may be created once a customer exists (IN_STORE or AT_REGISTER — at
the register this enables split payment when one card runs short). In BOOT
or DONE the command is refused per C6.

### 4.15 Receipt (printed on completed payment)

One receipt per purchase occasion (single renderer — consolidates the existing
duplicate receipt printing, see backlog): store name, one line per article
(quantity/weight, full description, price), discount line if applied, and the
total paid. Ends with `Thank you for shopping at Kwik E-Smart!`

### 4.16 `run pos` (auto mode)

Runs the whole flow setup automatically: creates store, a **random customer**
(random name/birthday, randomly member or not, one card with a random balance
that may be *below* the basket total so failure paths are reachable) and a
**random basket** (random subset of the assortment, random quantity/weight per
picked article). The customer starts AT_REGISTER with an unscanned basket —
the user then continues with `scan items`, `apply discount`, `payment`, …

```
Random customer created: <name> (member: yes|no)
Random basket created with <n> articles. Total: X.XX SEK
At store register. You can now scan the items or register membership
```

`run pos` is equivalent to `to pos` with everything generated automatically.

### 4.17 `exit`

Prints `Goodbye!` and terminates the program. Valid in every state.

## 5. Assortment (the "ItemList")

Twelve vegan grocery articles, seeded into the article registry at startup
(existing `ArticleRegistry` serves as the ItemList data class). Prices shown
are VAT-inclusive per §C3; food at 12 %, non-food at 25 %. Each displayed
price must be exactly reachable from an integer VAT-exclusive base price
(half-up rounding), so soy-milk and tofu are 34.96/27.96 rather than .95.

| Name (handle) | Full Description | Unit | Price |
|---|---|---|---|
| soy-milk | Alpro Soymilk | 1 pc | 34.96 SEK |
| oat-drink | Alpro Oat Drink | 1 pc | 32.95 SEK |
| orange-juice | Tropicana Orange Juice | 1 pc | 29.95 SEK |
| hummus | Hummus Original 200g | 1 pc | 24.95 SEK |
| tofu | Organic Firm Tofu | 1 pc | 27.96 SEK |
| black-beans | Black Beans 400g | 1 pc | 19.95 SEK |
| dark-chocolate | Maraboo Dark Chocolate 100g | 1 pc | 22.95 SEK |
| candle | Scented Candle Vanilla | 1 pc | 49.95 SEK |
| apple | Apple | per kg | 24.90 SEK |
| banana | Banana | per kg | 15.90 SEK |
| vegan-cheese | Vegan Cheese Slices | per kg | 199.00 SEK |
| olives | Marinated Olives | per kg | 149.50 SEK |

## 6. Data model requirements (reuse existing classes, fill gaps)

| Concept | Existing class | Gap to fill |
|---|---|---|
| CLI loop, command parsing | `RegisterSystem` | Full command set; case-insensitivity; injectable in/out for tests |
| Assortment / ItemList | `ArticleRegistry` | Lookup by name handle (currently only article number); seed §5 |
| Articles | `Item`, `UnitPricedArticle`, `WeighedItem` | Add full description + name handle fields |
| Basket | `Basket` | Grouped quantity listing; remove-one semantics |
| Customer | `Customer` | Add birthday, cards, status (in store / at register), member number |
| Payment card | `Card` **and** `PaymentCard` | Consolidate into one class: issuer name, id, linked `Account` (backlog item) |
| Accounts/transfers | `Account` | Reuse as-is (`transfer` already returns success/failure) |
| Money | `Money`, `money code (reference)/` | Reference only; keep integer-cents convention |
| VAT | `Vat` | Reuse as-is (basis points, half-up) |
| Purchase occasion | `PurchaseOccasion` | Track amount due, discount applied once, payment progress |
| Discount | `Discount`, `applyDiscount` | v1 uses the membership 20 % rule only; rest stays backlog |
| Receipt | `Receipt` (single renderer) | English output incl. discount line |
| Store | **new class** | Name + store bank account |
| Scanning/weighing | `Scanner` → renamed `BarcodeScanner`, `Scale` | `BarcodeScanner` is **owned by `PurchaseOccasion`** and resolves barcodes to registry articles; Basket stays scanner-free (owner decision, backlog rename adopted) |

## 7. Acceptance criteria

Each criterion is independently testable. CLI-level criteria are tested by
feeding scripted input and capturing output (no real stdin in unit tests);
domain rules are tested at class level. Randomness (§4.16, §4.14 issuer) must
come from an injectable source so tests are deterministic.

**Program & commands**
1. `./start` from the repo root compiles and launches the program, printing
   the welcome line of §4.1.
2. Every command is accepted in any letter case; extra surrounding whitespace
   is ignored.
3. An unknown command prints the C5 message and the program keeps running.
4. `help` lists all commands of §4.
5. `exit` terminates from any state with `Goodbye!`.

**Flows & states**
6. `enter store` creates store + non-member customer in IN_STORE **with a
   basket already picked up**; `add` works immediately without any prior
   `create basket`.
7. `run pos` creates store, random customer (member status random) and a
   non-empty random basket with random quantities, starting AT_REGISTER; the
   three output lines of §4.16 appear; given a fixed seed the result is
   reproducible.
8. Basket commands are rejected at the register; register commands are
   rejected in the store (C6 messages).
9. `to pos` and `to cash-register` behave identically and create a purchase
   occasion; the customer's status becomes *at the register*.

**Basket**
10. `create basket` prints `Basket has been created` and yields an empty
    basket (`getTotalPrice == 0`).
11. `list store-items` shows all twelve articles with the four columns of §4.5.
12. `add soy-milk` adds one piece and prints
    `1 Alpro Soymilk added to basket`; basket total increases by that article's
    inclusive price.
13. `add apple 200` adds 200 g and prints `200g Apple added to basket`; the
    line price equals `pricePerKg × grams / 1000` (integer cents) plus VAT.
14. Adding an unknown handle, a quantity on a unit article, or a
    missing/non-positive weight on a weighed article changes nothing and
    prints the matching error.
15. `remove soy-milk` removes exactly one piece and prints the §4.7 line;
    removing an absent item prints `That item is not in your basket.`
16. `list basket-items` groups duplicates (`3  Alpro Soymilk`) and shows
    weighed entries as `<n>g`; empty basket prints `Your basket is empty.`

**Scanning & discount**
17. `scan items` prints one line per article with the running inclusive total
    and the closing line of §4.10; totals are cumulative in scan order.
18. `apply discount` for a member reduces the amount due by 20 % (half-up) and
    prints the success message; for a non-member it prints the failure message
    and changes nothing.
19. A second `apply discount` prints the already-applied message; discount
    after a successful `use card` is refused.

**Membership**
20. `register membership` works only at the register; on valid input the
    customer becomes a member, gets an id matching `M\d{6}`, and the §4.11
    congratulations line appears.
21. A malformed birthday (`not yyyymmdd`) prints the invalid-format message
    and leaves the customer a non-member.

**Cards & payment**
22. `create card 300` adds a card with a random issuer from {MasterCard,
    Visa, Amex}, a unique id, and an account balance of exactly 300.00 SEK;
    output matches §4.14.
23. `payment` without a scanned basket is refused (C6); with cards it prints
    `Use card`, the card list and the amount due; without cards it prints the
    no-card message.
24. `use card <id>` with sufficient balance transfers the full due amount from
    the customer account to the store account (both balances verified) and
    prints `Your card has enough balance`.
25. `use card <id>` with insufficient balance transfers nothing and prints
    `Your card has insufficient balance`.
26. Split payment: partial `use card <id> <amount>` followed by a second card
    covers the remainder; when due reaches zero the completion message and
    receipt (§4.15) print, state becomes DONE.
27. The receipt lists each article with quantity/weight and price, the
    discount line when one was applied, and the total paid.

**End-to-end**
28. Happy path (fixed seed): `run pos` → `scan items` → `apply discount`
    (member) → `payment` → `use card <id>` ends with a correct receipt whose
    total equals basket total minus 20 % discount, and the store account grew
    by exactly that amount.
29. Failure path: non-member runs the same flow; `apply discount` fails; full
    payment still succeeds.

## 8. Edge cases (must be handled, message wording may vary)

- Every command called in the wrong state or before its prerequisite (§3).
- Empty basket at `to pos`, `scan items`, `payment`.
- Zero/negative/non-numeric quantity arguments; non-integer card amounts.
- Paying when already DONE; commands after payment completion.
- `create basket` twice (reset semantics); same weighed article added twice
  with different weights (two separate lines in the listing).
- Card id that does not exist → error message, nothing transferred.
- Discount rounding on odd cent amounts (half-up).
- Very large quantities: weight per line capped at 10 000 g → friendly error.

## 9. Out of scope (v1)

- Cash payments and coupons (card-only per owner decision).
- Senior discount, 3-for-2 and other multi-buy discounts (`Discount` class
  remains backlog).
- Stock/inventory levels, replenishment, article prices editing at runtime.
- Multiple simultaneous customers or registers; persistence between runs.
- GUI, online payments, receipts to file/email.

## 10. Assumptions (owner to confirm)

- **A1** Price display uses a decimal point (`34.95 SEK`).
- **A2** `remove <weighed item>` removes the whole weighed line; for unit
  items it removes one piece.
- **A3** `create card` without an amount defaults to 500.00 SEK.
- **A4** Displayed prices are VAT-inclusive (§C3).
- **A5** `run pos` random customer may start as a member (random); the random
  card balance may be insufficient so payment-failure paths are exercised.
