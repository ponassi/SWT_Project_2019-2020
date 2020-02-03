# SWT_Project_2019-2020
## Introduzione
Il progetto tratta la ricerca sui formati di metadatazione standardizzata per videoclip digitali e costruzione di un'ontologia che permetta la ricerca e descrizione semantica di un videoclip di un evento sportivo di tipo motoristico, in particolare MotoGP. Successivamente la realizzazione di un'applicazione con il framework JENA che faccia una ricerca SPARQL in due dataset remoti, in particolare il dataset Dorna (Fornito da terzi) e il dataset DBPedia. Ed infine, sempre con quest'ultima applicazione, la popolazione dell'ontologia precedentemente creata con il dataset di individui appena estratto.

## Architettura
Per questo progetto sono state prese le seguenti scelte architetturali:
- [Protégé](https://protege.stanford.edu/): per la creazione e l'annotazione dell'ontologia
- [Apache Jena](https://jena.apache.org/): framework utilizzato per l'implementazione dell'applicazione di ricerca e popolamento dell'ontologia (Java).
- [Fuseki](https://jena.apache.org/documentation/fuseki2/): Java SPARQL web server per la visualizzazione dei dati dell'ontologia appena popolata.

## I file
- [Homework.java](https://github.com/ponassi/SWT_Project_2019-2020/blob/master/Homework.java): contiene il processo principale dell'applicazione:
  - Importazione dell'ontologia creata,
  - Data ingestion da JSON Dorna e DBPedia tramite ricerca SPARQL,
  - Creazione di individui descritti dalle proprietà appena estratte,
  - Popolamento dell'ontologia ed esportazione in formato RDF,
  - Aggiuta degli individui su web server Fuseki.

- [Functions.java](https://github.com/ponassi/SWT_Project_2019-2020/blob/master/Functions.java): contiene le funzioni generalizzate per:
  - Creazione modello con framework Jena
  - Generazione di query SPARQL per interrogare un determinato dataset specifico
  - Creazione individui con specifiche proprietà
  - Mapping di dati
  - Aggiunta di individui su web server Fuseki

## Aggiunta di dataset o classi da interrogare
Come precedentemente spiegato il file [Functions.java](https://github.com/ponassi/SWT_Project_2019-2020/blob/master/Functions.java) contiene funzioni generalizzate che interrogano uno specifico dataset. In questo caso il progetto contiene unicamente il collegamento al dataset DBPedia. Per aggiungere un dataset bisogna specificare il comportamento modificando le seguenti funzioni:
```java
public static String GetSourceQuery(String source, String className)
```
Questa funzione permette di creare query specifiche per specifici dataset. L'implementazione corrente presenta la query sui rider sul dataset DBPedia, così fatta:
```java
case "dbpedia":
	switch(className) {
		case "rider":
			return 	 GetSourceQueryPrefix(source) + 
				"SELECT ?name ?bd ?team ?bikeNumber {\r\n" + 
				"dbpedia:%s dbpedia-owl:birthDate ?bd .\r\n" + 
				"dbpedia:%s foaf:name ?name .\r\n" +
				"dbpedia:%s dbp:bikeNumber ?bikeNumber .\r\n" +
				"dbpedia:%s dbpedia-owl:team ?team .\r\n" +
				"}";
		default:
			return "";
	}
```
E' possibile aggiungere una query su una determinata classe aggiungendo un case all'interno dello switch del dataset generato. E' inoltre possibile aggiungere query su classi di altri dataset specificando la nuova sorgente nello ```switch(source)```.
```java
public static String GetSourceQueryPrefix(String source)
```
Questa funzione ritona i prefissi SPARQL necessari al corretto funzionamento delle query descritte nella funzione precedente. Aggiungendo un dataset è necessario aggiungere un case specifico allo switch interno alla funzione.

## Descrizione funzioni generalizzate
Il codice presenta una documentazione XML al suo interno con la spiegazione di ogni funzione nello specifico (Consultare i file [Homework.java](https://github.com/ponassi/SWT_Project_2019-2020/blob/master/Homework.java) e [Functions.java](https://github.com/ponassi/SWT_Project_2019-2020/blob/master/Functions.java))
