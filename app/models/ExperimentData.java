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
    public String experimentDesc;

    public ExperimentData(String _expID, String _expDesc ) 
    {
    	
        this.experimentID = _expID;
        this.experimentDesc = _expDesc;
    }
    
    public String getExperimentID() {
    	return this.experimentID;
    }
    
    public String getExperimentDesc() {
    	return this.experimentDesc;
    }
}
