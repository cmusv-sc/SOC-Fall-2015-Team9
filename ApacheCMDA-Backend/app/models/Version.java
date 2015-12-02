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
public class Version{
    @Id
    private long id;
    private long serviceId;
    private long versionId;
    private String url;

    public Version(){
    }

    public Version(long id, long serviceId, long versionId, String url){
	super();
	this.id = id;
	this.serviceId = serviceId;
	this.versionId = versionId;
	this.url = url;
    }

    public long getServiceId(){
	return serviceId;
    }
    public void setServiceId(Long serviceId){
	this.serviceId = serviceId;
    }

    public long getVersionId(){
	return versionId;
    }
    public void setVersionId(Long versionId){
	this.versionId = versionId;
    }

    public String getUrl(){
	return url;
    }
    public void setUrl(String url){
	this.url = url;
    }

    @Override
    public String toString() {
	return "Service " + serviceId + " version " + versionId + ": " + url;
    }
}
