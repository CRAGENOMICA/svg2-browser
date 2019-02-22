package controllers;

import play.mvc.*;

import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
	
//import play.api.libs.json.*;
import org.json.simple.JSONObject;
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

import org.yaml.snakeyaml.Yaml;

import java.io.File;

import models.*;


@Singleton
public class SVG2Browser extends Controller 
{

	private final Form<ExperimentForm> formSelExp; // lo utilizamos en todos los forms
	private MessagesApi messagesApi;	
	private final List<ExperimentData> listExperiments;
	private final String mapStrDesc;

	private final Logger logger = LoggerFactory.getLogger(getClass()) ;
	
	@Inject
	public SVG2Browser(FormFactory formFactory, MessagesApi messagesApi) 
	{
		this.formSelExp 		= formFactory.form(ExperimentForm.class);
		this.messagesApi 		= messagesApi;
		//this.listExperiments 	= new ArrayList<ExperimentData>();
		this.listExperiments	= com.google.common.collect.Lists.newArrayList();
		
        Yaml yaml 				= new Yaml();

        // Foreach directory
        /// recover experiment data and create object with info
        
        File directory = new File("conf/experiments");
        File[] dirList = directory.listFiles();
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
    	StringWriter out = new StringWriter();
    	
    	for( ExperimentData exp : this.listExperiments) {
    		mapRawDesc.put( exp.experimentID, exp.experimentDesc );
    	}
    
    	try {
    		  mapRawDesc.writeJSONString(out);  
    	}
    	catch( java.io.IOException e ) {
    		  System.out.println("Booooom: " + e.toString() );
    	}
    	
    	mapStrDesc = out.toString();
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
    	    	
        return ok(views.html.inputSelection.render( asScala(listExperiments), formSelExp, mapStrDesc,
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
    
    public Result aboutSVG2Browser( Http.Request request ) {
        return ok(views.html.about.render());
    }
    
    public Result generateResults( Http.Request request ) {
    	
    	final Form<ExperimentForm> boundForm = formSelExp.bindFromRequest(request);

        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.inputSelection.render( asScala(listExperiments), formSelExp, mapStrDesc,
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
