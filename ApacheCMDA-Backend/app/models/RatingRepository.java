package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface RatingRepository extends CrudRepository<Rating, Long>{

    @Query(value = "SELECT * FROM Rating WHERE userId = ?1 AND serviceId = ?2 AND versionId = ?3", nativeQuery = true)
    Rating getIndividualRating(Long userId, Long serviceId, Long versionId);

    @Query(value = "SELECT AVG(rate) FROM Rating WHERE serviceId = ?1 AND versionId = ?2", nativeQuery = true)
    double getAverageRate(Long serviceId, Long versionId);

//    @Query(value = "INSERT INTO Rating (userId, serviceId, versionId, rate) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
//    void insertNewRating(Long userId, Long serviceId, Long versionId, int rate);
//
//    @Query(value = "UPDATE Rating SET rate = ?1 WHERE id  = ?2")
//    void updateExistingRating(int rate, Long ratingId);
}


