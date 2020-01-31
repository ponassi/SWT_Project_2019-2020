import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;

public class Functions {
	/** Parse JSON array file to a list of hash maps
	 * @param filePath
	 * @param elements
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> ParseJsonArrayFile(String filePath, String...elements) {
		ArrayList<HashMap<String, String>> response = new ArrayList<HashMap<String, String>>();
		
		InputStream inputStream;
		
		try {
			inputStream = new FileInputStream(new File(filePath));
			
			//Read JSON file
	        JsonValue jsonValue = JSON.parseAny(inputStream);

	        // Loop through jsonValue array
	        for(int i = 0; i < jsonValue.getAsArray().size(); i++) {
	        	
	        	// Create element map
	        	HashMap<String, String> map = new HashMap<String, String>();
	        	
	        	// Get elements passed as parameter to the function
	        	for(int j = 0; j < elements.length; j++) {
	    			String elementValue = jsonValue.getAsArray().get(i).getAsObject().getString(elements[j]);
	    			
	    			// Put element in hashMap
	    			map.put(elements[j], elementValue);
	    		}
	        	
	        	// Add element map to the list
	        	response.add(map);
	        }
			
		} catch (Exception e) {
			// Catch any exception and print it
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	/**
	 * Create an ontology model from .owl file
	 * @param filePath
	 * @return
	 */
	public static OntModel CreateModelFromOwlFile(String filePath) {
		try {
			InputStream inputStream = new FileInputStream(new File(filePath));
			OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
			m.read(inputStream, "RDF/XML");
			
			return m;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
			return null;
		}
    	
	}
	
	/**
	 * Get query of a specific class name from a specific source
	 * @param source
	 * @param className
	 * @return
	 */
	public static String GetSourceQuery(String source, String className) {
		switch(source) {
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
				
			case "anotherSource":
				// TDB
				return "";
				
			default:
				return "";
		}
	}
	
	/**
	 * Get source prefixes
	 * @param source
	 * @return
	 */
	public static String GetSourceQueryPrefix(String source) {
		switch(source) {
			case "dbpedia":
				return "prefix dbpedia-owl: <http://dbpedia.org/ontology/>\r\n" + 
			    		"prefix dbpedia: <http://dbpedia.org/resource/>\r\n" + 
			    		"prefix dbp: <http://dbpedia.org/property/>\r\n" +
			    		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
			    		"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
			    		"prefix foaf: <http://xmlns.com/foaf/0.1/>\r\n" + 
			    		"prefix xsd: <http://www.w3.org/2001/XMLSchema#>\r\n";
				
			case "anotherSource":
				// TDB
				return "";
			
			default:
				return "";
		}
	}
	
	/**
	 * @param source
	 * @param query
	 * @return
	 */
	public static ResultSet ExecuteSparqlQueryOnSource(String source, String query) {
		Query q = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", q);
		
		return qexec.execSelect();
	}
	
	/**
	 * Create model individual for a specific set of properties
	 * @param model
	 * @param solution
	 * @param individualUri
	 * @param classUri
	 * @param propertiesMap
	 * @return
	 */
	public static Individual CreateModelIndividual(OntModel model, QuerySolution solution, String individualUri, String classUri, HashMap<String, String> propertiesMap) {
		OntClass ontClass = model.getOntClass(classUri);
		Individual individual = model.createIndividual(individualUri, ontClass);
		
		for(int i = 0; i < propertiesMap.size(); i++) {
			RDFNode node = solution.get(propertiesMap.keySet().toArray()[i].toString());
			Property prop = model.getProperty(propertiesMap.values().toArray()[i].toString());
			
			individual.addProperty(prop, node);
		}
		
		return individual;
	}
	
	
	/**
	 * Map rider name and surname to match dbpedia values
	 * @param name
	 * @param surname
	 * @return
	 */
	public static String MapRiderNameForDbPedia(String name, String surname) {
		return FirstUpperCase(name) + "_" + FirstUpperCase(surname);
	}
	
	/**
	 * Add individual to fuseki
	 * @param endpoint
	 * @param className
	 * @param uri
	 */
	public static void AddIndividualToFuseki(String endpoint, String className, String uri) {
		UpdateProcessor upp = UpdateExecutionFactory.createRemote(
				UpdateFactory.create(String.format(GetFusekiQuery(className), uri)), 
				         endpoint);
				upp.execute();
	}
	
	/**
	 * Print individuals from fuseki endpoint
	 */
	public static void PrintFusekiInvididuals(String endpoint) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				endpoint, "SELECT * WHERE {?x ?r ?y}");
		ResultSet rs = qe.execSelect();
		ResultSetFormatter.out(System.out, rs);
		qe.close();
	}
	
	/**
	 * Get fuseki query for a specific class name
	 * @param className
	 * @return
	 */
	private static String GetFusekiQuery(String className) {
		switch(className) {
			case "rider":
				return "PREFIX owl: <http://www.semanticweb.org/andre/ontologies/2019/11/homework-swt#>\n"
			    		+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			            + "INSERT DATA"
			            + "{ <%s> rdf:type owl:rider ."
			            + "}";
				
			case "anotherClass":
				// TDB
				return "";
				
			default:
				return "";
		}
	}
	
	/**
	 * Return string with first upper case
	 * @param str
	 * @return
	 */
	private static String FirstUpperCase(String str) {
    	return str.toLowerCase().substring(0, 1).toUpperCase() + str.toLowerCase().substring(1);
    }
}
