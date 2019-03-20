package controllers;

import play.mvc.*;
import scala.util.parsing.json.JSONArray;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
	
import play.api.libs.json.*;
import org.json.simple.JSONObject;
import com.google.gson.Gson;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static play.libs.Scala.asScala;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;

import org.yaml.snakeyaml.Yaml;

// for R
import org.rosuda.REngine.*;
/*
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPList;
 */
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RList;


// for application.conf
import com.typesafe.config.Config;
import play.mvc.Controller;
import javax.inject.Inject;

import models.*;

@Singleton
public class SVG2Browser extends Controller 
{

	private final Form<ExperimentForm> formSelExp;
	private MessagesApi messagesApi;	
	private final List<ExperimentData> listExperiments;
	private final String mapStrDesc;
	private final String mapStrImg;
	private REngine eng;
	private final Config config;					// get configuration params from application.conf
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private List<String> niceColors;			// string of colors names from R
	private List<String> tissuesList;
	private List<String> filteredGeneList;
	private Path tempFolder;						// temporary folder to store images
		
	@Inject
	public SVG2Browser(FormFactory formFactory, MessagesApi messagesApi, Config config) 
	{
		this.formSelExp 		= formFactory.form(ExperimentForm.class);
		this.messagesApi 		= messagesApi;
		this.listExperiments	= com.google.common.collect.Lists.newArrayList();
		this.eng 				= null;
		this.config			 	= config;
		this.tissuesList 		= new ArrayList<String>();
    	this.filteredGeneList 	= new ArrayList<String>();
    	this.niceColors 		= new ArrayList<String>();
		
        // Foreach directory
        /// recover experiment data and create object with info
        
		Yaml yaml 		= new Yaml();
        File directory 	= new File("conf/experiments");
        File[] dirList 	= directory.listFiles();
        for (File file : dirList)
        {
        	String experiment_id = file.getName();
        	System.out.println("Found: " + file.getName() + " lets try.");
        	
        	if( experiment_id.equals(".DS_Store")) {
        		continue;
        	}
        	
        	InputStream inputStream = this.getClass()
        			.getClassLoader()
        			.getResourceAsStream("experiments/" + experiment_id.toString() + "/experiment_config.yml");
        	
        	Map<String, Map<String,String>> econfs = (Map<String, Map<String,String>>) yaml.load(inputStream);
        	
        	for(String key : econfs.keySet()) // it should iterate only once
        	{
        		System.out.println("key = " + key);
        		Map<String,String> econf = econfs.get(key);
        		
        		listExperiments.add( new ExperimentData( 
        				econf.get("experiment_id"),
        				econf.get("experiment_name"),
        				econf.get("experiment_description"),
        				econf.get("experiment_datafile"),
        				econf.get("experiment_svgfile")
        				));
        		
        	}
            System.out.println("ExpID: " + file.getName() + " retrieved.");
        }
        
        JSONObject mapRawDesc = new JSONObject();
        JSONObject mapRawImg = new JSONObject();
    	StringWriter outDesc = new StringWriter();
    	StringWriter outImg = new StringWriter();
    	
    	
    	for( ExperimentData exp : this.listExperiments) {
    		mapRawDesc.put( exp.experimentID, exp.experimentDesc );
    		mapRawImg.put(  exp.experimentID, exp.experimentSVGfile );
    	}
    
    	try {
    		  mapRawDesc.writeJSONString(outDesc);  
    		  mapRawImg.writeJSONString(outImg);  
    	}
    	catch( java.io.IOException e ) {
    		  System.out.println("Booooom: " + e.toString() );
    	}
    	mapStrDesc = outDesc.toString();
    	mapStrImg = outImg.toString();
    	
    	try {
	    	// create temp folder to store images
	    	tempFolder 		= Files.createTempDirectory("svg2tmp");    	
		}
		catch( java.io.IOException e ) {
			System.out.println("SVG2Browser Booooom1: " + e.toString() );
		}
    	// Start R engine
    	try {
    		// https://github.com/s-u/REngine/blob/master/JRI/test/RTest.java
    		eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", 
    					new String[] { "--vanilla", "--no-save" }, new REngineStdOutput(), false);
    		
    		niceColors = Arrays.asList( eng.parseAndEval("nice_colors").asStrings() );	    	        	
    	}		
    	catch( Exception e ) {
    		System.out.println("SVG2Browser Booooom3: " + e.toString() );
    	}
      	
        // https://www.playframework.com/documentation/2.7.x/api/scala/views/html/helper/index.html
    	
	}
	

    public Result index() {
    	
        //return ok(views.html.index.render());
    	return ok(views.html.index.render() );
    }

    /**
     * 
     * @param request
     * @return
     */
    public Result inputSelection( Http.Request request ) 
    {
    	    	
        return ok(views.html.inputSelection.render( asScala(listExperiments), formSelExp, mapStrDesc, mapStrImg,
    			request, 
    			messagesApi.preferred(request) ));
    }
    
    /**
     * 
     * @param request
     * @return
     */
 /*
    public Result showResults( Http.Request request ) 
    {	
    	
    	String pathSVG = "Arabidopsis_root_NEW.svg";
    	String pathBarplot = "my_barplot.png";    	
    	List<String> listTissue = new ArrayList<String>();    	
    	String mapGenes = "{hola: adios}"; 
    	
    	listTissue.add("tallo");
    	listTissue.add("hoja");
    	listTissue.add("raiz");
    	
    	return ok(views.html.showResults.render( pathSVG, pathBarplot, asScala(listTissue), mapGenes ));
    }
  */  
    /**
     * 
     * @param request
     * @return
     */
    public Result aboutSVG2Browser( Http.Request request ) {
        return ok(views.html.about.render());
    }
    
    
    /**
     * Given an experiment ID it returns a list of genes (strings) encoded as JSON
     * 
     * @param request
     * @return
     */
    //@ BodyParser.Of(Json.class)
    public Result getExampleGenes( String _expID ) 
    {
    	Gson gson = new Gson();
    	List<String> geneList = new ArrayList<String>();
    
    	
    	// this is so ugly, but for a few, it not a big deal
    	for( ExperimentData exp : this.listExperiments) {
    		if( exp.experimentID.equals(_expID) ) 
    		{	
    			try 
    			{    		    
    	    		// https://github.com/s-u/REngine/blob/master/JRI/test/RTest.java
    				
    				// restart sbt after changing lines
    				
    	    		if( eng == null ) {
    	    			//TODO: check this    	    			
    	    			return ok("Error en REngine");
    	    		}

    		    	//System.out.println("R Version: " + eng.parseAndEval("R.version.string").asString());
    		    	
    		    	System.out.println("Cargando: " + eng.parseAndEval("load(\"conf/experiments/" + exp.experimentID + 
    		    			"/" + exp.experimentDatafile + "\")").asString());

    		    	
    		    	// -> eng.assign("s", new String[] { "foo", null, "NA" });
    		    	// <- String s[] = eng.parseAndEval("c('foo', NA, 'NA')").asStrings();
    		    	String s[] = eng.parseAndEval("paste(example,collapse=\",\")").asStrings();
    		    	for( String gene: s) {
    		    		geneList.add( gene );
    		    	}
    		    		    		    	
    	    	}
    	        catch( org.rosuda.REngine.REngineException e ) {
    	  		  System.out.println("Booooom1: " + e.toString() );
    	        }
    	    	catch( org.rosuda.REngine.REXPMismatchException e ) {
    	    		  System.out.println("Booooom2: " + e.toString() );
    	        }
    	    	catch( Exception e ) {
    	  		  System.out.println("Booooom3: " + e.toString() );
    	    	}
    			
    			break;
    		}
    	}
    
    	return ok( gson.toJson(geneList) );

    }
        
    /**
     * Called by input form. It generates the results produced with R.
     * 
     * @param request
     * @return
     */
    public Result generateResults( Http.Request request ) 
    {    	
    	final Form<ExperimentForm> boundForm = formSelExp.bindFromRequest(request);

        if( boundForm.hasErrors() ) 
        {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(
            		views.html.inputSelection.render( 
            				asScala(listExperiments), 
            				formSelExp, 
            				mapStrDesc, 
            				mapStrImg,
            				request, messagesApi.preferred(request) ));
        } 
        else 
        {
        	ExperimentForm expData 	= boundForm.get();
        	List<String> geneList	= expData.getGeneList();        		
        	
        	try {
	        	// write down to a file the list of genes provided by the user
	        	FileWriter fw = new FileWriter( tempFolder.toString() + "/gene_list.txt" );
	        	for( String gene: geneList ) {
	        		fw.write( gene + "\n");
	        	}   
	        	fw.close();
        	}
        	catch( java.io.IOException e ) {
    			System.out.println("generateResults Booooom1: " + e.toString() );
    		}
        	
        	
        	// Following images are generated just for sanity check. Not needed, since they will be requested again by the view
        	// Remove in future versions
        	
        	// R:barplot        	
        	makeOutputBarplot( config.getString("svg2.color.final.default"),  tempFolder.toString() ); 
			   
        	// svgmap-cli: generate SVG
        	makeOutputSVG( expData.getExperimentID(), config.getString("svg2.color.final.default"), tempFolder.toString() );
        	
        	try 
        	{    		    
        		// https://github.com/s-u/REngine/blob/master/JRI/test/RTest.java				
        		// restart sbt after changing lines
        		if( this.eng == null ) {	    			 	    			
        			return ok("Error en REngine");
        		}

        		// R: list of tissues : names( calcEnrichment )
        		// <- String s[] = eng.parseAndEval("c('foo', NA, 'NA')").asStrings();	    
        		String tissuesArray[] = this.eng.parseAndEval("names( " + expData.getExperimentID() +  " )").asStrings();
        		
		    	// R: list of genes, given a tissue: finder( geneList:charvec, tissue:char )		    	
		 // TODO Tenemos que comprobar si genelist & selected Tissue tienen agua
		    	this.eng.assign( "mygenelist", geneList.toArray(new String[0]) );
	    		String filteredGeneArray[] = this.eng.parseAndEval("finder( mygenelist, \""+ expData.getSelectedTissue() + "\" )" ).asStrings();    			    		    	
	    		
	    		this.tissuesList 		= Arrays.asList( tissuesArray );
	    		this.filteredGeneList 	= Arrays.asList( filteredGeneArray );
	    	}
	        catch( org.rosuda.REngine.REngineException e ) {
	  		  System.out.println("makeOutputBarplot Booooom1: " + e.toString() );
	        }
	    	catch( org.rosuda.REngine.REXPMismatchException e ) {
	    		  System.out.println("makeOutputBarplot Booooom2: " + e.toString() );
	        }
	    	catch( Exception e ) {
	  		  System.out.println("makeOutputBarplot Booooom3: " + e.toString() );
	    	}
       	        	
        	expData.setColorSVG(		config.getString("svg2.color.final.default") );
        	expData.setColorBarplot( 	config.getString("svg2.color.final.default") );
        	
       
        	return ok(views.html.showResults.render( 
        			expData.getExperimentID(),
        			expData.getColorSVG(),
        			expData.getColorBarplot(),
        			formSelExp,        			
        			asScala(this.niceColors),
        			asScala(this.tissuesList),
        			asScala(this.filteredGeneList),
        			request, messagesApi.preferred(request) ));
		}
        
    }
    
  
	/**
	 * 
	 * @param _experimentID
	 * @param _newColor
	 * @return
	 */
    public Result getImageSVG( String _experimentID, String _newColor ) 
    {
    	// previously gene_list must be generated. You are expected to call this after generateResults from the web page
    	
    	makeOutputSVG( _experimentID, _newColor, this.tempFolder.toString() ); 
	
		return ok( new File( this.tempFolder.toString() + "/SVG.png" ) );
	}
    
    /**
     * 
     * @param _newColor
     * @return
     */
    public Result getImageBarplot( String _newColor ) 
    {
    	// previously gene_list must be generated. You are expected to call this after generateResults from the web page
    	
    	makeOutputBarplot( _newColor, tempFolder.toString() ); 
	
		return ok( new File( this.tempFolder.toString() + "/barplot.png" ) );
    }
    
    
    
    /**
     * 
     * @param request
     * @return
     */
    public Result updateOutputExperiment( Http.Request request ) 
    {
    	// previously gene_list must be generated. You are expected to call this after generateResults from the web page
    	final Form<ExperimentForm> boundForm = this.formSelExp.bindFromRequest(request);

        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.inputSelection.render( asScala(listExperiments), formSelExp, mapStrDesc, mapStrImg,
        			request, messagesApi.preferred(request) ));
        } 
        else {
        	// subir a poner los colores
        	ExperimentForm expData 	= boundForm.get();
    
    		
        	return ok(views.html.showResults.render( 
        			expData.getExperimentID(),
        			expData.getColorSVG(),
        			expData.getColorBarplot(),
        			formSelExp,        			
        			asScala(this.niceColors),
        			asScala(this.tissuesList),
        			asScala(this.filteredGeneList),
        			request, messagesApi.preferred(request) ));
        }
    }
    
    /**
     * 
     * @param _experimentID
     * @param _color
     * @param _tempFolder
     * @return
     */
    public Boolean makeOutputSVG( String _experimentID, String _color, String _tempFolder ) 
    {
		List<String> cmdArray = new ArrayList<String>();
		List<String> envArray = new ArrayList<String>();
		
		cmdArray.add( config.getString("java.home.path") + "/bin/java" );
		cmdArray.add( "-Djava.library.path=" + config.getString("java.library.JRI") );
		cmdArray.add( "-jar " + config.getString("svgmap-cli.jar.path") );
		cmdArray.add( "-S " + config.getString("experiments.path") + "/" + _experimentID + "/" + _experimentID + ".svg" );
		cmdArray.add( "-R " + config.getString("experiments.path") + "/" + _experimentID + "/" + _experimentID + ".RData" );
		cmdArray.add( "-K " + _tempFolder + "/gene_list.txt" );
		cmdArray.add( "-D " + _tempFolder + "/enrichment_output.tsv" );
		cmdArray.add( "-CF " + _color );
		cmdArray.add( "-C 3" );
		cmdArray.add( "-P" );
		cmdArray.add( "-O " + _tempFolder + "/SVG.png" );    	
		
		envArray.add( "JAVA_HOME=" 	+ config.getString("java.home.path") );
		envArray.add( "R_HOME=" 	+ config.getString("R.home.path"));
		envArray.add( "PATH=" 		+ config.getString("R.home.path") + "/bin/:" + System.getenv("PATH") );
	
		try {
		 	Process p = Runtime.getRuntime().exec( (String []) cmdArray.toArray(), (String []) envArray.toArray() );
		 	p.waitFor();
		}
		catch( java.io.IOException e ) {
			System.out.println("makeOutputSVG Booooom1: " + e.toString() );
		}
		catch( java.lang.InterruptedException e ){
			System.out.println("makeOutputSVG Booooom2: " + e.toString() );
		}
    	return true;
    }
   
    
    /**
     * 
     * @param _barColor
     * @param _tempFolder
     * @return
     */
    public Boolean makeOutputBarplot( String _barColor, String _tempFolder ) 
    {
    	// R:barplot
    	// Recover list of genes from input (mygenelist), and recalc drawing_vector(), to get data to feed to barplot
    	// "calcEnrichment <- drawing_vector( mygenelist );"; 
    	// mybarplot( myvec, "red", "barplot.png" )
    	// mybarplot( drawing_vector( mygenelist ), "red", "barplot.png" )
    	 
    	try 
		{    		    
    		// https://github.com/s-u/REngine/blob/master/JRI/test/RTest.java				
			// restart sbt after changing lines
    		
    		if( this.eng == null ) {
    			//TODO: check this    	    			
    			return Boolean.FALSE; //ok("Error en REngine");
    		}
    		
    		//this.eng.assign( "mygenelist", _mygenelist.toArray(new String[0]) );    		
    		String outputFile = _tempFolder + "/barplot.png" ; 
			   
        	this.eng.parseAndEval("mygenelist <- as.character( read.table(\"" + tempFolder + "/gene_list.txt\")[[1]]" );
    		this.eng.parseAndEval("mybarplot( drawing_vector( mygenelist ), \"" + _barColor + "\", \""+ outputFile +"\" )" );
    			    		    	
    	}
        catch( org.rosuda.REngine.REngineException e ) {
  		  System.out.println("makeOutputBarplot Booooom1: " + e.toString() );
        }
    	catch( org.rosuda.REngine.REXPMismatchException e ) {
    		  System.out.println("makeOutputBarplot Booooom2: " + e.toString() );
        }
    	catch( Exception e ) {
  		  System.out.println("makeOutputBarplot Booooom3: " + e.toString() );
    	}
    	
    	//my_barplot.png
    	return true;
    }
    
    
}
