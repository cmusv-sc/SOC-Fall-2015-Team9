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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    private final ClimateServiceRepository climateServiceRepository;
    private final HashTagRepository hashTagRepository;
    private final UserRepository userRepository;
    private final MentionRepository mentionRepository;
    private final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+|\\W+)");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    public CommentController(final CommentRepository commentRepository,
			     final ClimateServiceRepository climateServiceRepository,
			     final HashTagRepository hashTagRepository,
			     final UserRepository userRepository,
			     final MentionRepository mentionRepository){
	this.commentRepository = commentRepository;
	this.climateServiceRepository = climateServiceRepository;
	this.hashTagRepository = hashTagRepository;
	this.userRepository = userRepository;
	this.mentionRepository = mentionRepository;
    }

    private String failJson(String msg){
	ObjectNode response = Json.newObject();
	response.put("success", false);
	response.put("message", msg);

	return response.toString();
    }

    private String checkMention(String text){
	List<String> usernames = userRepository.getAllUsername();

	for (String username : usernames){
	    String mentionUsername = '@' + username + ' ';
	    int index = text.indexOf(mentionUsername);

	    if (index >= 0){
		User user  = userRepository.findByUsername(username);
		user.setUnreadMention(true);
		userRepository.save(user);
		
		String before = text.substring(0, index);
		String after = text.substring(index + username.length() + 1);
		text = before + "<b style=\"background-color: #59D0F7\">" + username + "</b>" + after;
	    }
	}

	return text;
    }

    private void saveMention(String text, Long commentId){
	List<String> usernames = userRepository.getAllUsername();

	for (String username : usernames){
	    String mentionUsername = '@' + username + ' ';
	    int index = text.indexOf(mentionUsername);

	    if (index >= 0){
		Mention mention = new Mention(username, commentId);
		mentionRepository.save(mention);
	    }
	}
    }

    private ArrayNode getCommentArray(Long elementId, Long parentId){
	List<Comment> comments = commentRepository.findAllByClimateServiceIdAndParentId(elementId, parentId);
	ArrayNode commentArray = JsonNodeFactory.instance.arrayNode();

	for (Comment comment : comments){
	    ObjectNode oneComment = JsonNodeFactory.instance.objectNode();
	    oneComment.put("comment_id", comment.getCommentId());
	    oneComment.put("parent_id", parentId);
	    oneComment.put("in_reply_to", comment.getInReplyTo());
	    oneComment.put("element_id", elementId);
	    oneComment.put("created_by", comment.getCreatedBy());
	    oneComment.put("fullname", comment.getFullname());
	    oneComment.put("picture", comment.getPicture());
	    oneComment.put("posted_date", timeFormat.format(comment.getPostedDate()));
	    oneComment.put("text", comment.getText());
	    oneComment.put("attachments", JsonNodeFactory.instance.arrayNode());
	    oneComment.put("childrens", getCommentArray(elementId, comment.getCommentId()));
	    commentArray.add(oneComment);
	}

	return commentArray;
    }

    private void deleteCommentById(Long elementId, Long commentId){
	List<Comment> comments = commentRepository.findAllByClimateServiceIdAndParentId(elementId, commentId);

	for (Comment comment : comments){
	    deleteCommentById(elementId, comment.getCommentId());
	    commentRepository.delete(comment);
	}

	commentRepository.delete(commentRepository.findCommentById(commentId));
    }
    
    public Result getComment(Long id, String email, String format){
	System.out.println("GET COMMENT");
	ObjectNode response = Json.newObject();
	ObjectNode result = Json.newObject();
	ObjectNode user = Json.newObject();
	JsonNode json = request().body().asJson();
	
	// User node
	if (userRepository.getUserIdByEmail(email) == null){
	    user.put("user_id", -1);
	    user.put("fullname", "Visitor");
	    user.put("is_logged_in", false);
	    user.put("is_add_allowed", false);
	    user.put("is_edit_allowed", false);
	}
	else{
	    user.put("user_id", userRepository.getUserIdByEmail(email));
	    user.put("fullname", userRepository.getUsernameByEmail(email));
	    user.put("is_logged_in", true);
	    user.put("is_add_allowed", true);
	    user.put("is_edit_allowed", true);
	}
	user.put("picture", "/assets/images/user_blank_picture.png");

	// result
	result.put("comments", getCommentArray(id, 0L));
	result.put("total_comment", commentRepository.countComments(id));
	result.put("user", user);

	// response
	response.put("results", result);

	return ok(response.toString());
    }


    private void addHashTags(Comment comment) {
	Matcher mat = HASHTAG_PATTERN.matcher(comment.getText());

	List<HashTag> htags = new ArrayList<HashTag>();
	while (mat.find()) {
	    String hashTag = mat.group(1);
	    String serviceName = hashTag.substring(1);
	    List<ClimateService> services = climateServiceRepository.findAllByName(serviceName);
	    if (!services.isEmpty()) {
		ClimateService service = services.get(0);
		HashTag htag = new HashTag(comment, service, hashTag);
		htags.add(htag);
	    }
	}
	hashTagRepository.save(htags);
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
	    String text = checkMention(json.findPath("text").asText());
	    String email = json.findPath("email").asText();
	    Long createdBy = userRepository.getUserIdByEmail(email);
	    String fullname = userRepository.getUsernameByEmail(email);
	    
	    long serviceId = json.findPath("climate_service_id").asLong();
	    Date postedDate = timeFormat.parse(json.findPath("posted_date").asText());
	    String inReplyTo = null;

	    // if inside reply
	    if (parentId != 0){
		inReplyTo = userRepository.getUsernameById(parentId);
	    }
	    
	    Comment comment = new Comment(parentId, inReplyTo, serviceId, createdBy, fullname, "/assets/images/user_blank_picture.png", postedDate, text);
	    Comment commentEntry = commentRepository.save(comment);
	    addHashTags(commentEntry);
	    saveMention(json.findPath("text").asText(), commentEntry.getCommentId());

	    response.put("success", true);
	    response.put("comment_id", commentEntry.getCommentId());
	    response.put("parent_id", commentEntry.getParentId());
	    response.put("created_by", commentEntry.getCreatedBy());
	    response.put("fullname", commentEntry.getFullname());
	    response.put("picture", commentEntry.getPicture());
	    response.put("posted_date", timeFormat.format(commentEntry.getPostedDate()));
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

	return ok(response);
    }

    public Result editComment(){
	System.out.println("EDIT COMMENT");

	ObjectNode response = Json.newObject();
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out.println("Comment not updated, expecting Json data");
	    return badRequest(failJson("Comment not updated, expecting Json data"));
	}
	System.out.println(json.toString());

	try{
	    String text = checkMention(json.findPath("text").asText());
	    Long commentId = json.findPath("comment_id").asLong();
	    Date postedDate = timeFormat.parse(json.findPath("posted_date").asText());

	    Comment comment = commentRepository.findCommentById(commentId);
	    comment.setText(text);
	    comment.setPostedDate(postedDate);
	    Comment commentEntry = commentRepository.save(comment);
	    saveMention(json.findPath("text").asText(), commentEntry.getCommentId());

	    response.put("success", true);
	    response.put("text", commentEntry.getText());
	}
	catch (ParseException pe){
	    pe.printStackTrace();
	    System.out.println("Invalid date format");
	    return badRequest(failJson("Invalid date format"));
	}
	catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Comment not updated");
	    return badRequest(failJson("Comment not updated"));
	}

	return ok(response.toString());
    }

    public Result deleteComment(Long service_id, Long comment_id){
	System.out.println("DELETE COMMENT");

	ObjectNode response = Json.newObject();

	try{
	    deleteCommentById(service_id, comment_id);

	    response.put("success", true);
	    response.put("total_comment", commentRepository.countComments(service_id));
	}
	catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Comment not deleted");
	    return badRequest(failJson("Comment not deleted"));
	}

	return ok(response.toString());
    }

    public Result getMentions(String email){
	String username = userRepository.getUsernameByEmail(email);
	List<Long> commentIds = mentionRepository.findAllCommentIdByUsername(username);

	return ok("tmp");
    }
}
