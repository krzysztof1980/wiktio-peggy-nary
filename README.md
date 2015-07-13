# wiktio-peggy-nary
PEG parser for german Wiktionary

The goal of this project is to parse dump of the page http://de.wiktionary.org to a form, that can be easily used by computer programms. Technique chosen for this task is Parsing Expression Grammar. The parser is generated from a grammar definition using the Mouse tool (http://www.romanredz.se/freesoft.htm). This declarative approach allows the logic behind parsing to be easier grasped, as if it were implemented directly in Java or another general purpose programming language (probably with some regular expressions buried in the source code). It also make it easier to adapt the parser to changing templates used by Wiktionary.

Target representation will be JSON, stored in some document oriented database, probably CouchDB (http://couchdb.apache.org/).
