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

import static play.libs.Scala.asScala;

import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;

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
		this.formSelExp = formFactory.form(ExperimentForm.class);
		
        this.messagesApi = messagesApi;
     
        
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
         .getClassLoader()
         .getResourceAsStream("experiments/brady/experiment_config.yml");
        //ExperimentConfig econf = (ExperimentConfig) yaml.loadAs(inputStream, ExperimentConfig.class);
        /*
        Iterable<Object> itr = yaml.loadAll(inputStream);
        System.out.println("Probando 1 2 3");
        for (Object o : itr) {
            System.out.println("Loaded object type:" + o.getClass());
            System.out.println(o);
        }
        */
        
        //Map<String, Map<String, String>> persons = (Map<String, Map<String, String>>).yaml.load(inputStream);
        Map<String, Map<String,String>> econfs = (Map<String, Map<String,String>>) yaml.load(inputStream);
        
        for(String key : econfs.keySet())
        {
            System.out.println("key = " + key);
            Map<String,String> econf = econfs.get(key);
            
            for (String valueKey : econf.keySet())
            {
                System.out.println(valueKey + " = " + econf.get(valueKey));
            }
              
        }
        
        
        this.listExperiments = com.google.common.collect.Lists.newArrayList(
                new ExperimentData("experiment1", "Detailed description about experiment1)"),
                new ExperimentData("experiment2", "Detailed description about experiment2)")
                //new ExperimentData( econf.experiment_id,  econf.experiment_name )
        );
		
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
