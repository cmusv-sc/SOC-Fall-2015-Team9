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
    final static Form<Comment> commentForm = Form.form(Comment.class);

    public Result getComment(String name){
	String result = "{\"results\":{\"comments\":[{\"comment_id\":\"1\",\"parent_id\":\"0\",\"in_reply_to\":null,\"element_id\":\"134\",\"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\":\"2013-02-2709:03:25\",\"text\":\"Testmessageone\",\"attachments\":[],\"childrens\":[]},{\"comment_id\":\"2\",\"parent_id\":\"0\",\"in_reply_to\":null,\"element_id\":\"134\",\"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\":\"2013-02-2709:03:25\",\"text\":\"Testmessageone\",\"attachments\":[],\"childrens\":[]}],\"total_comment\":2,\"user\":{\"user_id\":1,\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"is_logged_in\":true,\"is_add_allowed\":true,\"is_edit_allowed\":true}}}";
	
	// try {
	//     jsonData.put("creatorId", 1);
	//     DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	//     Date date = new Date();
	//     jsonData.put("createTime", dateFormat.format(date));
	//     result = jsonData.toString();
	// } catch (IllegalStateException e) {
	//     e.printStackTrace();
	// } catch (Exception e) {
	//     e.printStackTrace();
	// }
	//System.out.println(result);
	System.out.println("GET COMMENT");
	return ok(result);
    }

    public Result postComment(String url){
	String text = DynamicForm.form().bindFromRequest().get("text");
	String parent_id = DynamicForm.form().bindFromRequest().get("parent_id");
	ClimateService element = ClimateService.findServiceByUrl(url);

	System.out.println("POST COMMENT");
	System.out.println("Url: " + url);
	System.out.println("Text: " + text);
	System.out.println("Parent ID: " + parent_id);

	ObjectNode jsonData = Json.newObject();
	String result = "{\"success\": true, \"comment_id\": \"3\", \"parent_id\":\"2\", \"posted_date\": \"2013-02-27 09:03:25\", \"childrens\": [], \"text\": \"heheh\"}";

	return ok(result);
    }

    public Result editComment(String url, String id){
	String text = DynamicForm.form().bindFromRequest().get("text");
	String parent_id = DynamicForm.form().bindFromRequest().get("parent_id");
	String comment_id = id;
	ClimateService element = ClimateService.findServiceByUrl(url);

	System.out.println("EDIT COMMENT");
	System.out.println("Url: " + url);
	System.out.println("Text: " + text);
	System.out.println("Parent ID: " + parent_id);
	System.out.println("Comment ID: " + comment_id);

	ObjectNode jsonData = Json.newObject();
	String result = "{\"success\": true, \"comment_id\": \"" + id + "\", \"parent_id\":\"2\", \"posted_date\": \"2013-02-27 09:03:25\", \"childrens\": [], \"text\": \""+ text + "\"}";

	return ok(result);
    }

    public Result deleteComment(String url){
	String comment_id = DynamicForm.form().bindFromRequest().get("comment_id");
	ClimateService element = ClimateService.findServiceByUrl(url);

	System.out.println("DELETE COMMENT");
	System.out.println("Url: " + url);
	System.out.println("Comment ID: " + comment_id);

	ObjectNode jsonData = Json.newObject();
	String result = "{\"success\": true, \"total_comment\": 2}";

	return ok(result);
    }
}
