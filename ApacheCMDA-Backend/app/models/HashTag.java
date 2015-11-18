package models;

import javax.persistence.*;

/**
 * Created by seckcoder on 11/17/15.
 */



@Entity
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "commentId", referencedColumnName = "commentId")
    private Comment comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "serviceId", referencedColumnName = "id")
    private ClimateService climateService;

    private String content;

    public HashTag() {
    }

    public HashTag(Comment comment, ClimateService climateService, String content) {
        super();
        this.comment = comment;
        this.climateService = climateService;
        this.content = content;
    }

    public Comment getComment() {
        return comment;
    }
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ClimateService getClimateService() {
        return climateService;
    }
    public void setClimateService(ClimateService climateService) {
        this.climateService = climateService;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "stupid";
    }
}
