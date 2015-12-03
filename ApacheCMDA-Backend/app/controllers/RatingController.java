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
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    private ArrayNode getRatingArray(Long userId, Long serviceId){
        List<Rating> ratings = ratingRepository.findAllByClimateServiceId(userId, serviceId);
        ArrayNode ratingArray = JsonNodeFactory.instance.arrayNode();

        for (Rating rating : ratings){
            ObjectNode oneRating = JsonNodeFactory.instance.objectNode();
            oneRating.put("rating_id", rating.getRatingId());
            oneRating.put("user_id", userId);
            oneRating.put("service_id", serviceId);
            oneRating.put("fullname", rating.getFullName());
            oneRating.put("posted_date", timeFormat.format(rating.getPostedDate()));
            oneRating.put("rate", rating.getRate());
            ratingArray.add(oneRating);
        }

        return ratingArray;
    }

    private void deleteRatingById(Long userId, Long serviceId){
        List<Rating> ratings = ratingRepository.findAllByClimateServiceId(userId, serviceId);

        for (Rating rating : ratings){
            ratingRepository.delete(rating);
        }
    }

    public Result getRating(Long serviceId, String email, String format){
        System.out.println("GET RATiNG");
        ObjectNode response = Json.newObject();
        ObjectNode result = Json.newObject();
        ObjectNode user = Json.newObject();
        JsonNode json = request().body().asJson();

        // User node
        if (userRepository.getUserIdByEmail(email) == null){
            user.put("user_id", -1);
            user.put("fullname", "Visitor");
            user.put("is_logged_in", false);
            user.put("is_add_allowed", false);
            user.put("is_edit_allowed", false);
        }
        else{
            user.put("user_id", userRepository.getUserIdByEmail(email));
            user.put("fullname", userRepository.getUsernameByEmail(email));
            user.put("is_logged_in", true);
            user.put("is_add_allowed", true);
            user.put("is_edit_allowed", true);
        }
        user.put("picture", "/assets/images/user_blank_picture.png");

        // result
        result.put("ratings", getRatingArray(userRepository.getUserIdByEmail(email), serviceId));
        result.put("total_rating", ratingRepository.countRatings(serviceId));
        result.put("user", user);

        // response
        response.put("results", result);

        return ok(response.toString());
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
            long userId = json.findPath("user_id").asLong();
            long serviceId = json.findPath("service_id").asLong();

            String email = json.findPath("email").asText();
            String fullname = userRepository.getUsernameByEmail(email);

            Date postedDate = timeFormat.parse(json.findPath("posted_date").asText());
            int rate = json.findPath("rate").asInt();

            Rating rating = new Rating(userId, serviceId, fullname, postedDate, rate);
            Rating ratingEntry = ratingRepository.save(rating);

            response.put("success", true);
            response.put("rating_id", ratingEntry.getRatingId());
            response.put("user_id", ratingEntry.getUserId());
            response.put("service_id", ratingEntry.getServiceId());
            response.put("fullname", ratingEntry.getFullName());
            response.put("posted_date", timeFormat.format(ratingEntry.getPostedDate()));
            response.put("rate", ratingEntry.getRate());
            response.put("is_logged_in", true);
            response.put("is_edit_allowed", true);
            response.put("is_add_allowed", true);
        }
        catch (ParseException pe){
            pe.printStackTrace();
            System.out.println("Invalid date format");
            return badRequest(failJson("Invalid date format"));
        }
        catch (PersistenceException pe) {
            pe.printStackTrace();
            System.out.println("Rating not saved");
            return badRequest(failJson("Rating not saved"));
        }

        return ok(response);
    }

    public Result editRating(){
        System.out.println("EDIT Rating");

        ObjectNode response = Json.newObject();
        JsonNode json = request().body().asJson();
        if (json == null) {
            System.out.println("Rating not updated, expecting Json data");
            return badRequest(failJson("Rating not updated, expecting Json data"));
        }
        System.out.println(json.toString());

        try{
            int rate = json.findPath("rate").asInt();
            Long ratingId = json.findPath("rating_id").asLong();
            Date postedDate = timeFormat.parse(json.findPath("posted_date").asText());

            Rating rating = ratingRepository.findRatingById(ratingId);
            rating.setRate(rate);
            rating.setPostedDate(postedDate);
            Rating ratingEntry = ratingRepository.save(rating);

            response.put("success", true);
            response.put("rate", ratingEntry.getRate());
        }
        catch (ParseException pe){
            pe.printStackTrace();
            System.out.println("Invalid date format");
            return badRequest(failJson("Invalid date format"));
        }
        catch (PersistenceException pe) {
            pe.printStackTrace();
            System.out.println("Rating not updated");
            return badRequest(failJson("Rating not updated"));
        }

        return ok(response.toString());
    }

    public Result deleteRating(Long user_id, Long service_id){
        System.out.println("DELETE RATING");

        ObjectNode response = Json.newObject();

        try{
            deleteRatingById(user_id, service_id);

            response.put("success", true);
            response.put("total_rating", ratingRepository.countRatings(service_id));
        }
        catch (PersistenceException pe) {
            pe.printStackTrace();
            System.out.println("Rating not deleted");
            return badRequest(failJson("Rating not deleted"));
        }

        return ok(response.toString());
    }
}
