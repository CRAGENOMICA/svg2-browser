package controllers;


import play.mvc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import play.data.validation.Constraints;


import models.*;


public class ExperimentForm {

    @Constraints.Required
    private String experimentID;

    private List<String> geneList;
    
    private String colorSVG;
    
    private String colorBarplot;
    
    private Boolean showGenesInTissues;

    public ExperimentForm() {
    }
    
    public void setExperimentID(String _experimentID) {
        this.experimentID = _experimentID;
    }
    
    public String getExperimentID() {
    	return this.experimentID;
    }
}