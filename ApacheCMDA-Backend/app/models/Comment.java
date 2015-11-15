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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Comment{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long commentId;
    private long parentId;
    private String inReplyTo;
    private long elementId;
    private long createdBy;
    private String fullname;
    private String picture;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;
    private String text;

    public Comment(){
	
    }

    public Comment(long parentId, String inReplyTo, long elementId, long createdBy, String fullname,
		   String picture, Date postedDate, String text){
	super();
	this.parentId = parentId;
	this.inReplyTo = inReplyTo;
	this.elementId = elementId;
	this.createdBy = createdBy;
	this.fullname = fullname;
	this.picture = picture;
	this.postedDate = postedDate;
	this.text = text;
    }

    public long getCommentId(){
	return commentId;
    }
    public void setCommentId(long commentId){
	this.commentId = commentId;
    }

    public long getParentId(){
	return parentId;
    }
    public void setParentId(long parentId){
	this.parentId = parentId;
    }
    
    public String getInReplyTo(){
	return inReplyTo;
    }
    public void setInReplyTo(String inReplyTo){
	this.inReplyTo = inReplyTo;
    }

    public long getElementId(){
	return elementId;
    }
    public void setElementId(long elementId){
	this.elementId = elementId;
    }

    public long getCreatedBy(){
	return createdBy;
    }
    public void setCreatedBy(long createdBy){
	this.createdBy = createdBy;
    }

    public String getFullname(){
	return fullname;
    }
    public void setFullname(String fullname){
	this.fullname = fullname;
    }

    public String getPicture(){
	return picture;
    }
    public void setPicture(String picture){
	this.picture = picture;
    }

    public Date getPostedDate(){
	return postedDate;
    }
    public void setPostedDate(Date postedDate){
	this.postedDate = postedDate;
    }

    public String getText(){
	return text;
    }
    public void setText(String text){
	this.text = text;
    }

    @Override
    public String toString() {
	return "Comment from " + fullname + " @ " + postedDate + ": " + text;
    }
}
