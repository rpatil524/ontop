/*
 * Copyright (C) 2009-2013, Free University of Bozen Bolzano
 * This source code is available under the terms of the Affero General Public
 * License v3.
 * 
 * Please see LICENSE.txt for full license terms, including the availability of
 * proprietary exceptions.
 */
package it.unibz.krdb.obda.owlrefplatform.owlapi3.example;

import it.unibz.krdb.obda.io.ModelIOManager;
import it.unibz.krdb.obda.model.OBDADataFactory;
import it.unibz.krdb.obda.model.OBDAModel;
import it.unibz.krdb.obda.model.impl.OBDADataFactoryImpl;
import it.unibz.krdb.obda.owlapi3.QuestOWLIndividualIterator;
import it.unibz.krdb.obda.owlrefplatform.owlapi3.OWLAPI3Materializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.semanticweb.owlapi.model.OWLIndividualAxiom;

/**
 * A very simple example that shows how to generate triples in an N-Triple file,
 * using the data of a database and a set of mappings from the OBDA file.
 * 
 * NOTE: This process does not involve reasoning since no ontology is given. The
 * result are the DIRECT triples generated by the mappings.
 */
public class ABoxMaterializerExample {

	/*
	 * Use the sample database using H2 from
	 * https://babbage.inf.unibz.it/trac/obdapublic/wiki/InstallingTutorialDatabases
	 */
	final String inputFile = "src/main/resources/example/exampleBooks.obda";
	final String outputFile = "src/main/resources/example/exampleBooks.txt";
	
	public void generateTriples() throws Exception {

		/*
		 * Load the OBDA model from an external .obda file
		 */
		OBDADataFactory fac = OBDADataFactoryImpl.getInstance();
		OBDAModel obdaModel = fac.getOBDAModel();
		ModelIOManager ioManager = new ModelIOManager(obdaModel);
		ioManager.load(inputFile);

		/*
		 * Start materializing data from database to triples.
		 */

		OWLAPI3Materializer materializer = new OWLAPI3Materializer(obdaModel);
		
		long numberOfTriples = materializer.getTriplesCount();
		System.out.println("Generated triples: " + numberOfTriples);

		/*
		 * Obtain the triples iterator
		 */
		QuestOWLIndividualIterator triplesIter = materializer.getIterator();
		
		/*
		 * Print the triples into an external file.
		 */
		File fout = new File(outputFile);
		if (fout.exists()) {
			fout.delete(); // clean any existing output file.
		}
		
		PrintWriter out = null;
		try {
		    out = new PrintWriter(new BufferedWriter(new FileWriter(fout, true)));
			while (triplesIter.hasNext()) {
				OWLIndividualAxiom individual = triplesIter.next();
				out.println(individual.toString());
			}
			out.flush();
		} finally {
		    if (out != null) {
		    	out.close();
		    }
		    materializer.disconnect();
		}
	}

	public static void main(String[] args) {
		try {
			ABoxMaterializerExample example = new ABoxMaterializerExample();
			example.generateTriples();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
