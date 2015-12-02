package models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ratingId;
    private long userId;
    private long serviceId;
    private String fullName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;
    private int rate;


    public Rating(){

    }

    public Rating(long userId, long serviceId, String fullName, Date postedDate, int rate){
        super();
        this.userId = userId;
        this.serviceId = serviceId;
        this.postedDate = postedDate;
    }

    public long getRatingId() {
        return ratingId;
    }

    public void setRatingId(long ratingId) {
        this.ratingId = ratingId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }


    @Override
    public String toString() {
        return "Rating from " + fullName + " @ " + postedDate + ": " + rate;
    }
}
