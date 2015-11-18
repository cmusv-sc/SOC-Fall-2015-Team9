/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

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

public class CommentController extends Controller{
    private static final Long USER_ID = 0L;
    private static final String GET_COMMENT_CALL = Constants.NEW_BACKEND + "comment/getComment/";
    private static final String POST_COMMENT_CALL = Constants.NEW_BACKEND + "comment/postComment";
    private static final String EDIT_COMMENT_CALL = Constants.NEW_BACKEND + "comment/editComment";
    private static final String DELETE_COMMENT_CALL = Constants.NEW_BACKEND + "comment/deleteComment/";
    
    final static Form<Comment> commentForm = Form.form(Comment.class);
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String failJson(String msg){
	ObjectNode response = Json.newObject();
	response.put("success", false);
	response.put("message", msg);

	return response.toString();
    }

    public Result getComment(String url){
	System.out.println("GET COMMENT");
	
	ClimateService element = ClimateService.findServiceByUrl(url);
	JsonNode response = null;

	try{
	    response = APICall.callAPI(GET_COMMENT_CALL + element.getId() + "/" + session("email") + "/json");
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

    public Result postComment(String url){
	System.out.println("POST COMMENT");

	if (session("email") == null){
	    return ok(failJson("Please login first"));
	}
	String text = DynamicForm.form().bindFromRequest().get("text");
	String parent_id = DynamicForm.form().bindFromRequest().get("parent_id");
	ClimateService element = ClimateService.findServiceByUrl(url);
	ObjectNode jsonData = Json.newObject();
	JsonNode response = null;
	
	try{
	    Date date = new Date();
	    jsonData.put("posted_date", dateFormat.format(date));
	    jsonData.put("parent_id", parent_id);
	    jsonData.put("text", text);
	    jsonData.put("email", session("email"));
	    jsonData.put("climate_service_id", element.getId());
	    response = APICall.postAPI(POST_COMMENT_CALL, jsonData);
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

    public Result editComment(String url, String id){
	System.out.println("EDIT COMMENT");
	
	String text = DynamicForm.form().bindFromRequest().get("text");
	String comment_id = id;
	ClimateService element = ClimateService.findServiceByUrl(url);
	ObjectNode jsonData = Json.newObject();
	JsonNode response = null;
	
	try{
	    Date date = new Date();
	    jsonData.put("posted_date", dateFormat.format(date));
	    jsonData.put("comment_id", comment_id);
	    jsonData.put("text", text);
	    jsonData.put("climate_service_id", element.getId());
	    response = APICall.putAPI(EDIT_COMMENT_CALL, jsonData);
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

    public Result deleteComment(String url){
	System.out.println("DELETE COMMENT");
	
	String comment_id = DynamicForm.form().bindFromRequest().get("comment_id");
	ClimateService element = ClimateService.findServiceByUrl(url);
	JsonNode response = null;

	try{
	    response = APICall.deleteAPI(DELETE_COMMENT_CALL + element.getId() + "/" + comment_id);
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
}
