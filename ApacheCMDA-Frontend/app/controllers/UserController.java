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

import models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

public class UserController extends Controller{
    private static final String GET_USER_LIST_CALL = Constants.NEW_BACKEND + "users/getAllUsername";
    final static Form<User> userForm = Form.form(User.class);

    private String failJson(String msg){
	ObjectNode response = Json.newObject();
	response.put("success", false);
	response.put("message", msg);

	return response.toString();
    }
    
    public Result getAllUsername(){
	System.out.println("Get user list");

	JsonNode response = null;

	try{
	    response = APICall.callAPI(GET_USER_LIST_CALL);
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
