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
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Mention{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mentionId;
    private String username;
    private Long commentId;
    
    public Mention(){
	
    }
    
    public Mention(String username, Long commentId){
	super();
	this.username = username;
	this.commentId = commentId;
    }
    
    public Long getMentionId(){
	return mentionId;
    }
    
    public void setMentionId(Long mentionId){
	this.mentionId = mentionId;
    }

    public String getUsername(){
	return username;
    }

    public void setUsername(String username){
	this.username = username;
    }
    
    public Long getCommentId(){
	return commentId;
    }
    
    public void setCommentId(Long commentId){
	this.commentId = commentId;
    }
}
