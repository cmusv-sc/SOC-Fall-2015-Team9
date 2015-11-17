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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import play.libs.Json;

import models.*;
import util.Common;
import util.Constants;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.gson.Gson;

@Named
@Singleton
public class CommentController extends Controller{
    private final CommentRepository commentRepository;

    @Inject
    public CommentController(final CommentRepository commentRepository){
	this.commentRepository = commentRepository;
    }

    private String failJson(String msg){
	ObjectNode response = Json.newObject();
	response.put("success", false);
	response.put("message", msg);

	return response.toString();
    }
    
    public Result getComment(Long id, String format){
	String result = "{\"results\":{\"comments\":[{\"comment_id\":\"1\",\"parent_id\":\"0\",\"in_reply_to\":null,\"element_id\":\"134\",\"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\":\"2013-02-27 09:03:25\",\"text\":\"Testmessageone\",\"attachments\":[],\"childrens\":[]},{\"comment_id\":\"2\",\"parent_id\":\"0\",\"in_reply_to\":null,\"element_id\":\"134\",\"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\":\"2015-02-27 09:03:25\",\"text\":\"Testmessageone\",\"attachments\":[],\"childrens\":[]}],\"total_comment\":2,\"user\":{\"user_id\":1,\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"is_logged_in\":true,\"is_add_allowed\":true,\"is_edit_allowed\":true}}}";

	System.out.println("GET COMMENT");
	long totalComment = 0;
	ObjectNode response = Json.newObject();
	ObjectNode user = Json.newObject();

	List<Comment> comments = commentRepository.findAllByClimateServiceId(id);

	ArrayNode apps = JsonNodeFactory.instance.arrayNode();
	ObjectNode appsObj = JsonNodeFactory.instance.objectNode();
	appsObj.put("user_id", 1);
	appsObj.put("fullname", "zhibin");
	apps.add(appsObj);
	apps.add(appsObj);
	response.put("apps", apps);
	
	// System.out.println("GET COMMENT FROM MYSQL:");
	// for (Comment comment : comments){
	//     System.out.println(comment);
	// }

	// User node
	user.put("user_id", 1);
	user.put("fullname", "Admin");
	user.put("picture", "/assets/images/user_blank_picture.png");
	user.put("is_logged_in", true);
	user.put("is_add_allowed", true);
	user.put("is_edit_allowed", true);

	response.put("total_comment", totalComment);
	response.put("user", user);

	System.out.println(response.toString());
	
	return ok(result);
    }

    public Result postComment(){
	System.out.println("POST COMMENT");

	ObjectNode response = Json.newObject();
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out.println("Comment not saved, expecting Json data");
	    return badRequest("Comment not saved, expecting Json data");
	}

	try{
	    long parentId = json.findPath("parent_id").asLong();
	    String text = json.findPath("text").asText();
	    long userId = json.findPath("user_id").asLong();
	    long serviceId = json.findPath("climate_service_id").asLong();
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date postedDate = format.parse(json.findPath("posted_date").asText());
	    String inReplyTo = null;

	    // if inside reply
	    if (parentId != 0){
		inReplyTo = "Admin";
	    }
	    
	    Comment comment = new Comment(parentId, inReplyTo, serviceId, 1, "Admin", "/assets/images/user_blank_picture.png", postedDate, text);
	    Comment commentEntry = commentRepository.save(comment);

	    response.put("success", true);
	    response.put("comment_id", commentEntry.getCommentId());
	    response.put("parent_id", commentEntry.getParentId());
	    response.put("created_by", commentEntry.getCreatedBy());
	    response.put("fullname", commentEntry.getFullname());
	    response.put("picture", commentEntry.getPicture());
	    response.put("posted_date", format.format(commentEntry.getPostedDate()));
	    response.putArray("childrens");
	    response.put("text", commentEntry.getText());
	    response.put("is_logged_in", true);
	    response.put("is_edit_allowed", true);
	    response.put("is_add_allowed", true);
	}
	catch (ParseException pe){
	    pe.printStackTrace();
	    System.out.println("Invalid date format");
	    return badRequest(failJson("Invalid date format"));
	}
	catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Comment not saved");
	    return badRequest(failJson("Comment not saved"));
	}

	//String result = "{\"success\": true, \"comment_id\": \"3\", \"parent_id\":\"2\", \"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\": \"2015-02-27 09:03:25\", \"childrens\": [], \"text\": \"heheh\",\"is_logged_in\":true,\"is_add_allowed\":true,\"is_edit_allowed\":true}";

	return ok(response);
    }

    public Result editComment(){
	System.out.println("EDIT COMMENT");

	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out.println("Comment not updated, expecting Json data");
	    return badRequest(failJson("Comment not updated, expecting Json data"));
	}
	System.out.println(json.toString());

	String result = "{\"success\": true, \"comment_id\": \"3\", \"parent_id\":\"2\", \"posted_date\": \"2013-02-27 09:03:25\", \"childrens\": [], \"text\": \"heheh\"}";

	return ok(result);
    }

    public Result deleteComment(Long service_id, Long comment_id){
	System.out.println("DELETE COMMENT");

	String result = "{\"success\": true, \"total_comment\": 2}";

	return ok(result);
    }
}
