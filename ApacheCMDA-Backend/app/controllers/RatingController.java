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
public class RatingController extends Controller{
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Inject
    public RatingController(final RatingRepository ratingRepository,
                            final UserRepository userRepository){
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    private String failJson(String msg){
        ObjectNode response = Json.newObject();
        response.put("success", false);
        response.put("message", msg);

        return response.toString();
    }


    public Result getRating(Long serviceId, Long versionId, String email, String format){
        System.out.println("GET RATiNG");
        ObjectNode response = Json.newObject();
	Rating individualRecord =  ratingRepository.getIndividualRating(userRepository.getUserIdByEmail(email), serviceId, versionId);
	Double averageRate = ratingRepository.getAverageRate(serviceId, versionId);

	// result
	response.put("success", true);
	if (individualRecord != null){
	    response.put("individual_rate", individualRecord.getRate());
	}
	else{
	    response.put("individual_rate", 0);
	}

	if (averageRate != null){
	    response.put("average_rate", averageRate);
	}
	else{
	    response.put("average_rate", 0.0);
	}

	return ok(response);
    }

    public Result postRating(){
	System.out.println("POST RATING");

	ObjectNode response = Json.newObject();
	JsonNode json = request().body().asJson();
	if (json == null) {
	    System.out.println("Rating not saved, expecting Json data");
	    return badRequest("Rating not saved, expecting Json data");
	}

	try{
	    String email = json.findPath("email").asText();

	    long userId = userRepository.getUserIdByEmail(email);
	    long serviceId = json.findPath("service_id").asLong();
	    long versionId = json.findPath("version_id").asLong();
	    int rate = json.findPath("rate").asInt();
	    Rating record = ratingRepository.getIndividualRating(userId, serviceId, versionId);

	    if(record == null){
		Rating rating = new Rating(userId, serviceId, versionId, rate);
		Rating ratingEntry = ratingRepository.save(rating);

		response.put("success", true);
		response.put("rate", ratingEntry.getRate());
		response.put("average_rate", ratingRepository.getAverageRate(serviceId, versionId));
	    }else{
		Rating rating = record;
		rating.setRate(rate);
		Rating ratingEntry = ratingRepository.save(rating);

		response.put("success", true);
		response.put("rate", ratingEntry.getRate());
		response.put("average_rate", ratingRepository.getAverageRate(serviceId, versionId));
	    }
	}
	catch (PersistenceException pe) {
	    pe.printStackTrace();
	    System.out.println("Rating not saved");
	    return badRequest(failJson("Rating not saved"));
	}

	return ok(response);
    }

}
