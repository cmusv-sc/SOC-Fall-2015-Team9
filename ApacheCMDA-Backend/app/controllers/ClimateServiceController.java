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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.gson.Gson;

import play.libs.Json;

class CompareServiceById implements Comparator<ClimateService> {
    @Override
	public int compare(ClimateService cs1, ClimateService cs2) {
	return Long.compare(cs1.getId(), cs2.getId());
    }
};

/**
 * The main set of web services.
 */
@Named
@Singleton
public class ClimateServiceController extends Controller {
    private final int initialcount = 0;
    private final ClimateServiceRepository climateServiceRepository;
    private final UserRepository userRepository;
    private final ServiceEntryRepository serviceEntryRepository;
    private final VersionRepository versionRepository;
    private final RatingRepository ratingRepository;

    // We are using constructor injection to receive a repository to support our
    // desire for immutability.
    @Inject
    public ClimateServiceController(final ClimateServiceRepository climateServiceRepository,
				    UserRepository userRepository,ServiceEntryRepository serviceEntryRepository,
				    VersionRepository versionRepository, RatingRepository ratingRepository) {
	this.climateServiceRepository = climateServiceRepository;
	this.userRepository = userRepository;
        this.serviceEntryRepository = serviceEntryRepository;
	this.versionRepository = versionRepository;
	this.ratingRepository = ratingRepository;
    }

    public Result addClimateService() {
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out
		.println("Climate service not saved, expecting Json data");
	    return badRequest("Climate service not saved, expecting Json data");
	}

	// Parse JSON file
	long rootServiceId = json.findPath("rootServiceId").asLong();
	String creatorEmail = json.findPath("creatorEmail").asText();
	String name = json.findPath("name").asText();
	String purpose = json.findPath("purpose").asText();
	String url = json.findPath("url").asText();
	String scenario = json.findPath("scenario").asText();
	Date createTime = new Date();
	SimpleDateFormat format = new SimpleDateFormat(Common.DATE_PATTERN);
	try {
	    createTime = format.parse(json.findPath("createTime").asText());
	} catch (ParseException e) {
	    System.out
		.println("No creation date specified, set to current time");
	}
	String versionNo = json.findPath("versionNo").asText();

	try {
	    User user = userRepository.findByEmail(creatorEmail);
	    ClimateService climateService = new ClimateService(rootServiceId,
							       user, name, purpose, url, scenario, createTime, versionNo);
	    ClimateService savedClimateService = climateServiceRepository
		.save(climateService);
	    String registerNote = "ClimateService Name: " + savedClimateService.getName() + ", VersionNo: "+versionNo;
	    ServiceEntry serviceEntry = new ServiceEntry(createTime, versionNo, user, createTime, registerNote, initialcount, savedClimateService);
	    serviceEntryRepository.save(serviceEntry);
	    System.out.println("Climate Service saved: "
			       + savedClimateService.getName());
	    return created(new Gson().toJson(savedClimateService.getId()));
	} catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Climate Service not saved: " + name);
	    return badRequest("Climate Service not saved: " + name);
	}
    }
	
    public Result savePage() {
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out.println("Climate service not saved, expecting Json data");
	    return badRequest("Climate service not saved, expecting Json data");
	}

	// Parse JSON file
	String temp = json.findPath("pageString").asText();

	// Remove delete button from preview page
	String result = temp.replaceAll("<td><button type=\\\\\"button\\\\\" class=\\\\\"btn btn-danger\\\\\" onclick=\\\\\"Javascript:deleteRow\\(this\\)\\\\\">delete</button></td>", "");	
		
	result = StringEscapeUtils.unescapeJava(result);
	result = result.substring(1, result.length() - 1);
	System.out.println(result);
		
	String str1 = Constants.htmlHead;
	String str2 = Constants.htmlTail;
		
	result = str1 + result + str2;
		
	String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
	    .format(new Date());
	String location = "climateServicePageRepository/" + timeStamp + ".txt";

	File theDir = new File("climateServicePageRepository");

	// if the directory does not exist, create it
	if (!theDir.exists()) {
	    System.out.println("creating directory: climateServicePageRepository");
	    boolean create = false;

	    try {
		theDir.mkdir();
		create = true;
	    } catch (SecurityException se) {
		// handle it
	    }
	    if (create) {
		System.out.println("DIR created");
	    }
	} else {
	    System.out.println("No");
	}

	try {
	    File file = new File(location);
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
	    output.write(result);
	    output.close();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return ok(result);
    }

    public Result deleteClimateServiceById(long id) {
	ClimateService climateService = climateServiceRepository.findOne(id);
	if (climateService == null) {
	    System.out.println("Climate service not found with id: " + id);
	    return notFound("Climate service not found with id: " + id);
	}

	climateServiceRepository.delete(climateService);
	System.out.println("Climate service is deleted: " + id);
	return ok("Climate service is deleted: " + id);
    }
	
    public Result deleteClimateServiceByName(String name) {
	ClimateService climateService = climateServiceRepository.findFirstByName(name);
	if (climateService == null) {
	    System.out.println("Climate service not found with name: " + name);
	    return notFound("Climate service not found with name: " + name);
	}

	climateServiceRepository.delete(climateService);
	System.out.println("Climate service is deleted: " + name);
	return ok("Climate service is deleted: " + name);
    }

    public Result updateClimateServiceById(long id) {
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out
		.println("Climate service not saved, expecting Json data");
	    return badRequest("Climate service not saved, expecting Json data");
	}

	// Parse JSON file
	long rootServiceId = json.findPath("rootServiceId").asLong();
	long creatorId = json.findPath("creatorId").asLong();
	String name = json.findPath("name").asText();
	String purpose = json.findPath("purpose").asText();
	String url = json.findPath("url").asText();
	String scenario = json.findPath("scenario").asText();
	String versionNo = json.findPath("versionNo").asText();
	// Creation time should be immutable and not updated.

	try {
	    ClimateService climateService = climateServiceRepository
		.findOne(id);
	    User user = userRepository.findOne(creatorId);
	    ServiceEntry serviceEntry = null;
	    if (versionNo.equals(climateService.getVersionNo())) {
		List<ServiceEntry> serviceEntries = serviceEntryRepository.findByClimateServiceAndVersionNo(climateService, versionNo);
		if (serviceEntries.size()==0) {
		    String registerNote = "ClimateService Name: " + climateService.getName() + ", VersionNo: "+versionNo;
		    serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
		}
		else {
		    serviceEntry = serviceEntries.get(0);
		}
	    }
	    else {
		String registerNote = "ClimateService Name:" + climateService.getName() + ", VersionNo: "+versionNo;
		serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
	    }
	    climateService.setUser(user);
	    climateService.setName(name);
	    climateService.setPurpose(purpose);
	    climateService.setRootServiceId(rootServiceId);
	    climateService.setScenario(scenario);
	    climateService.setUrl(url);
	    climateService.setVersionNo(versionNo);
	    ClimateService savedClimateService = climateServiceRepository
		.save(climateService);
	    serviceEntry.setClimateService(savedClimateService);
	    ServiceEntry savedServiceEntry = serviceEntryRepository.save(serviceEntry);
			
	    System.out.println("Climate Service updated: "
			       + savedClimateService.getName());
	    return created("Climate Service updated: "
			   + savedClimateService.getName());
	} catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Climate Service not updated: " + name);
	    return badRequest("Climate Service not updated: " + name);
	}
    }

    public Result updateClimateServiceByName(String oldName) {
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out
		.println("Climate service not saved, expecting Json data");
	    return badRequest("Climate service not saved, expecting Json data");
	}
	System.out.println(json);
	// Parse JSON file
	long rootServiceId = json.findPath("rootServiceId").asLong();
	long creatorId = json.findPath("creatorId").asLong();
	String name = json.findPath("name").asText();
	String purpose = json.findPath("purpose").asText();
	String url = json.findPath("url").asText();
	String scenario = json.findPath("scenario").asText();
	String versionNo = json.findPath("versionNo").asText();
	// Creation time is immutable and should not be updated

	if (oldName == null || oldName.length() == 0) {
	    System.out.println("Old climate Service Name is null or empty!");
	    return badRequest("Old climate Service Name is null or empty!");
	}

	try {
	    ClimateService climateService = climateServiceRepository
		.findFirstByName(oldName);
	    User user = userRepository.findOne(creatorId);
	    ServiceEntry serviceEntry = null;
	    if (versionNo.equals(climateService.getVersionNo())) {
		List<ServiceEntry> serviceEntries = serviceEntryRepository.findByClimateServiceAndVersionNo(climateService, versionNo);
		if (serviceEntries.size()==0) {
		    String registerNote = "ClimateService Name: " + climateService.getName() + ", VersionNo: "+versionNo;
		    serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
		}
		else {
		    serviceEntry = serviceEntries.get(0);
		}
	    }
	    else {
		String registerNote = "ClimateService Name: " + climateService.getName() + ", VersionNo: "+versionNo;
		serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
	    }
	    climateService.setName(name);
	    climateService.setPurpose(purpose);
	    climateService.setRootServiceId(rootServiceId);
	    climateService.setScenario(scenario);
	    climateService.setUrl(url);
			
	    climateService.setUser(user);
	    climateService.setVersionNo(versionNo);

	    ClimateService savedClimateService = climateServiceRepository
		.save(climateService);
	    serviceEntry.setClimateService(savedClimateService);
	    ServiceEntry savedServiceEntry = serviceEntryRepository.save(serviceEntry);
	    System.out.println("Climate Service updated: "
			       + savedClimateService.getName());
	    return created("Climate Service updated: "
			   + savedClimateService.getName());
	} catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Climate Service not updated: " + name);
	    return badRequest("Climate Service not updated: " + name);
	}
    }

    public Result getClimateService(String name, String format) {
	if (name == null || name.length() == 0) {
	    System.out.println("Climate Service Name is null or empty!");
	    return badRequest("Climate Service Name is null or empty!");
	}

	List<ClimateService> climateService = climateServiceRepository
	    .findAllByName(name);
	if (climateService == null) {
	    System.out.println("Climate service not found with name: " + name);
	    return notFound("Climate service not found with name: " + name);
	}

	String result = new String();
	if (format.equals("json")) {
	    result = new Gson().toJson(climateService);
	}

	return ok(result);
    }

    public Result getClimateServiceById(long id) {
	ClimateService climateService = climateServiceRepository.findOne(id);
	if (climateService == null) {
	    System.out.println("Climate service not found with id: " + id);
	    return notFound("Climate service not found with id: " + id);
	}

	String result = new Gson().toJson(climateService);

	return ok(result);
    }

    public Result getAllClimateServices(String format) {
	Iterable<ClimateService> climateServices = climateServiceRepository
	    .findAll();
	if (climateServices == null) {
	    System.out.println("No climate service found");
	}

	String result = new String();
	if (format.equals("json")) {
	    result = new Gson().toJson(climateServices);
	}

	return ok(result);

    }

    public Result getAllClimateServicesWithRatings() {
	Iterable<Version> versions = versionRepository.getAllVersions();
	ObjectNode result = Json.newObject();
	ArrayNode servicesArray = JsonNodeFactory.instance.arrayNode();

	for (Version version : versions){
	    ObjectNode oneResult = Json.newObject();
	    ClimateService climateService = climateServiceRepository.findServiceById(version.getServiceId());
	    Double rating = ratingRepository.getAverageRate(version.getServiceId(), version.getVersionId());

	    oneResult.put("name", climateService.getName());
	    oneResult.put("purpose", climateService.getPurpose());
	    oneResult.put("url", version.getUrl());
	    oneResult.put("version", version.getVersionId());
	    oneResult.put("rating", rating);

	    servicesArray.add(oneResult);
	}

	result.put("services", servicesArray);

	return ok(result);
    }

    public Result getAllClimateServicesOrderByCreateTime(String format){
        Iterable<ClimateService> climateServices = climateServiceRepository
	    .findByOrderByCreateTimeDesc();
	if (climateServices == null) {
	    System.out.println("No climate service found");
	}

	String result = new String();
	if (format.equals("json")) {
	    result = new Gson().toJson(climateServices);
	}

	return ok(result);
    }

    public Result getTop3GradesServices(){
	Iterable<Rating> ratings = ratingRepository.getAllOrderByAverageRate();
	ArrayList<ClimateService> climateServices = new ArrayList<ClimateService>();

	for (Rating rating : ratings){
	    ClimateService climateService = climateServiceRepository.findServiceById(rating.getServiceId());
	    climateService.setUrl(versionRepository.getUrlByServiceAndVersion(rating.getServiceId(), rating.getVersionId()));
	    climateService.setVersionNo(Long.toString(rating.getVersionId()));
	    climateServices.add(climateService);
	}

	return ok(new Gson().toJson(climateServices));
    }

    public Result getTop3MostRecentUsedServices(){
	List<Version> versions = versionRepository.getTop3MostRecentUsed();
	ArrayList<ClimateService> climateServices = new ArrayList<ClimateService>();

	for (Version version : versions){
	    ClimateService climateService = climateServiceRepository.findServiceById(version.getServiceId());
	    climateService.setUrl(version.getUrl());
	    climateService.setVersionNo(Long.toString(version.getVersionId()));
	    climateServices.add(climateService);
	}

	return ok(new Gson().toJson(climateServices));
    }

    public Result updateAccessTimestamp(){
	ObjectNode response = Json.newObject();
	JsonNode json = request().body().asJson();

	try{
	    Version version = versionRepository.getOneByUrlAndVersion(json.findPath("url").asText(), json.findPath("version").asLong());
	    version.setLatestAccessTimeStamp(new Date());
	    versionRepository.save(version);
	    response.put("success", "success");
	}
	catch (PersistenceException pe) {
	    pe.printStackTrace();
	    response.put("success", "fail");
	}
	
	return ok(response);
    }

    public Result getAllClimateServicesOrderByCount(String format){
	Iterable<ClimateService> climateServices = climateServiceRepository.getClimateServiceOrderByCount();
	if (climateServices == null) {
	    System.out.println("No climate service found");
	}

	String result = new String();
	if (format.equals("json")) {
	    result = new Gson().toJson(climateServices);
	}

	return ok(result);
    }

    public Result addServiceEntry() {
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out
		.println("Climate service not saved, expecting Json data");
	    return badRequest("Climate service not saved, expecting Json data");
	}

	// Parse JSON file
	String versionNo = json.findPath("versionNo").asText();
	String registerNote = json.findPath("registerNote").asText();
	int count = json.findPath("count").asInt();
	long serviceId = json.findPath("serviceId").asLong();
	long creatorId = json.findPath("creatorId").asLong();

	Date registerTime = new Date();
	SimpleDateFormat format = new SimpleDateFormat(Common.DATE_PATTERN);
	try {
	    registerTime = format.parse(json.findPath("registerTimeStamp").asText());
	} catch (ParseException e) {
	    System.out
		.println("No creation date specified, set to current time");
	}

	Date latestAccessTime = new Date();
	try {
	    latestAccessTime = format.parse(json.findPath("latestAccessTimeStamp").asText());
	} catch (ParseException e) {
	    System.out
		.println("No creation date specified, set to current time");
	}

	try {
	    ClimateService climateService = climateServiceRepository.findOne(serviceId);
	    User creator = userRepository.findOne(creatorId);
	    ServiceEntry entry = new ServiceEntry();
	    entry.setClimateService(climateService);
	    entry.setCount(count);
	    entry.setRegisterNote(registerNote);
	    entry.setVersionNo(versionNo);
	    entry.setUser(creator);
	    entry.setRegisterTimeStamp(registerTime);
	    entry.setLatestAccessTimestamp(latestAccessTime);

	    ServiceEntry savedServiceEntry = serviceEntryRepository.save(entry);

	    System.out.println("Service Entry saved: "
			       + savedServiceEntry.getId());
	    return created(new Gson().toJson(savedServiceEntry));
	} catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Service Entry not saved: " + serviceId);
	    return badRequest("Service Entry not saved: " + serviceId);
	}
    }

    public Result getAllServiceEntries(String format) {
	Iterable<ServiceEntry> serviceEntries = serviceEntryRepository
	    .findAll();
	if (serviceEntries == null) {
	    System.out.println("No service entry found");
	}

	String result = new String();
	if (format.equals("json")) {
	    result = new Gson().toJson(serviceEntries);
	}

	return ok(result);

    }

    private List<ClimateService> removeDuplicateServices(List<ClimateService> in) {
	List<ClimateService> out = new ArrayList<ClimateService>();
	Collections.sort(in, new CompareServiceById());
	ClimateService prev = null;

	for (ClimateService cur: in) {
	    if (prev == null || prev.getId() != cur.getId()) {
		out.add(cur);
	    }
	    prev = cur;
	}
	return out;
    }
    
    public Result queryClimateService(String keywords) {
	String [] words = keywords.split("\\s+");
	List<ClimateService> services = new ArrayList<ClimateService>();
	for (String word : words){
	    services.addAll(climateServiceRepository.findServiceWithName("%" + word + "%"));
	    services.addAll(climateServiceRepository.findServiceWithPurpose("%" + word + "%"));
	}
	services = removeDuplicateServices(services);
	String result = new Gson().toJson(services);
	return ok(result);
    }

    public Result getAllVersions(Long serviceId){
	ObjectNode response = Json.newObject();
	ArrayNode versionArray = JsonNodeFactory.instance.arrayNode();

	try{
	    List<Version> versions = versionRepository.getAllVersionsById(serviceId);

	    for (Version version : versions){
		ArrayNode versionNode = JsonNodeFactory.instance.arrayNode();
		versionNode.add(version.getVersionId());
		versionNode.add(version.getUrl());
		versionArray.add(versionNode);
	    }
	}
	catch (PersistenceException pe) {
	    pe.printStackTrace();
	}
	
	response.put("versions", versionArray);
	return ok(response.toString());
    }
}
