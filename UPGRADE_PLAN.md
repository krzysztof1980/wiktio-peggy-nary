# Upgrade Plan: JDK + Libraries

This project hasn't been updated in a while. The JDK is 1.8 (EOL), the Gradle wrapper is 2.2 (2015), and most libraries are outdated. This plan upgrades the JDK and modernizes the dependencies. The JDK move is done in **two steps** to isolate variables: first to 17 (the foundation, with the Gradle 8 jump), then to 25 (the final LTS, with the Gradle 9 jump) after all library upgrades are verified.

## Current State

| Component | Current | Notes |
|-----------|---------|-------|
| JDK | 1.8 (Corretto) | EOL; newer JDKs break the build |
| Gradle wrapper | 2.2 | 2015; only supports Java 8 |
| Elasticsearch client | 5.6.0 | `elasticsearch-rest-high-level-client` (deprecated in ES 7, removed in ES 8) |
| Jackson | 2.5.3 | `jackson-databind` |
| Groovy | 2.4.12 | `groovy-all` |
| SLF4J | 1.7.12 | `slf4j-api` |
| Logback | 1.1.3 | `logback-classic` |
| JWKT (Mouse) | 1.0.1 | `de.tudarmstadt.ukp.jwktl:jwktl` (superseded by `org.dkpro.jwktl:dkpro-jwktl`) |
| Spock | 1.0-groovy-2.4 | Testing |
| JUnit | 4.11 | Testing |
| Commons | lang3 3.4, cli 1.3 | Minor |

## Target State

| Component | Target | Notes |
|-----------|--------|-------|
| JDK | **17** (intermediate) → **25** (final) | 17 first (isolate the JDK+Gradle upgrade), then 25 after all library upgrades |
| Gradle wrapper | **8.x** (with JDK 17) → **9.x** (with JDK 25) | Gradle 8 supports JDK 17; Gradle 9 supports JDK 25 |
| Elasticsearch client | 8.x | `org.elasticsearch:elasticsearch-java` (new official client) |
| Jackson | 2.20.x | `jackson-databind` |
| Groovy | 4.0.x | `org.apache.groovy:groovy` (not `groovy-all`) |
| SLF4J | 2.0.x | `slf4j-api` |
| Logback | 1.5.x | `logback-classic` |
| JWKT (Mouse) | 1.1.0 | `org.dkpro.jwktl:dkpro-jwktl` (new coordinates) |
| Spock | 2.4-groovy-4.0 | Requires Groovy 4+; **JUnit Platform** engine (needs `useJUnitPlatform()` + `junit-platform-launcher`). No "3.x" exists on Maven Central — latest is the `2.x-groovy-*` line |
| JUnit | 5.x | JUnit 5 |

## Phases

### Phase 1: JDK + Gradle Upgrade (1.8 → 17, Gradle 2.2 → 8.x)

1. **Install JDK 17** (Corretto or Temurin).
2. **Upgrade the Gradle wrapper** to 8.x (the current 2.2 only supports Java 8).
3. **Update `build.gradle`**:
   - `sourceCompatibility` and `targetCompatibility` → 17.
   - Replace the deprecated `maven` plugin with `maven-publish`.
   - Update the `sourceSets` (the Groovy compilation).
4. **Verify the build** (compile + tests). This proves the JDK+Gradle jump is clean before touching libraries.

### Phase 2: Library Upgrades (in order of risk, on JDK 17)

1. **Jackson**: 2.5.3 → **2.20.2** (low risk). ✅ **Done**
2. **SLF4J**: 1.7.12 → **2.0.18** (low risk). ✅ **Done**
3. **Logback**: 1.1.3 → **1.5.38** (low risk, requires SLF4J 2.x). ✅ **Done**
4. **JWKT (Mouse)**: 1.0.1 → **1.1.0** (medium risk; new coordinates `org.dkpro.jwktl:dkpro-jwktl`). ✅ **Done** — no source changes (the `IWiktionaryPageParser`/`WiktionaryDumpParser` API is byte-identical, same `de.tudarmstadt.ukp.jwktl` package). Excluded the sibling project's transitive `jwktl:1.0.1` so only the new `dkpro-jwktl:1.1.0` is on the classpath (avoids duplicate `de.tudarmstadt.ukp.jwktl.parser.*` classes).
5. **Groovy**: 2.4.12 → 4.0.x (medium risk; `groovy-all` → `org.apache.groovy:groovy`). ✅ **Done (pulled into Phase 1)** — the 2.4 runtime was incompatible with the Gradle-8-bundled-Groovy-3.x-compiled bytecode (`IncompatibleClassChangeError` on `IntStream.range`), so this was required to unblock Phase 1. Landed on `org.apache.groovy:groovy:4.0.11`.
6. **Spock**: 1.0-groovy-2.4 → **2.4-groovy-4.0** (medium risk; requires Groovy 4+). ✅ **Done (pulled into Phase 1)** — forced by the Groovy upgrade. Spock 2.4 is a JUnit Platform engine, so `test { useJUnitPlatform() }` + `testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.14.1'` were added (otherwise Gradle silently runs 0 tests).
7. **JUnit**: 4.11 → **5.14.1** (medium risk). ✅ **Done** — the `junit:junit:4.11` was a dead leftover (all tests are Spock, a JUnit Platform engine; no `org.junit` imports). Swapped for `org.junit.jupiter:junit-jupiter:5.14.1`, aligned with the existing `junit-platform-launcher:1.14.1`. 76 tests pass, 0 failures.
8. **Elasticsearch client**: 5.6.0 → 8.x (**high risk**; major API change; `elasticsearch-rest-high-level-client` → `elasticsearch-java`).

After each step (or at the end of the phase), run the test suite to confirm the upgrade is clean.

### Phase 3: JDK 17 → 25 + Gradle 8.x → 9.x (final jump)

After all library upgrades are verified on JDK 17, do the final jump to the newest LTS:

1. **Switch the JDK** to 25 (already installed).
2. **Upgrade the Gradle wrapper** to 9.x (supports JDK 25).
3. **Update `build.gradle`**: `sourceCompatibility` and `targetCompatibility` → 25.
4. **Verify the build** (compile + tests + app run). If anything breaks here, it's the JDK/Gradle jump (not the libraries), since the libraries were already verified on 17.

### Phase 4: Verification

1. **Run the test suite** (76 tests).
2. **Run the app** (subset) to verify the migration.
3. **Check the ES indexing** (the docs are indexed correctly).

### Phase 5: Groovy Elimination (2026-11) — ✅ Done

Replaced every use of Groovy with modern Java; Groovy is gone from both projects' sources, dependencies, and classpath (Gradle DSL stays Groovy-flavored).

- **`wiktio-peggy-nary` main**: 9 Groovy classes → Java.
  - `@ToString`/`@EqualsAndHashCode` + mutable properties on value types → **records**: `ItemNumber(List<String>)`, `KTemplate(List<Part>, suffix)` + `Part(text, separator)`, `LanguageVariant(variant, suffix)`, `WikipediaLink(pageTitle, linkText)` (`WiktionarySemantics` call sites rebuilt to constructor-built records).
  - Beans kept where mutation is real: `Meaning` (`addSubMeaning`, setter-built during parsing), `ReferenceWiktionaryEntry` (+ enum `fromReferenceName` via values() loop).
  - Groovy map literal → `Map.ofEntries` (`KontextShortcutMapper`); GStrings → concatenation + **text blocks** (`WiktionaryEntryEsRepository` mapping JSON); implicit closures → lambdas; Jackson checked exceptions wrapped in `UncheckedIOException` to keep the public API throws-free.
  - `build.gradle`: `groovy` plugin → `java`, joint-compilation `sourceSets` hack removed, dropped `groovy`, `spock-core`, `cglib`, `objenesis`.
- **`wiktio-peggy-nary` tests**: 8 Spock specs + 2 utils → **JUnit 5** (`*Spec` → `*Test`); `where:` tables → `@ParameterizedTest` (`@MethodSource`/`@CsvSource`); Groovy collection/cast idioms → streams/`get(i)`/casts.
- **`wiktionary-dump-browser`** (the ES base classes wiktio subclasses — the reason the Groovy runtime had to stay otherwise): 4 Groovy classes → Java (closures → lambdas, JSON bodies → text blocks, ES fluent lambdas kept as Java lambdas); `java` plugin; republished to mavenLocal (`publishToMavenLocal`), POM no longer carries Groovy.

**Result:** `clean build` green; **76 tests, 0 failures** — exact parity with the last Spock run; `groovy` absent from both compile and runtime classpaths; JSON/ES document shape unchanged (records keep the old class names, so `@JsonTypeInfo(MINIMAL_CLASS)` ids in indexed docs still resolve).

## Risks

- **Elasticsearch client** (5.6.0 → 8.x): The biggest risk. The `elasticsearch-rest-high-level-client` is deprecated/removed in ES 8. The new client (`elasticsearch-java`) has a different API. This requires code changes in:
  - `ElasticsearchRepository` (base class) — the client initialization.
  - `ElasticsearchIndexingRepository` (bulk processor) — the bulk requests.
  - `WiktionaryEntryEsRepository` (main project) — the indexing.
  - `WiktionaryDumpEsRepository` (sibling project) — the dump indexing.
- **JWKT (Mouse)**: The new coordinates (`org.dkpro.jwktl:dkpro-jwktl`) and possible API changes. The PEG parser generator might have changed. The jar is from ~2016-2020 and was never built against JDK 17/25, but it's plain Java so it should run.
- **Groovy**: 2.4 → 4.0 has some breaking changes (the `groovy-all` artifact is replaced by individual modules).
- **JDK 25 + Gradle 9 jump** (Phase 3): The newest toolchain. Isolated as a separate phase so any breakage is attributable to the JDK/Gradle jump, not the libraries.

## Order of Operations

1. **Phase 1**: JDK + Gradle upgrade to **17** (the foundation).
2. **Phase 2**: Library upgrades (in order of risk, starting with the low-risk ones).
3. **Phase 3**: JDK **17 → 25** + Gradle **8.x → 9.x** (the final jump, after all library upgrades are verified).
4. **Phase 4**: Verification (tests + app run).
5. **Phase 5**: Groovy elimination (both projects → pure Java, JUnit 5).

The ES client upgrade (Phase 2, step 8) is the most complex library change and should be done last within Phase 2 (after the other upgrades are verified). The JDK 25 jump (Phase 3) is the very last step, so it's isolated from the library work.

## Log Baseline (Regression Check)

The pre-upgrade log files are backed up to `log-backup-pre-upgrade-20260905/`. They were generated using the command 
```
./gradlew runParser -PdumpFile=../dewiktionary-20260901-pages-articles-multistream.xml
```
After the migration (Phase 4), re-run the app on the same dump and compare:

| Log file | What it tracks | Baseline | Regression signal |
|----------|---------------|----------|-------------------|
| `erroneous_wiktionary_entries.log` | Entries that failed to parse | 112 lines | More entries failing after the upgrade = parser regression |
| `unknown_templates.log` | Templates the parser doesn't recognize | 405 lines | More unknown templates = grammar regression |
| `parser.log` | Parser trace | 3798 lines | Diff for behavioral changes |
| `wiktiopeggynary.log` | Main app log | 3820 lines | Diff for errors/warnings |
| `elasticsearch.log` | ES client log | 20 lines | Diff for indexing changes |

The `erroneous_wiktionary_entries.log` and `unknown_templates.log` are the key regression signals: if the upgraded parser (new JWKT/Groovy) fails to recognize entries or templates that the old one handled, the diff will show it.
