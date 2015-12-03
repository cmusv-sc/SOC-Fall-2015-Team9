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

        // result
        response.put("individual_rate", ratingRepository.getIndividualRating(userRepository.getUserIdByEmail(email), serviceId, versionId).getRate());
        response.put("average_rate", ratingRepository.getAverageRate(serviceId, versionId));

        // response;
        System.out.println(response);

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
                response.put("rating_id", ratingEntry.getRatingId());
                response.put("user_id", ratingEntry.getUserId());
                response.put("service_id", ratingEntry.getServiceId());
                response.put("version_id", ratingEntry.getVersionId());
                response.put("rate", ratingEntry.getRate());
                response.put("average_rate", ratingRepository.getAverageRate(serviceId, versionId));
                response.put("is_logged_in", true);
                response.put("is_edit_allowed", true);
                response.put("is_add_allowed", true);
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

        System.out.println(response);

        return ok(response);
    }

}
