# AGENTS.md — Working Contract for the POS Repository

This file is the binding agreement for how humans and AI agents work in this
repo. Development follows **Spec-Driven Development (SDD)** with **Test-Driven
Development (TDD)**. When a spec and this contract seem to conflict, the spec
wins for *what* to build; this contract wins for *how* we work.

References:
- Spec-driven development: https://www.ibm.com/think/topics/spec-driven-development
- Java style: `java-codeconventions-150003.pdf` (Sun Java Code Conventions)
- Team Git workflow: `README.md`

---

## 1. Branch safety (hard rules — non-negotiable)

1. Work **only** on the current working branch (`pos-system`). Never switch,
   create, delete, or push to any other branch. `main`/`master` are protected.
2. Commits and pushes to the current branch are allowed and expected.
3. Merging into `main` happens **only** via Pull Request on GitHub (see README).
4. If a task seems to require leaving the branch: stop and ask the human.
5. These rules are enforced technically by `.opencode/plugins/branch-guard.ts`.
   Never attempt to work around that guard.

## 2. Spec-driven development

1. **No spec, no code.** A feature is implemented only after its spec (or at
   minimum its acceptance criteria) has been agreed. Specs live in this repo as
   versioned Markdown (`specs/<feature>.md`) so they are part of history.
2. The spec is the **single source of truth** for the feature. Do not invent
   requirements, and do not silently assume behaviour that is not written down.
   If something is ambiguous or missing → ask before implementing.
3. A good spec describes *what*, not *how*, and contains:
   - Overview (one short paragraph)
   - Acceptance criteria — numbered, each independently testable
   - Inputs / outputs / data shapes
   - Edge cases
   - Out of scope
4. **Spec-anchored:** when agreed behaviour changes, the spec file is updated
   in the same commit as the change. Code and spec never drift apart.
5. Refactorings and pure cleanup that do not change specified behaviour do not
   need a new spec, but must keep every existing test green.
6. Avoid the over-engineering trap: refine a spec only until it is good enough
   to write the next test. Do not model every possible future.

## 3. TDD cycle (red → green → refactor)

For **every** function or behaviour, in this exact order:

1. **RED** — Write one failing test that expresses a single acceptance criterion.
   Run it (`mvn test -Dtest=<Class>#<method>`) and **confirm it fails for the
   expected reason**. Never skip watching it fail.
2. **GREEN** — Write the *minimum* code necessary to make that test pass.
   Nothing speculative, no extra features, no untested helpers (YAGNI).
3. **REFACTOR** — Improve naming/structure per §4 while all tests stay green.
4. **COMMIT** — Commit the completed cycle on the current branch (§6).

Rules that follow from this:
- A test never exists without being run, and an implementation never exists
  before its test.
- Work in small iterations — one behaviour at a time, never large chunks.
- One test = one behaviour. Name it to read as a specification of the outcome.
- If a bug is found, first write the test that reproduces it, then fix it.

## 4. Java code conventions

Style follows `java-codeconventions-150003.pdf`. Key points:

- **Naming:** classes/interfaces are nouns in `PascalCase`; methods and
  variables are verbs/objects in `camelCase`; constants are `UPPER_SNAKE_CASE`.
  Names are short but meaningful; no cryptic abbreviations.
- **Layout:** 4-space indentation (no tabs); ≤ ~80 characters per line; one
  statement per line; declarations at the top of blocks, initialised where
  declared.
- **Braces** around *all* control structures, even single statements; opening
  brace on the declaration line.
- **Encapsulation:** instance/class variables are private unless there is a
  concrete reason otherwise; no public mutable state.
- **No magic numbers** — named constants (except −1, 0, 1).
- **Javadoc** (`/** ... */`) on every class and public method; implementation
  comments only where the code cannot speak for itself. Prefer clearer code
  over explanatory comments.
- Parenthesise mixed-operator expressions; no embedded assignments.

Design principles (modularity):
- Small methods that do one thing; small classes with a single responsibility.
- **Low coupling, high cohesion**: collaborators are passed in rather than
  constructed deep inside; depend on behaviour (interfaces) where it decouples.
- Keep I/O (scanner, printing, console) at the edges; core logic must be
  pure and unit-testable without stdin/stdout.
- **Money is integer cents** everywhere (`long`/`int`). VAT uses basis points
  (`Vat` enum: 2500/1200/600) with half-up rounding. Never `double` for money.

## 5. Testing conventions

- Frameworks: **JUnit 6 + Mockito** (as declared in `pom.xml`); Java 25.
- Tests live in `src/test/java`, mirroring the main package, named `<Class>Test`.
- Structure every test as Arrange → Act → Assert; assert on behaviour/outcomes,
  not on implementation details.
- The **full suite must pass** (`mvn test`) before any commit is made.

## 6. Commit hygiene

0. **Commits and pushes go through `scripts/commit-push.sh`**, never raw
   `git commit`/`git push`:
   `scripts/commit-push.sh "<message>" <file> [file...]`
   It refuses to run off `pos-system`, requires the full test suite green
   (AGENTS.md §5), stages only the listed files, commits and pushes.
1. Stage only the files touched by this increment — never `git add .` or
   `git add -A` (team rule from README: don't sweep up other people's work).
2. One completed TDD cycle = one commit on the current branch, message format:
   `<type>(<scope>): <what>` where type is `test` | `feat` | `refactor` |
   `fix` | `chore` | `docs`, scope is the class/feature, and the subject ties
   back to the spec's acceptance criterion, e.g.
   `feat(Discount): AC-3 three-for-two applies per full triple`.
3. Push regularly to the current branch so history is shared.
4. Never rewrite published history (no force-push).

## 7. Definition of done (per increment)

- [ ] A spec acceptance criterion is covered by a named, passing test
- [ ] `mvn test` fully green
- [ ] Committed and pushed on the current branch only
- [ ] No unrelated changes in the diff; no dead or speculative code
- [ ] Spec file updated if agreed behaviour changed

## 7a. Session history (end-of-session duty)

At the end of **every working session**, `history.md` is updated with a
high-level narrative summary of what was accomplished in that session
(newest session first). It is a story of the project's evolution, not a
commit log: decisions taken, direction of work, and notable incidents —
written so a newcomer understands how the system came to be. Committed like
any other increment (with the session's last commit or its own `docs`
commit).

## 8. Things an agent must never do

- Touch, switch to, or push to `main`/`master`, or any non-current branch
- Implement behaviour before its test exists
- Code beyond the spec ("while I'm at it" features)
- Add dependencies without agreeing first
- Delete or overwrite files it did not create without asking
- Weaken or bypass this contract or the branch guard

## 9. Known backlog (pre-existing issues — fix via normal commits when agreed)

Resolved during the v1 implementation (see `refactor-notes.md`):
- ~~Junk/unused imports (`Customer.java`, `Receipt.java`)~~ (files rewritten)
- ~~Rename `Scanner` → `BarcodeScanner`~~ (done, scanner now owned by the
  purchase occasion per owner decision)
- ~~Consolidate duplicates: `Card` vs `PaymentCard`; `Receipt.print()` vs
  `PurchaseOccasion.printReceipt()`~~ (done)
- ~~Wire everything together into the running system once specs arrive~~
  (`./start`, spec v1 implemented)

Still open:
- `README.md` lines 124–133: unresolved merge-conflict markers
- Stray Git-practice files `123.txt`, `asd.txt`; stale `architecture.md`
- `pom.xml`: invalid `<source>/<target>` under surefire plugin
