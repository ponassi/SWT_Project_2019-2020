import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;

public class Homework extends Base
{
	public static OntModel m;

    public static void main( String[] args ) {
        new Homework().setArgs( args ).run();
    }

    public void run() {
    	// Create model from owl ontology file
    	m = Functions.CreateModelFromOwlFile("ontology.owl");
    	
    	// Get Dorna drivers from JSON file
    	ArrayList<HashMap<String, String>> dornaRidersValues = Functions.ParseJsonArrayFile("Dorna.json", "n", "sn", "na");
    	
    	for(int i = 0; i < dornaRidersValues.size(); i++) {
    		try
    		{
    			// Get data
	    		String name = dornaRidersValues.get(i).get("n");
	    		String surname = dornaRidersValues.get(i).get("sn");
	    		
	    		String riderNameFormatted = Functions.MapRiderNameForDbPedia(name,  surname);
	    		String query = String.format(Functions.GetSourceQuery("dbpedia", "rider"),
	    				riderNameFormatted, riderNameFormatted, riderNameFormatted, riderNameFormatted);
	    		
	    		// Execute query on dbpedia
	    		ResultSet resultQuery = Functions.ExecuteSparqlQueryOnSource("dbpedia", query);
	    		
	    		// Properties mapping
	    		HashMap<String, String> propertiesMap = new HashMap<String, String>();
	    		propertiesMap.put("name", "foaf:name");
	    		propertiesMap.put("team", "http://www.semanticweb.org/andre/ontologies/2019/11/homework-swt#corp");
	    		propertiesMap.put("bikeNumber", "http://www.semanticweb.org/andre/ontologies/2019/11/homework-swt#number");
	    		
	    		// Retrieve query solution
	    		QuerySolution solution = resultQuery.nextSolution();
	    		
	    		// Create individual rider with properties mapped
	    		Individual riderIndividual = Functions.CreateModelIndividual
	    				(m, solution,
	    						"http://www.semanticweb.org/andre/resources/2019/11/" + Functions.MapRiderNameForDbPedia(name,  surname),
	    						"http://www.semanticweb.org/andre/ontologies/2019/11/homework-swt#rider",
	    						propertiesMap);
	    		
	    		// Add nationality property
				Property nationalityProp = m.getProperty("http://www.semanticweb.org/andre/ontologies/2019/11/homework-swt#nationality");
	    		riderIndividual.addProperty(nationalityProp, dornaRidersValues.get(i).get("na"));
	    		
	    		// Logging
	    		System.out.println(String.format("Adding %s", riderNameFormatted));
	    		
	    		// Add individual to fuseki
	    		Functions.AddIndividualToFuseki("http://localhost:3030/ds/update",
	    						"rider",
	    						riderNameFormatted);
	    		
	    		// Write model to file as RDF/XML
	    		m.write(new FileOutputStream("model.rdf"), "RDF/XML");
	    		Functions.PrintFusekiInvididuals("http://localhost:3030/ds/query");
    		}
    		catch(Exception e) {
    			// If we couldn't find some riders on dbpedia, skip it
    			//System.out.println(e.getMessage());
    		}
    	}
    }
}

