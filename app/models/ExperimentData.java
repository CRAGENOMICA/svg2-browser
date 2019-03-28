package models;

/**
 * Presentation object used for displaying data in a template.
 *
 * Note that it's a good practice to keep the presentation DTO,
 * which are used for reads, distinct from the form processing DTO,
 * which are used for writes.
 */
public class ExperimentData {
	
    public String experimentID;
    public String experimentName;
    public String experimentDesc;
    public String experimentDatafile;
    public String experimentSVGfile;
    
    public ExperimentData(String _expID, String _expName, String _expDesc, String _expDatafile, String _expSVGfile ) 
    {
    	
        this.experimentID = _expID;
        this.experimentName = _expName;
        this.experimentDesc = _expDesc;
        this.experimentDatafile = _expDatafile;
        this.experimentSVGfile = _expSVGfile;
    }
    
    
    //creo que no necesito getter and setters
    public String getExperimentID() {
    	return this.experimentID;
    }
    
    public String getExperimentDesc() {
    	return this.experimentDesc;
    }


	public String getExperimentDatafile() {
		return experimentDatafile;
	}


	public void setExperimentDatafile(String experimentDatafile) {
		this.experimentDatafile = experimentDatafile;
	}


	public String getExperimentSVGfile() {
		return experimentSVGfile;
	}


	public void setExperimentSVGfile(String experimentSVGfile) {
		this.experimentSVGfile = experimentSVGfile;
	}
  
}
