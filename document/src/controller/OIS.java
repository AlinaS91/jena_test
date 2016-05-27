/**
 * @author Waldemar Jaufmann
 * @author Thomas Seewald
 */

package controller;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


import models.Document;
import models.ObjectRelation;
import models.Word;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectReader;


@Path("/rest") 
public class OIS {
	  private static JSONObject jsonObject;

	  private static String object;
	  private static String fileName = "data/A-Box_Cloud_Dokumente.owl";
	  private static OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	  
	  public static void main(String[] args) {
		


	}

// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// METHODS FOR THE Simulator
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	  	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// DELETE Statements
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		 @GET
		 @Path("/DeleteDocument/{googleDriveID}")
		 @Produces("application/json")
		 public String deleteDocument(@PathParam("googleDriveID") String googleDriveID) throws JSONException {
			 String UPDATE_TEMPLATE = 
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					 + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
					 + "PREFIX Cloud_Dokumente: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
					 + "DELETE { ?Cloud_Dokumente ?p ?v }"
					 + "WHERE"
					 + "{ ?Cloud_Dokumente Cloud_Dokumente:Name ?Name ."
					 + "FILTER regex(?Name,'Besprechungsprotokoll_HighNet_16-01-2016')"
					 + "?Cloud_Dokumente ?p ?v}";
					 
			//Add a new book to the collection
		     String id = UUID.randomUUID().toString();
		     UpdateProcessor upp = UpdateExecutionFactory.createRemote(
		                UpdateFactory.create(String.format(UPDATE_TEMPLATE, id)), 
		                "http://localhost:3030/ds/update");
		     upp.execute();
			 
			 return "hi";
		 }
		 
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// END ------------- DELETE Statements
			// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// GET Statements
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		 
		 @GET
		 @Path("/GetWordinformation/{word}")
		 @Produces("application/json")
		 public Response getWordinformation(@PathParam("word") String word) throws JSONException {
				jsonObject = new JSONObject();
				Word wordInformation;
				ArrayList<Word> listWord = new ArrayList<Word>();
				
				try {
					String sparQuery = "PREFIX foaf: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ "	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
							+ " PREFIX mebase: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ " SELECT  * "
							+ " WHERE   { ?Dokument ?Typ ?Ausgabe ."
							+ " FILTER regex(?Ausgabe, '"+word+"') . "
							+ "?Typ <http://www.w3.org/2000/01/rdf-schema#domain> ?Klassentyp ."
							+ "?Typ <http://www.w3.org/2000/01/rdf-schema#range> ?Datentyp}";
					
					QueryExecution qe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", sparQuery);

			        ResultSet results = qe.execSelect();
			        List var = results.getResultVars();
			        String documentMetadata="";
			        String documentName="";
			        while (results.hasNext()){
			        	wordInformation = new Word();
						QuerySolution qs = results.nextSolution();
						for(int i=0; i<var.size();i++){			
							String va = var.get(i).toString();
							RDFNode node = qs.get(va);
							String value = node.toString().substring(node.toString().indexOf("#")+1, node.toString().length());
							switch(i){
								case 0: wordInformation.setObjectRelation(getGraphInformatoin("<"+node.toString()+">"));
									documentName = node.toString().substring(node.toString().indexOf("#")+1, node.toString().length());
									break;
								case 1: wordInformation.setValueType(value);
									documentMetadata=value;
								break;
									case 2: wordInformation.setValue(node.toString());
								break;
									case 3: wordInformation.setClassName(value);
								
								if(value.equals("Dokument")){
									System.out.println(documentMetadata+"_von: "+documentName);
								}
								break;
								case 4: wordInformation.setDataType(value);
								break;
							}
						}
						listWord.add(wordInformation);
					}
			        qe.close();
				} catch(Exception e){
					e.printStackTrace();
				}
				jsonObject.put("data", listWord);
				return Response.status(200).entity(jsonObject.toString()).build();
		 	}
		 
		 
		 @GET
		 @Path("/GetDocumentMetadata/")
		 @Produces("application/json")
		 public Response getDocumentMetadata() throws JSONException {
				jsonObject = new JSONObject();
				Document document;
				ArrayList<Document> listDocument = new ArrayList<Document>();
				ArrayList<String> listKeywort = new ArrayList<String>();				
				try {

					String sparQuery = "prefix Cloud_Dokumente: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ "	SELECT ?Name (group_concat(?Schlagwort;separator=' , ') as ?Schlagworte) ?Dokumenttyp ?Erstellungsdatum ?Speicherort ?Status ?Version ?z"
									+ " WHERE {"
									+ "	  ?x Cloud_Dokumente:Name ?Name ."
									+ "	  ?x Cloud_Dokumente:Schlagwort ?Schlagwort ."
									+ "	  ?x Cloud_Dokumente:Dokumenttyp ?Dokumenttyp ."
									+ "	  ?x Cloud_Dokumente:Erstellungsdatum ?Erstellungsdatum ."
									+ "	  ?x Cloud_Dokumente:Speicherort ?Speicherort ."
									+ "	  ?x Cloud_Dokumente:Status ?Status ."
									+ "	  ?x Cloud_Dokumente:Version ?Version ."
									+ "	  ?x Cloud_Dokumente:Dokument_hat_Verfasser ?Verfasser"
									+ "	  {"
									+ "	    SELECT *"
									+ "		    WHere {"
									+ "		    	  ?Verfasser Cloud_Dokumente:Vorname ?z"
									+ "		 	}"
									+ "		 } "
									+ "	}"
									+ "	group by ?Name ?Dokumenttyp ?Erstellungsdatum ?Speicherort ?Status ?Version ?Verfasser ?z";

					
					QueryExecution qe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", sparQuery);

			        ResultSet results = qe.execSelect();
			        List var = results.getResultVars();
			        String documentMetadata="";
			        String documentName="";
			        int countIteration =0;
			        while (results.hasNext()){
			        	
			        	document = new Document();
						QuerySolution qs = results.nextSolution();
						for(int i=0; i<var.size();i++){
							
							String va = var.get(i).toString();
							RDFNode node = qs.get(va);
							
							countIteration++;
							switch(countIteration){
								case 1: 
									document.setName(node.toString());
									break;
								case 2: 					
									listKeywort.add(node.toString().replace(" ", "\""));
									document.setListKeyword(listKeywort);
									break;
								case 3: 
									document.setType(node.toString());				
									break;
								case 4: 
								    document.setCreationDate(node.toString().substring(0, node.toString().indexOf("^^")));
									
									break;
								case 5: 
									document.setPath(node.toString());
									break;
								case 6: 
									document.setStatus(node.toString());
									break;
								case 7: 
									document.setVersion(node.toString().substring(0, node.toString().indexOf("^^")));
									break;
								case 8: 
									document.setCreatedBy(node.toString());
									break;
							}
							if(countIteration==8){
								countIteration=0;
							}
						}
						listDocument.add(document);
					}
			        qe.close();
				} catch(Exception e){
					e.printStackTrace();
				}
				jsonObject.put("data", listDocument);
				return Response.status(200).entity(jsonObject.toString()).build();
		 	}
		 
		 
		/* public static void main(String[] args) {
			 getGraphInformatoin("<http://www.semanticweb.org/alinasiebert/ontologies/2016/0/A-BOX_Cloud_Dokumente#Lisa_Maier>");

		}*/
		 
		 public ArrayList<ObjectRelation>  getGraphInformatoin(String GrapfInformation) {
				jsonObject = new JSONObject();
				ObjectRelation objectRelation;
				ArrayList<ObjectRelation> listObjectRelation = new ArrayList<ObjectRelation>();
				
				try {
					
					String sparQuery = "PREFIX foaf: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ "	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
							+ " PREFIX mebase: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ " SELECT  * "
							+ " WHERE   { "+GrapfInformation+" ?Typ ?Ausgabe }";
					
					QueryExecution qe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", sparQuery);

			        ResultSet results = qe.execSelect();
			        List var = results.getResultVars();
			        while (results.hasNext()){
						QuerySolution qs = results.nextSolution();
						for(int i=0; i<var.size();i++){
							String va = var.get(i).toString();
							RDFNode node = qs.get(va);
							System.out.println(node.toString());
							if(va.equals("Typ")&&node.toString().contains("#")){
								RDFNode ausgabe = qs.get(var.get(i+1).toString());
								String type = node.toString().substring(node.toString().indexOf("#")+1, node.toString().length());
								String value = ausgabe.toString().substring(ausgabe.toString().indexOf("#")+1, ausgabe.toString().length());
								if(type.contains("_")){
									if(ausgabe.toString().contains("#")){
										objectRelation = new ObjectRelation();
										objectRelation.setType(type);
										objectRelation.setValue(value);
										listObjectRelation.add(objectRelation);
									}
								}

								
								/*typ=va+""+node.toString().substring(node.toString().indexOf("#")+1, node.toString().length());
								System.out.println(typ);*/
							}
						
						}
					}
			        
			        qe.close();
					
				} catch(Exception e){
					e.printStackTrace();
				}
				return listObjectRelation;
		}
		 
		 
		 
		 
		 @GET
		 @Path("/GetDocumentByGoogleDriveID/{driveID}")
		 @Produces("application/json")
		 public Response getDocumentByGoogleDriveID(@PathParam("driveID") int driveID) throws JSONException {
				jsonObject = new JSONObject();
				Document document = new Document();
				ArrayList<Document> listDocument = new ArrayList<Document>();
				/*
				try {
					
					String sparQuery = "PREFIX foaf: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ "	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
							+ " PREFIX mebase: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ " SELECT  * "
							+ " WHERE   { ?Dokument ?Typ ?Ausgabe ."
							+ " FILTER regex(?Ausgabe, '"+word+"') . "
							+ "?Typ <http://www.w3.org/2000/01/rdf-schema#domain> ?Klassentyp ."
							+ "?Typ <http://www.w3.org/2000/01/rdf-schema#range> ?Datentyp}";
					
					QueryExecution qe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", sparQuery);

			        ResultSet results = qe.execSelect();
			        List var = results.getResultVars();
			        while (results.hasNext()){
						QuerySolution qs = results.nextSolution();
						for(int i=0; i<var.size();i++){
							String va = var.get(i).toString();
							RDFNode node = qs.get(va);
							String value = node.toString().substring(node.toString().indexOf("#")+1, node.toString().length());
							switch(i){
								case 0: System.out.println("Graphennamen: ");
								break;
								case 1: wordInformation.setValueType(value);
								break;
								case 2: wordInformation.setSearchParameter(node.toString());
								break;
								case 3: wordInformation.setClassName(value);
								break;
								case 4: wordInformation.setDataType(value);
								break;
							}	
						}
					}
			        
			        qe.close();
					listWord.add(wordInformation);
					
				} catch(Exception e){
					e.printStackTrace();
				}*/
				listDocument.add(document);
				jsonObject.put("data", listDocument);
				return Response.status(200).entity(jsonObject.toString()).build();
		 	}
		 
		 
		 
		 
		 /*
		 @GET
		 @Path("/GetAllKeywords")
		 @Produces("application/json")
		 public Response getAllKeywords() throws JSONException {
				jsonObject = new JSONObject();
				Document document = new Document();
				ArrayList<Document> listDocument = new ArrayList<Document>();
				ArrayList<String> listKeywords = new ArrayList<String>();
				
				try {
					
					String sparQuery = "PREFIX foaf: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ "	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
							+ " PREFIX mebase: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
							+ " SELECT DISTINCT ?Schlagwort "
							+ " WHERE  { ?Dokument foaf:Schlagwort ?Schlagwort }";
					
					QueryExecution qe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", sparQuery);

			        ResultSet results = qe.execSelect();
			        List var = results.getResultVars();
			        while (results.hasNext()){
						QuerySolution qs = results.nextSolution();
						for(int i=0; i<var.size();i++){
							String va = var.get(i).toString();
							RDFNode node = qs.get(va);
							listKeywords.add(node.toString());
							System.out.println(node.toString());
						}
					}
			        
			        qe.close();
					document.setKeyword(listKeywords);
					listDocument.add(document);
					
				} catch(Exception e){
					e.printStackTrace();
				}
				
				jsonObject.put("data", listDocument);
				return Response.status(200).entity(jsonObject.toString()).build();
		 	}
		 */

		 	/*
			 @GET
			 @Path("/GetDocumentByName/{documentName}")
			 @Produces("application/json")
			 public Response getDocumentByName(@PathParam("documentName") String documentName) throws JSONException {
					jsonObject = new JSONObject();
					Document document = new Document();
					ArrayList<Document> listDocument = new ArrayList<Document>();
					String fileName = "data/A-Box_Cloud_Dokumente.owl";
					
					OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
					try {
						File file = new File(fileName);
						FileReader reader = new FileReader(file);
						model.read(reader,null);
						
						String sparQuery = "PREFIX foaf: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
								+ "	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
								+ " PREFIX mebase: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
								+ " SELECT ?Name ?Speicherort ?Erstellungsdatum ?Dokument_hat_Verfasser ?Dokument_gehoert_zu_Projekt"
								+ " WHERE  { ?Dokument foaf:Name ?Name ."
								+ "			 ?Dokument foaf:Speicherort ?Speicherort . "
								+ "			 ?Dokument foaf:Erstellungsdatum ?Erstellungsdatum . "
								+ "			 ?Dokument foaf:Dokument_hat_Verfasser ?Dokument_hat_Verfasser . "
								+ "			 ?Dokument foaf:Dokument_gehoert_zu_Projekt ?Dokument_gehoert_zu_Projekt . "
								+ "			 FILTER regex(?Name,'"+documentName+"')}";
									
						Query query = QueryFactory.create(sparQuery);
						QueryExecution qe = QueryExecutionFactory.create(query, model);
						ResultSet results = qe.execSelect();
						List var = results.getResultVars();
						
						while (results.hasNext()){
							QuerySolution qs = results.nextSolution();
							for(int i=0; i<var.size();i++){
								String va = var.get(i).toString();
								RDFNode node = qs.get(va);
								if(i==0){
									document.setDocumentName(node.toString());
								} else if(i==1){
									document.setDocumentPath(node.toString());
								} else if(i==2){
									document.setCreationDate(node.toString().substring(0, 19));
								} else if(i==3){
									document.setCreatedBy(getAttributeOfGraph(node.toString()));
								} else if(i==4){
									document.setRelatedProject(getAttributeOfGraph(node.toString()));
								}
								System.out.println(node.toString());
							}
						}
						MyOutputStream myOutput = new MyOutputStream();
						ResultSetFormatter.out(myOutput, results, query);
						
					} catch(Exception e){
						e.printStackTrace();
					}
					listDocument.add(document);
					jsonObject.put("data", listDocument);
					return Response.status(200).entity(jsonObject.toString()).build();
			}*/
			 
			 
		public static String getAttributeOfGraph(String URI){
			Model schema = FileManager.get().loadModel("data/Cloud_Dokumente.owl");
			Model data = FileManager.get().loadModel("data/A-Box_Cloud_Dokumente.owl");
			Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
			reasoner = reasoner.bindSchema(schema);
			InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
			Resource nForce = infmodel.getResource(URI);
			return printStatements(infmodel, nForce, null, null);
		}
		
		public static String printStatements(Model m, Resource s, Property p, Resource o) {
			ObjectRelation relation = new ObjectRelation();
			ArrayList<ObjectRelation> listObjectRelations = new ArrayList<ObjectRelation>();
		    for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
		        Statement stmt = i.nextStatement();
		        if(stmt.getPredicate().getLocalName().toString().contains("_")){
		        	relation.setType(stmt.getPredicate().getLocalName().toString());
		        	relation.setValue(stmt.getObject().toString().substring(stmt.getObject().toString().indexOf("#")+1, stmt.getObject().toString().length()));
			    }
		    }
		    listObjectRelations.add(relation);
		    System.out.println(listObjectRelations);
		    
		    return object;
		}
		
		

		  /**
		   * 
		   * @return Response
		   * @throws JSONException
		   */
			/* @GET
			 @Path("/GetAllKeywords")
			 @Produces("application/json")
			 public Response getAllKeywords() throws JSONException {
					jsonObject = new JSONObject();
					Document document = new Document();
					ArrayList<Document> listDocument = new ArrayList<Document>();
					ArrayList<String> listKeywords = new ArrayList<String>();
					
					OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
					try {
						File file = new File(fileName);
						FileReader reader = new FileReader(file);
						model.read(reader,null);
						
						String sparQuery = "PREFIX foaf: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
								+ "	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
								+ " PREFIX mebase: <http://www.semanticweb.org/alinasiebert/ontologies/2016/0/Cloud_Dokumente#>"
								+ " SELECT DISTINCT ?Schlagwort "
								+ " WHERE  { ?Dokument foaf:Schlagwort ?Schlagwort }";
						
						Query query = QueryFactory.create(sparQuery);
						QueryExecution qe = QueryExecutionFactory.create(query, model);
						ResultSet results = qe.execSelect();
						List var = results.getResultVars();
						
						while (results.hasNext()){
							QuerySolution qs = results.nextSolution();
							for(int i=0; i<var.size();i++){
								String va = var.get(i).toString();
								RDFNode node = qs.get(va);
								listKeywords.add(node.toString());
								System.out.println(node.toString());
							}
						}
						document.setKeyword(listKeywords);
						listDocument.add(document);
						MyOutputStream myOutput = new MyOutputStream();
						ResultSetFormatter.out(myOutput, results, query);
						
					} catch(Exception e){
						e.printStackTrace();
					}
					
					
					jsonObject.put("data", listDocument);
					return Response.status(200).entity(jsonObject.toString()).build();
			}*/
			 
}