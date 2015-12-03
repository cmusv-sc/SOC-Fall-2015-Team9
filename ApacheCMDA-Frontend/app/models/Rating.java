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
    private long versionId;
    private int rate;


    public Rating(){

    }

    public Rating(long userId, long serviceId, long versionId, int rate){
        super();
        this.userId = userId;
        this.serviceId = serviceId;
        this.versionId = versionId;
        this.rate = rate;
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

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

}