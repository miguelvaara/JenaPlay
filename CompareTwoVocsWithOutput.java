package JenaTesting1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.GetTriple;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RIOT;
import org.apache.jena.riot.system.PrefixMap;

public class CompareTwoVocsWithOutput {
	
	public static void main (String[] args) {
//		final File file1 = new File("/home/[username]/codes/Finto-data/vocabularies/ykl/ykl.ttl");
		final File file1 = new File(args[0]);
//		final File file2 = new File("/home/[username]/codes/RDFTools/ykl-2021-11-17/ykl-tbc-orig-and-keep-it-immutable.ttl");
		final File file2 = new File(args[1]);
		List<Statement> triplesMissingFromAFileA = new ArrayList<Statement>();
		List<Statement> triplesMissingFromAFileB = new ArrayList<Statement>();
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		Model modelForPrintingA = ModelFactory.createDefaultModel();
		Model modelForPrintingB = ModelFactory.createDefaultModel();
		

		checkIfFileExists(file1);
		checkIfFileExists(file2);
		
		m1.read(file1.toString());
		m2.read(file2.toString());

		StmtIterator it1 =  m1.listStatements();
		StmtIterator it2 =  m2.listStatements();
		
		while (it2.hasNext()) {
			Statement stmt = it2.next();
			m1.setNsPrefixes(m2.getNsPrefixMap());
			if (m1.contains(stmt.getSubject(), stmt.getPredicate(), stmt.getObject())) {
//				triple löytyi
			} else {
				triplesMissingFromAFileA.add(stmt);
			}
		} 
		while (it1.hasNext()) {
			Statement stmt = it1.next();
			m2.setNsPrefixes(m2.getNsPrefixMap());
			if (m2.contains(stmt.getSubject(), stmt.getPredicate(), stmt.getObject())) {
//				triple löytyi
			} else {
				triplesMissingFromAFileB.add(stmt);
			}
		}
		
		try {
//			FileWriter resultAsCSV = new FileWriter("/home/[username]/codes/RDFTools/ykl-2021-11-17/results.csv");
			FileWriter resultAsCSV = new FileWriter(args[2]);

			resultAsCSV.write("* Subjekti * " + ";" + "* Predikaatti *" + ";"  + "* Objekti* " + "\n");
			resultAsCSV.write("*****************************************************************************" + ";" + ";" + "\n");
			resultAsCSV.write("Seuraavat triplet puuttuvat tai poikkeavat A:n osalta tiedostossa B olleista:" + ";" + ";" + "\n");
			resultAsCSV.write("B: " + file1.toString() + ";" + ";" + "\n");
			resultAsCSV.write("*****************************************************************************" + ";" + ";" + "\n");
			int iterNumForA = 0;
			for (Statement triple : triplesMissingFromAFileA) {
				iterNumForA += 1;
				resultAsCSV.write(iterNumForA + ";" + ";" + "\n");
				resultAsCSV.write(triple.getSubject().toString() + ";" + 
				triple.getPredicate().toString() + ";" + 
				triple.getObject().toString() + "\n\n");
			}
			
			resultAsCSV.write("*****************************************************************************" + ";" + ";" + "\n");
			resultAsCSV.write("Seuraavat triplet puuttuvat tai poikkeavat B:n osalta tiedostossa A olleista:" + ";" + ";" + "\n");
			resultAsCSV.write("B: " + file2.toString() + ";" + ";" + "\n");
			resultAsCSV.write("*****************************************************************************" + ";" + ";" + "\n");
			int iterNumForB = 0;
			for (Statement triple : triplesMissingFromAFileB) {
				iterNumForB += 1;
				resultAsCSV.write(iterNumForB + ";" + ";" + "\n");
				resultAsCSV.write(triple.getSubject().toString() + ";" + 
				triple.getPredicate().toString() + ";" + 
				triple.getObject().toString() + "\n\n");
			}
			resultAsCSV.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		modelForPrintingA.add(triplesMissingFromAFileA);
		modelForPrintingB.add(triplesMissingFromAFileB);

		showMeTheTriples(modelForPrintingA, "Tiedosto: A", m1.getNsPrefixMap());
		showMeTheTriples(modelForPrintingB, "Tiedosto: B", m2.getNsPrefixMap());
		
		System.out.println(assertTrue(m1, m2) ? "Annetut sanastot ovat isomorfisia": "Annetut sanastot eivät ole isomorfisia");
		System.out.println("Förbi, Done, Slut!");
		
	}
	private static void showMeTheTriples(Model m, String message, Map prfMap) {
		System.out.println("*****");
		System.out.println(message);
		System.out.println("*****");
		m.setNsPrefixes(prfMap);
		RDFWriter.create()
		.set(RIOT.symTurtleDirectiveStyle, "sparql")
		.lang(Lang.TTL)
		.source(m)
		.output(System.out);
	}

	private static boolean assertTrue(Model mx1, Model my2) {
		if (mx1.isIsomorphicWith(my2)) {
			return true;
		} 
		return false;
	}
	
	private static void checkIfFileExists(File file) {
		try {
			 if (!file.exists())
			    throw new FileNotFoundException();
			}
			catch(FileNotFoundException e) {
			   System.out.println("Syöttämääsi tiedostoa ei löytynyt");
			}
	}
}
