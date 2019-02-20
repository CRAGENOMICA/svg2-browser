package controllers;

import play.mvc.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class SVG2Browser extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    public Result inputSelection() {
        return ok(views.html.inputSelection.render());
    }
    
    public Result showResults() {
        return ok(views.html.showResults.render());
    }
    
    public Result aboutSVG2Browser() {
        return ok(views.html.about.render());
    }
}
