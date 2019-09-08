package controllers.api;

import play.mvc.Controller;
import play.mvc.Result;

public class DocController extends Controller {

    /**
     * Display interactive API docs
     */
    public Result index() {
        return ok(views.html.api.docs.render());
    }
}
