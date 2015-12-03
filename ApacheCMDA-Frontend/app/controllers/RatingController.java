package controllers;

import models.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Comment;
import models.metadata.ClimateService;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Console;
import util.APICall;
import util.APICall.ResponseType;
import util.Constants;
import views.html.climate.*;
import play.data.DynamicForm;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RatingController extends Controller{
    private static final String GET_RATING_CALL = Constants.NEW_BACKEND + "rating/getRating/";
    private static final String POST_RATING_CALL = Constants.NEW_BACKEND + "rating/postRating";

    final static Form<Rating> ratingForm = Form.form(Rating.class);

    private String failJson(String msg){
        ObjectNode response = Json.newObject();
        response.put("success", false);
        response.put("message", msg);

        return response.toString();
    }

    public Result getRating(String url, Long version){
        System.out.println("GET RATING");

        ClimateService element = ClimateService.findServiceByUrl(url);
        JsonNode response = null;

        try{
            response = APICall.callAPI(GET_RATING_CALL + element.getId() + "/" + version + "/" + session("email") + "/json");
        }
        catch (IllegalStateException e){
            e.printStackTrace();
            return ok(failJson("Fail to connect"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ok(failJson("Fail to connect"));
        }
        System.out.println(response);
        return ok(response);
    }

    public Result postRating(Long rate, String url, Long version){
        System.out.println("POST RATING");

        if (session("email") == null){
            return ok(failJson("Please login first"));
        }

        ClimateService element = ClimateService.findServiceByUrl(url);
        ObjectNode jsonData = Json.newObject();
        JsonNode response = null;

        try{
            jsonData.put("email", session("email"));
            jsonData.put("service_id", element.getId());
            jsonData.put("version_id", version);
            jsonData.put("rate", rate);
            response = APICall.postAPI(POST_RATING_CALL, jsonData);
        }
        catch (IllegalStateException e){
            e.printStackTrace();
            return ok(failJson("Fail to connect"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ok(failJson("Fail to connect"));
        }

        System.out.println(response);
        return ok(response);
    }
}
