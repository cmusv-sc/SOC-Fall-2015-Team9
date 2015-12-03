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
    private static final String EDIT_RATING_CALL = Constants.NEW_BACKEND + "rating/editRating";
    private static final String DELETE_RATING_CALL = Constants.NEW_BACKEND + "rating/deleteRating/";

    final static Form<Rating> ratingForm = Form.form(Rating.class);
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String failJson(String msg){
        ObjectNode response = Json.newObject();
        response.put("success", false);
        response.put("message", msg);

        return response.toString();
    }

    public Result getRating(String url){
        System.out.println("GET RATING");

        ClimateService element = ClimateService.findServiceByUrl(url);
        JsonNode response = null;

        try{
            response = APICall.callAPI(GET_RATING_CALL + element.getId() + "/" + session("email") + "/json");
        }
        catch (IllegalStateException e){
            e.printStackTrace();
            return ok(failJson("Fail to connect"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ok(failJson("Fail to connect"));
        }

        return ok(response.toString());
    }

//    public Result postRating(String url){
//        System.out.println("POST RATING");
//
//        if (session("email") == null){
//            return ok(failJson("Please login first"));
//        }
//        int rate = DynamicForm.form().bindFromRequest().get("rate");
//        long userId = DynamicForm.form().bindFromRequest().get("user_id");
//
//        ClimateService element = ClimateService.findServiceByUrl(url);
//        ObjectNode jsonData = Json.newObject();
//        JsonNode response = null;
//
//        try{
//            Date date = new Date();
//            jsonData.put("posted_date", dateFormat.format(date));
//            jsonData.put("parent_id", parent_id);
//            jsonData.put("text", text);
//            jsonData.put("email", session("email"));
//            jsonData.put("climate_service_id", element.getId());
//            response = APICall.postAPI(POST_COMMENT_CALL, jsonData);
//        }
//        catch (IllegalStateException e){
//            e.printStackTrace();
//            return ok(failJson("Fail to connect"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return ok(failJson("Fail to connect"));
//        }
//
//        return ok(response.toString());
//    }

//    public Result editRating(String url, String id){
//        System.out.println("EDIT COMMENT");
//
//        String text = DynamicForm.form().bindFromRequest().get("text");
//        String comment_id = id;
//        ClimateService element = ClimateService.findServiceByUrl(url);
//        ObjectNode jsonData = Json.newObject();
//        JsonNode response = null;
//
//        try{
//            Date date = new Date();
//            jsonData.put("posted_date", dateFormat.format(date));
//            jsonData.put("comment_id", comment_id);
//            jsonData.put("text", text);
//            jsonData.put("climate_service_id", element.getId());
//            response = APICall.putAPI(EDIT_COMMENT_CALL, jsonData);
//        }
//        catch (IllegalStateException e){
//            e.printStackTrace();
//            return ok(failJson("Fail to connect"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return ok(failJson("Fail to connect"));
//        }
//
//        return ok(response.toString());
//    }
//
//    public Result deleteComment(String url){
//        System.out.println("DELETE COMMENT");
//
//        String comment_id = DynamicForm.form().bindFromRequest().get("comment_id");
//        ClimateService element = ClimateService.findServiceByUrl(url);
//        JsonNode response = null;
//
//        try{
//            response = APICall.deleteAPI(DELETE_COMMENT_CALL + element.getId() + "/" + comment_id);
//        }
//        catch (IllegalStateException e){
//            e.printStackTrace();
//            return ok(failJson("Fail to connect"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return ok(failJson("Fail to connect"));
//        }
//
//        return ok(response.toString());
//    }
}
