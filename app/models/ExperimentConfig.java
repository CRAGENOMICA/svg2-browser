package models;

//import java.util.LinkedHashMap;

import java.util.HashMap;

/**
 * Presentation object used for displaying data in a template.
 *
 * Note that it's a good practice to keep the presentation DTO,
 * which are used for reads, distinct from the form processing DTO,
 * which are used for writes.
 */
public class ExperimentConfig {
	
    public String experiment_id;
    public String experiment_name;
    public String experiment_description;
    public String experiment_datafile;
    public String experiment_svgfile;

    public ExperimentConfig(String _id, String _name, String _description, String _datafile, String _svgfile )
    {
        this.experiment_id = _id;
        this.experiment_name = _name;
        this.experiment_description = _description;
        this.experiment_datafile = _datafile;
        this.experiment_svgfile = _svgfile;
    }
/*    
    public String getExperiment_id(){
	    return this.experiment_id;  
    };
	
    public String getExperiment_name(){
	    return this.experiment_name;
	};
	
//	public HashMap<Integer, String> getExperiment_description(){
//	    return this.experiment_description;
//	};
	
	public String getExperiment_datafile(){
	    return this.experiment_datafile;
	};
	
	public String getExperiment_svgfile(){
	    return this.experiment_svgfile;
	};
	
	public void setExperiment_id( String _experiment_id ) {
	  this.experiment_id = _experiment_id;
	}
	public void setExperiment_name( String _experiment_name ) {
	  this.experiment_name = _experiment_name;
	}
//	public void setExperiment_description( HashMap<Integer, String> _experiment_description ) {
//	  this.experiment_description = _experiment_description;
//	}
	public void setExperiment_datafile( String _experiment_datafile ) {
	  this.experiment_datafile = _experiment_datafile;
	}
	public void setExperiment_svgfile( String _experiment_svgfile ) {
	  this.experiment_svgfile = _experiment_svgfile;
	}
*/
}



/*
svg2_config:
  experiment_id: brady
  experiment_name: Brady Super power
  experiment_description:
    El experimento fue...
    muy bonito...
  experiment_datafile: Arabidopsis_root.RData
  experiment_svgfile: experiment.svg
*/