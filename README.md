# SWT_Project_2019-2020
## Introduzione
Il progetto tratta la ricerca sui formati di metadatazione standardizzata per videoclip digitali e costruzione di un'ontologia che permetta la ricerca e descrizione semantica di un videoclip di un evento sportivo di tipo motoristico, in particolare MotoGP. Successivamente la realizzazione di un'applicazione con il framework JENA che faccia una ricerca SPARQL in due dataset remoti, in particolare il dataset Dorna (Fornito da terzi) e il dataset DBPedia. Ed infine, sempre con quest'ultima applicazione, la popolazione dell'ontologia precedentemente creata con il dataset di individui appena estratto.

## Architettura
Per questo progetto sono state prese le seguenti scelte architetturali:
- [Protégé](https://protege.stanford.edu/): per la creazione e l'annotazione dell'ontologia
- [Apache Jena](https://jena.apache.org/): framework utilizzato per l'implementazione dell'applicazione di ricerca e popolamento dell'ontologia (Java).
- [Fuseki](https://jena.apache.org/documentation/fuseki2/): Java SPARQL web server per la visualizzazione dei dati dell'ontologia appena popolata.

## I file
- Homework.java: contiene il processo principale dell'applicazione:
  - Importazione dell'ontologia creata,
  - Data ingestion da JSON Dorna e DBPedia tramite ricerca SPARQL,
  - Creazione di individui descritti dalle proprietà appena estratte,
  - Popolamento dell'ontologia ed esportazione in formato RDF,
  - Aggiuta degli individui su web server Fuseki.

- Functions.java: contiene le funzioni generalizzate per:
  - Creazione modello con framework Jena
  - Generazione di query SPARQL per interrogare un determinato dataset specifico
  - Creazione individui con specifiche proprietà
  - Mapping di dati
  - Aggiunta di individui su web server Fuseki

## Aggiunta di dataset o classi da interrogare
Come precedentemente spiegato il file Functions.java contiene funzioni generalizzate che interrogano uno specifico dataset. In questo caso il progetto contiene unicamente il collegamento al dataset DBPedia. Per aggiungere un dataset bisogna specificare il comportamento modificando le seguenti funzioni:
```java
public static String GetSourceQuery(String source, String className)
```
```java
public static String GetSourceQueryPrefix(String source)
```
