package JenaTesting1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RIOT;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD;

import com.apicatalog.jsonld.lang.LanguageTag;

import org.apache.jena.ttl.turtle.*;

public class HowDoesItWork {
	static final String resourceURI = "http://somewhere/JohnSmith"; 
	static final String fullName = "John Smith";
	
	public static void main (String[] args) {
		
		List<Resource> movableTriplesSubj = new ArrayList<Resource>();
		List<String> movableTriplesObj = new ArrayList<String>();
		List<Statement> movableTriplesStmts = new ArrayList<Statement>();
		Model m = ModelFactory.createDefaultModel();
		m.read("/home/[username]/codes/Finto-data/vocabularies/ykl/ykl-skos.ttl");
		int counter = 0;
		int forCounter = 0;

//		This is for later use ---
//		RDFWriter.create()
//		.set(RIOT.symTurtleDirectiveStyle, "sparql")
//		.lang(Lang.TTL)
//		.source(m)
//		.output(System.out);	
		
		StmtIterator it =  m.listStatements();
		
		while (it.hasNext()) {
		     Statement stmt = it.next();
		     if (stmt.asTriple().predicateMatches(SKOS.closeMatch.asNode())) {
				movableTriplesSubj.add(stmt.getSubject().asResource());
				movableTriplesObj.add(stmt.getObject().toString());
		    	movableTriplesStmts.add(stmt);
				counter += 1;
			} 
		     System.out.println(counter + " " + stmt.asTriple().getMatchPredicate().toString());
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Iterator<Statement> removableStmts = movableTriplesStmts.iterator();
		while (removableStmts.hasNext()) {
			m.remove(removableStmts.next());
		}
		
		for (int i = 0; i < movableTriplesSubj.size(); i++) {
			m.add(movableTriplesSubj.get(i), SKOS.related, movableTriplesObj.get(i));
			forCounter += 1;
			System.out.printf("%d Added %s %s %s %n", counter, movableTriplesSubj.get(i).asResource().toString(),
					SKOS.related.toString(), movableTriplesObj.get(i));
		}

		// ---
		Resource printableTestResource = m.getResource("http://urn.fi/URN:NBN:fi:au:ykl:14.09");
		System.out.println("*****");
		String res = printableTestResource.getProperty(SKOS.altLabel, "fi").getString().equals("Psykologia, historia (14.09)") ? "Resurssi löytyi" : "Resurssia ei löytynyt!";
		System.out.printf("%s%n", res); //
		System.out.println(printableTestResource.getProperty(SKOS.altLabel, "fi").getString());
	}
}
