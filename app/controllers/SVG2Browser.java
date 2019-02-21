package controllers;

import play.mvc.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static play.libs.Scala.asScala;

import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;

import java.io.File;

import models.*;


@Singleton
public class SVG2Browser extends Controller 
{

	private final Form<ExperimentForm> formSelExp; // lo utilizamos en todos los forms
	private MessagesApi messagesApi;	
	private final List<ExperimentData> listExperiments;

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
        		       		  
        		//for (String valueKey : econf.keySet())
        		//{
        		//	System.out.println(valueKey + " = " + econf.get(valueKey));
        		//}
        		
        	}
            System.out.println("ExpID: " + file.getName() + " retrieved.");
        }
        
        
        /*
        Iterable<Object> itr = yaml.loadAll(inputStream);
        System.out.println("Probando 1 2 3");
        for (Object o : itr) {
            System.out.println("Loaded object type:" + o.getClass());
            System.out.println(o);
        }
        */
        
        //Map<String, Map<String, String>> persons = (Map<String, Map<String, String>>).yaml.load(inputStream);
        
        
    //    this.listExperiments = com.google.common.collect.Lists.newArrayList(
     //           new ExperimentData("experiment1", "Detailed description about experiment1)", "A", "B", "C"),
      //          new ExperimentData("experiment2", "Detailed description about experiment2)", "A", "B", "C"),
        //        new ExperimentData(listExperiments.get(0).experimentID,  listExperiments.get(0).experimentName, "A", "B", "C" )
        //);
		
	}
	

    public Result index() {
    	
        //return ok(views.html.index.render());
    	return ok(views.html.index.render() );
    }

    public Result inputSelection( Http.Request request ) {
        return ok(views.html.inputSelection.render( asScala(listExperiments), formSelExp,
    			request, messagesApi.preferred(request) ));
    }
    
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
            return badRequest(views.html.inputSelection.render( asScala(listExperiments), formSelExp,
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
