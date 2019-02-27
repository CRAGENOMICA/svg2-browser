package controllers;

import play.mvc.*;
import scala.util.parsing.json.JSONArray;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
	
import play.api.libs.json.*;
import org.json.simple.JSONObject;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static play.libs.Scala.asScala;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.File;

import org.yaml.snakeyaml.Yaml;

import org.rosuda.REngine.*;
/*
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPList;
 */
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RList;
import models.*;


@Singleton
public class SVG2Browser extends Controller 
{

	private final Form<ExperimentForm> formSelExp; // lo utilizamos en todos los forms
	private MessagesApi messagesApi;	
	private final List<ExperimentData> listExperiments;
	private final String mapStrDesc;
	private final String mapStrImg;
	private REngine eng;

	private final Logger logger = LoggerFactory.getLogger(getClass()) ;
	
	@Inject
	public SVG2Browser(FormFactory formFactory, MessagesApi messagesApi) 
	{
		this.formSelExp 		= formFactory.form(ExperimentForm.class);
		this.messagesApi 		= messagesApi;
		this.listExperiments	= com.google.common.collect.Lists.newArrayList();
		this.eng 				= null;
		
        // Foreach directory
        /// recover experiment data and create object with info
        
		Yaml yaml 		= new Yaml();
        File directory 	= new File("conf/experiments");
        File[] dirList 	= directory.listFiles();
        for (File file : dirList){
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
	    	// Start R engine
    		// https://github.com/s-u/REngine/blob/master/JRI/test/RTest.java
    		eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", 
    					new String[] { "--vanilla", "--no-save" }, new REngineStdOutput(), false);
    	}		
    	catch( Exception e ) {
    		System.out.println("Booooom3: " + e.toString() );
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
    			request, messagesApi.preferred(request) ));
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public Result showResults( Http.Request request ) {
        return ok(views.html.showResults.render());
    }
    
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
    		    	String s[] = eng.parseAndEval("example").asStrings();
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
        
    
    public Result generateResults( Http.Request request ) {
    	
    	final Form<ExperimentForm> boundForm = formSelExp.bindFromRequest(request);

        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.inputSelection.render( asScala(listExperiments), formSelExp, mapStrDesc, mapStrImg,
        			request, messagesApi.preferred(request) ));
        } 
        else {
        	ExperimentForm expData = boundForm.get();
        	
            // here we need to call R
        	
            //return redirect(routes.WidgetController.listWidgets())
            //    .flashing("info", "Widget added!");
        	return ok(views.html.showResults.render());
        }
    	
    	
    	
    }
}
