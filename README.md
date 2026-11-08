# wiktio-peggy-nary
PEG parser for german Wiktionary

The goal of this project is to parse dump of the page http://de.wiktionary.org to a form, that can be easily used by computer programms. Technique chosen for this task is Parsing Expression Grammar. The parser is generated from a grammar definition using the Mouse tool (http://www.romanredz.se/freesoft.htm). This declarative approach allows the logic behind parsing to be easier grasped, as if it were implemented directly in Java or another general purpose programming language (probably with some regular expressions buried in the source code). It also make it easier to adapt the parser to changing templates used by Wiktionary.

Target representation will be JSON, stored in some document oriented database, probably CouchDB (http://couchdb.apache.org/).

## How to Run

This app parses a German Wiktionary dump and indexes the entries into Elasticsearch. The steps below are the exact steps to get it running end-to-end.

### Prerequisites

- **JDK 25** (e.g. Temurin or Corretto). The app targets Java 25; the build requires JDK 25 (older JDKs cannot compile Java 25 bytecode). `java` is not on the default PATH — point `JAVA_HOME` at a JDK 25 for every Gradle/Java invocation:
  ```bash
  export JAVA_HOME=/path/to/jdk25   # e.g. ~/.jdks/jdk-25.0.4.1+1
  ```
- **Docker** (to run Elasticsearch).
- **Gradle** (the bundled wrapper `./gradlew` is sufficient).

### 1. Build the sibling project

This project depends on `wiktionary-dump-browser` (a sibling project), resolved from the local Maven repository (`mavenLocal()`). Build and install it first:

```bash
cd ../wiktionary-dump-browser
./gradlew build publishToMavenLocal
```

### 2. Build this project

```bash
cd ../wiktio-peggy-nary
./gradlew build
```

This compiles the app, runs the 76-test suite, and produces `build/libs/wiktiopeggynary-1.0-SNAPSHOT.jar`.

### 3. Start Elasticsearch

The app hardcodes the Elasticsearch connection: `localhost:9200`, user `elastic`, password `changeme` (in the sibling project's `ElasticsearchRepository`). Start an ES 8.x instance. The sibling project ships a `docker-compose.yml`:

```bash
cd ../wiktionary-dump-browser
docker compose up -d
```

> **Hint:** If `docker compose` fails with `unknown command: docker compose` or `unknown shorthand flag`, the Docker Compose plugin is missing. Install it first (e.g. `sudo apt-get install docker-compose-plugin`).

Or run the ES image directly (no Compose needed):

```bash
docker run -d --name elasticsearch1 -p 9200:9200 \
  -e ES_JAVA_OPTS="-Xms512m -Xmx512m" \
  -e ELASTIC_PASSWORD=changeme \
  docker.elastic.co/elasticsearch/elasticsearch:8.15.1
```

Verify ES is up:

```bash
curl -s -u elastic:changeme localhost:9200
```

### 4. Download the German Wiktionary dump

The app expects a MediaWiki `pages-articles` dump (the `pages-articles-multistream.xml.bz2`). Find and download the latest dump from Wikimedia:

```bash
# Find the latest dump date:
DATE=$(curl -s https://dumps.wikimedia.org/dewiktionary/ | grep -oE '20[0-9]{6}' | tail -1)

# Download the pages-articles dump:
wget "https://dumps.wikimedia.org/dewiktionary/${DATE}/dewiktionary-${DATE}-pages-articles-multistream.xml.bz2"
```

The current dump (20260801) is ~302 MB compressed, ~2 GB decompressed.

### 5. Decompress the dump

The app reads plain XML (not `.bz2`). Decompress:

```bash
bunzip2 dewiktionary-${DATE}-pages-articles-multistream.xml.bz2
```

### 6. Run the app

The app deletes/recreates the `dewiktionary_entries` index on every run. Run it with the `runParser` Gradle task (sequential) or `runParserParallel` (one parser thread per CPU core), passing the dump file:

```bash
./gradlew runParser -PdumpFile=dewiktionary-${DATE}-pages-articles-multistream.xml
./gradlew runParserParallel -PdumpFile=dewiktionary-${DATE}-pages-articles-multistream.xml
```

The full dump has ~300k entries and takes a long time to parse/index. Progress is logged to `wiktiopeggynary.log`.

### 7. View the results in Kibana

The docker-compose file also starts Kibana (a web client for Elasticsearch) on port 5601. Open `http://localhost:5601` and log in with the same credentials as Elasticsearch: username `elastic`, password `changeme`. The parsed entries are stored in the `dewiktionary_entries` index.

### Known issue

- **Full-dump indexing (re-validating)**: Previously, the full dump failed with a `NullPointerException` in the old ES 5.6 client's `BulkProcessor` (the `bulkProcessor` field was null). The client was upgraded to ES 8, which replaces `BulkProcessor` with manual bulk handling, so that specific NPE can no longer occur. A 50-entry subset indexes cleanly; a full-dump run is pending to confirm the fix at scale.
