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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import models.*;
import util.Common;
import util.Constants;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

@Named
@Singleton
public class CommentController extends Controller{
    private final CommentRepository commentRepository;

    @Inject
    public CommentController(final CommentRepository commentRepository){
	this.commentRepository = commentRepository;
    }

    public Result getComment(Long id, String format){
	String result = "{\"results\":{\"comments\":[{\"comment_id\":\"1\",\"parent_id\":\"0\",\"in_reply_to\":null,\"element_id\":\"134\",\"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\":\"2013-02-2709:03:25\",\"text\":\"Testmessageone\",\"attachments\":[],\"childrens\":[]},{\"comment_id\":\"2\",\"parent_id\":\"0\",\"in_reply_to\":null,\"element_id\":\"134\",\"created_by\":\"1\",\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"posted_date\":\"2013-02-2709:03:25\",\"text\":\"Testmessageone\",\"attachments\":[],\"childrens\":[]}],\"total_comment\":2,\"user\":{\"user_id\":1,\"fullname\":\"Administratoradmin\",\"picture\":\"/assets/images/user_blank_picture.png\",\"is_logged_in\":true,\"is_add_allowed\":true,\"is_edit_allowed\":true}}}";
	return ok(result);
    }
}
