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
    @Query(value = "select r.* from Rating r where r.userId = ?1 and r.serviceId = ?2 order by postedDate desc", nativeQuery = true)
    List<Rating> findAllByClimateServiceId(Long userId, Long serviceId);
    @Query(value = "select count(*) from Rating where serviceId = ?1", nativeQuery = true)
    Long countRatings(Long serviceId);
    @Query(value = "select r.* from Rating r where r.ratingId = ?1", nativeQuery = true)
    Rating findRatingById(Long ratingId);
}
