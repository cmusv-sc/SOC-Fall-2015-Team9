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

package models.metadata;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import util.APICall;
import util.Constants;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ClimateService {
    private String id;
    private String climateServiceName;
    private String purpose;
    private String url;
    private String scenario;
    private String version;
    private String rootservice;
    private String photo;
    private String rating;

    public String getScenario() {
	return scenario;
    }
    public void setScenario(String scenario) {
	this.scenario = scenario;
    }

    public String getVersion() {
	return version;
    }
    public void setVersion(String version) {
	this.version = version;
    }

    public String getRootservice() {
	return rootservice;
    }
    public void setRootservice(String rootservice) {
	this.rootservice = rootservice;
    }

    public String getRating() {
	return rating;
    }
    public void setRating(String rating) {
	this.rating = rating;
    }

    private static final String GET_CLIMATE_SERVICES_CALL = Constants.NEW_BACKEND+"climateService/getAllClimateServices/json";

    private static final String GET_MOST_RECENTLY_ADDED_CLIMATE_SERVICES_CALL = Constants.NEW_BACKEND+"climateService/getAllMostRecentClimateServicesByCreateTime/json";

    private static final String GET_MOST_RECENTLY_USED_CLIMATE_SERVICES_CALL = Constants.NEW_BACKEND+"climateService/getTop3MostRecentUsedServices";

    private static final String GET_TOP_3_GRADES_CLIMATE_SERVICES_CALL = Constants.NEW_BACKEND+"climateService/getTop3HighestGradesServices";

    private static final String GET_ALL_RATINGS = Constants.NEW_BACKEND + "climateService/getAllClimateServicesWithRatings";

    private static final String UPDATE_ACCESS_TIMESTAMP = Constants.NEW_BACKEND + "climateService/updateAccessTimestamp";

    private static final String GET_MOST_POPULAR_CLIMATE_SERVICES_CALL = Constants.NEW_BACKEND+"climateService/getAllMostUsedClimateServices/json";

    private static final String ADD_CLIMATE_SERVICE_CALL = Constants.NEW_BACKEND+"climateService/addClimateService";

    private static final String DELETE_CLIMATE_SERVICE_CALL = Constants.NEW_BACKEND + util.Constants.NEW_DELETE_CLIMATE_SERVICE;

    private static final String EDIT_CLIMATE_SERVICE_CALL = Constants.NEW_BACKEND+ "climateService/" + util.Constants.NEW_EDIT_CLIMATE_SERVICE + "/name/";

    private static final String CLIMATE_SERVICE_QUERY = Constants.NEW_BACKEND + "climateService/queryClimateService/";

    public ClimateService() {
	// TODO Auto-generated constructor stub
    }

    public String getId() {
	return id;
    }

    public String getClimateServiceName() {
	return climateServiceName;
    }

    public String getPurpose() {
	return purpose;
    }

    public String getUrl() {
	return url;
    }

    public void setId(String id) {
	this.id = id;
    }

    public void setClimateServiceName(String climateServiceName) {
	this.climateServiceName = climateServiceName;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
    }

    public void setUrl(String url) {
	this.url = url;
	setPhoto();
    }

    public static ClimateService find(String id) {
	ClimateService climateService = new ClimateService();
	climateService.setId(id);
	return climateService;
    }

    /**
     * find a climateService by its name
     *
     * @param climateServiceName
     * @return the founded result. If not found, return null
     */
    public static ClimateService findServiceByName(String climateServiceName){
	List<ClimateService> allList = all();
	for (ClimateService element : allList) {
	    String elementUri = element.getClimateServiceName();
	    if (elementUri.equals(climateServiceName))
		return element;
	}
	return null;
    }

    /**
     * find a climateService by its url
     *
     * @param url
     * @return the founded result. If not found, return null
     */
    public static ClimateService findServiceByUrl(String url){
	List<ClimateService> allList = all();
	for (ClimateService element : allList) {
	    if (element.getUrl().equals(url))
		return element;
	}
	return null;
    }

    /**
     * Generate the list of all sensor categories
     *
     * @return a list of all the sensor categories
     */
    public static List<ClimateService> all() {

	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall
	    .callAPI(GET_CLIMATE_SERVICES_CALL);

	if (climateServicesNode == null || climateServicesNode.has("error")
	    || !climateServicesNode.isArray()) {
	    return climateServices;
	}

	for (int i = 0; i < climateServicesNode.size(); i++) {
	    JsonNode json = climateServicesNode.path(i);
	    ClimateService newService = new ClimateService();
	    newService.setId(json.path("id").asText());
	    newService.setClimateServiceName(json.get(
						      "name").asText());
	    newService.setPurpose(json.path("purpose").asText());
	    newService.setUrl(json.path("url").asText());
	    //newService.setCreateTime(json.path("createTime").asText());
	    newService.setScenario(json.path("scenario").asText());
	    newService.setVersion(json.path("versionNo").asText());
	    newService.setRootservice(json.path("rootServiceId").asText());
	    climateServices.add(newService);
	}
	return climateServices;
    }

    public static List<ClimateService> queryClimateService(String keywords) throws UnsupportedEncodingException{
	List<ClimateService> climateservice = new ArrayList<ClimateService>();
	String requestPara = new String();

	if (keywords != null){
	    requestPara = keywords;
	}

	JsonNode climateServiceNode = APICall.callAPI(CLIMATE_SERVICE_QUERY + URLEncoder.encode(requestPara, "UTF-8").replace("+", "%20"));

	if (climateServiceNode == null || climateServiceNode.has("error") || !climateServiceNode.isArray()) {
	    return climateservice;
	}

	for (int i = 0; i < climateServiceNode.size() && i < 3; i++) {
	    JsonNode json = climateServiceNode.path(i);
	    ClimateService newClimateService = deserializeJsonToClimateService(json);
	    climateservice.add(newClimateService);
	}

	return climateservice;
    }

    private static ClimateService deserializeJsonToClimateService(JsonNode json) {
	ClimateService newClimateService = new ClimateService();
	newClimateService.setId(json.get("id").asText());
	newClimateService.setClimateServiceName(json.get("name").asText());
	newClimateService.setPurpose(json.get("purpose").asText());
	newClimateService.setUrl(json.get("url").asText());
	newClimateService.setScenario(json.get("scenario").asText());
	newClimateService.setVersion(json.get("versionNo").asText());
	newClimateService.setRootservice(json.get("rootServiceId").asText());

	return newClimateService;
    }

    public static List<ClimateService> getMostRecentlyAdded() {
	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall
	    .callAPI(GET_MOST_RECENTLY_ADDED_CLIMATE_SERVICES_CALL);
	if (climateServicesNode == null || climateServicesNode.has("error")
	    || !climateServicesNode.isArray()) {
	    return climateServices;
	}

	for (int i = 0; i < climateServicesNode.size(); i++) {
	    JsonNode json = climateServicesNode.path(i);
	    ClimateService newService = new ClimateService();
	    newService.setId(json.get("id").asText());
	    newService.setClimateServiceName(json.get(
						      "name").asText());
	    newService.setPurpose(json.findPath("purpose").asText());
	    newService.setUrl(json.findPath("url").asText());
	    newService.setScenario(json.findPath("scenario").asText());
	    newService.setVersion(json.findPath("versionNo").asText());
	    newService.setRootservice(json.findPath("rootServiceId").asText());
	    climateServices.add(newService);
	}
	return climateServices;
    }

    public static List<ClimateService> getMostRecentlyUsed() {

	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall
	    .callAPI(GET_MOST_RECENTLY_USED_CLIMATE_SERVICES_CALL);

	if (climateServicesNode == null || climateServicesNode.has("error")
	    || !climateServicesNode.isArray()) {
	    return climateServices;
	}

	for (int i = 0; i < climateServicesNode.size(); i++) {
	    JsonNode json = climateServicesNode.path(i);
	    ClimateService newService = new ClimateService();
	    newService.setId(json.get("id").asText());
	    newService.setClimateServiceName(json.get(
						      "name").asText());
	    newService.setPurpose(json.findPath("purpose").asText());
	    newService.setUrl(json.findPath("url").asText());
	    newService.setScenario(json.findPath("scenario").asText());
	    newService.setVersion(json.findPath("versionNo").asText());
	    newService.setRootservice(json.findPath("rootServiceId").asText());
	    climateServices.add(newService);
	}
	return climateServices;
    }


    public static List<ClimateService> getMostRecentlyUsed3(){
	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall
	    .callAPI(GET_MOST_RECENTLY_USED_CLIMATE_SERVICES_CALL);

	if (climateServicesNode == null || climateServicesNode.has("error")
	    || !climateServicesNode.isArray()) {
	    return climateServices;
	}

	for (int i = 0; i < climateServicesNode.size(); i++) {
	    JsonNode json = climateServicesNode.path(i);
	    ClimateService newService = new ClimateService();
	    newService.setId(json.get("id").asText());
	    newService.setClimateServiceName(json.get("name").asText());
	    newService.setPurpose(json.findPath("purpose").asText());
	    newService.setUrl(json.findPath("url").asText());
	    newService.setScenario(json.findPath("scenario").asText());
	    newService.setVersion(json.findPath("versionNo").asText());
	    newService.setRootservice(json.findPath("rootServiceId").asText());
	    climateServices.add(newService);
	}
	return climateServices;
    }

    public static List<ClimateService> getTopGrades3(){
	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall
	    .callAPI(GET_TOP_3_GRADES_CLIMATE_SERVICES_CALL);

	if (climateServicesNode == null || climateServicesNode.has("error")
	    || !climateServicesNode.isArray()) {
	    return climateServices;
	}

	for (int i = 0; i < climateServicesNode.size(); i++) {
	    JsonNode json = climateServicesNode.path(i);
	    ClimateService newService = new ClimateService();
	    newService.setId(json.get("id").asText());
	    newService.setClimateServiceName(json.get("name").asText());
	    newService.setPurpose(json.findPath("purpose").asText());
	    newService.setUrl(json.findPath("url").asText());
	    newService.setScenario(json.findPath("scenario").asText());
	    newService.setVersion(json.findPath("versionNo").asText());
	    newService.setRootservice(json.findPath("rootServiceId").asText());
	    climateServices.add(newService);
	}
	return climateServices;
    }

    public static List<ClimateService> getAllRatings(){
	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall.callAPI(GET_ALL_RATINGS);

	if (climateServicesNode == null || climateServicesNode.has("error")){
	    return climateServices;
	}

	ArrayNode versions = (ArrayNode)climateServicesNode.get("services");
	for (JsonNode version: versions) {
	    ClimateService cs = new ClimateService();
	    cs.setVersion(version.get("version").asText());
	    cs.setUrl(version.get("url").asText());
	    cs.setClimateServiceName(version.get("name").asText());
	    cs.setPurpose(version.get("purpose").asText());
	    Double rating = version.get("rating").asDouble();
	    if (rating == 0){
		cs.setRating("N/A");
	    }
	    else{
		cs.setRating(String.valueOf(rating));
	    }

	    climateServices.add(cs);
	}

	return climateServices;
    }

    public static void updateAccessTimestamp(String url, Long version){
	ObjectNode jsonData = Json.newObject();

	jsonData.put("url", url);
	jsonData.put("version", version);
	
	APICall.postAPI(UPDATE_ACCESS_TIMESTAMP, jsonData);
    }

    public static List<ClimateService> getMostPopular() {
	List<ClimateService> climateServices = new ArrayList<ClimateService>();

	JsonNode climateServicesNode = APICall
	    .callAPI(GET_MOST_POPULAR_CLIMATE_SERVICES_CALL);

	if (climateServicesNode == null || climateServicesNode.has("error")
	    || !climateServicesNode.isArray()) {
	    return climateServices;
	}

	for (int i = 0; i < climateServicesNode.size(); i++) {
	    JsonNode json = climateServicesNode.path(i);
	    ClimateService newService = new ClimateService();
	    newService.setClimateServiceName(json.get("name").asText());
	    newService.setPurpose(json.findPath("purpose").asText());
	    newService.setUrl(json.findPath("url").asText());
	    newService.setScenario(json.findPath("scenario").asText());
	    newService.setVersion(json.findPath("versionNo").asText());
	    newService.setRootservice(json.findPath("rootServiceId").asText());
	    climateServices.add(newService);
	}
	return climateServices;
    }

    /**
     * Create a new climate service
     *
     * @param jsonData
     * @return the response from the API server
     */
    public static JsonNode create(JsonNode jsonData) {
	return APICall.postAPI(ADD_CLIMATE_SERVICE_CALL, jsonData);
    }

    /**
     * Edit a climate service
     *
     * @param jsonData
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JsonNode edit(String climateServiceName, JsonNode jsonData) throws UnsupportedEncodingException {
	return APICall.putAPI(EDIT_CLIMATE_SERVICE_CALL + URLEncoder.encode(climateServiceName, "UTF-8"), jsonData);
    }

    /**
     * Delete a sensor category
     *
     * @param climateServiceId
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JsonNode delete(String climateServiceId) throws UnsupportedEncodingException {
	return APICall.deleteAPI(DELETE_CLIMATE_SERVICE_CALL
				 + URLEncoder.encode(climateServiceId, "UTF-8"));
    }

    /**
     * Generate a list of climate service names
     *
     * @return a list of climate service names
     */
    public static List<String> allClimateServiceName() {
	List<ClimateService> allList = all();
	List<String> resultList = new ArrayList<String>();
	for (ClimateService element : allList) {
	    String elementName = element.getClimateServiceName();
	    if (elementName != null)
		resultList.add(elementName);
	}
	return resultList;
    }

    public void setPhoto(){
	if(url.contains("threeDimVarVertical.html")){
	    photo = "/assets/images/3DVerticalProfile.jpeg";
	}else if(url.contains("twoDimZonalMean.html")){
	    photo = "http://einstein.sv.cmu.edu:9002/static/twoDimZonalMean/65778f88e3e057738423aa7183f5ee54/nasa_modis_clt_200401_200412_Annual.jpeg";
	}else if(url.contains("twoDimMap.html")){
	    photo = "http://einstein.sv.cmu.edu:9002/static/twoDimMap/6879a2eedd1910f4c45e6213d342e066/nasa_modis_clt_200401_200412_Annual.jpeg";
	}else if(url.contains("twoDimSlice3D.html")){
	    photo = "http://einstein.sv.cmu.edu:9002/static/twoDimSlice3D/ba6b08d54048d9c8349185d2606d3638/nasa_airs_ta_200401_200412_Annual.jpeg";
	}else if(url.contains("scatterPlot2Vars.html")){
	    photo = "/assets/images/ScatterPlot.png";
	}else if(url.contains("conditionalSampling.html")){
	    photo = "/assets/images/ConditionalSampling1Variable.jpeg";
	}else if(url.contains("twoDimTimeSeries.html")){
	    photo = "/assets/images/TimeSeriesPlot.jpeg";
	}else if(url.contains("threeDimZonalMean.html")){
	    photo = "http://einstein.sv.cmu.edu:9002/static/threeDimZonalMean/e4e120045d2bb589eed371e1ca08fd99/nasa_airs_ta_200401_200412_Annual.jpeg";
	}else if(url.contains("diffPlot2Vars.html")){
	    photo = "/assets/images/DifferencePlot.png";
	}else if (url.contains("regridAndDownload.html")) {
	    photo = "/assets/images/regrid.jpg";
	}else if (url.contains("correlationMap.html")) {
	    photo = "/assets/images/correlationMap.png";
	}else if (url.contains("conditionalSampling2Var.html")) {
	    photo = "/assets/images/conditionalSampling2Variables.jpeg";
	}
	else{
	    photo = "http://upload.wikimedia.org/wikipedia/commons/3/33/White_square_with_question_mark.png";
	}
    }
    public String getPhoto() {
	return photo;
    }

}
