package models;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by seckcoder on 11/17/15.
 */

@Named
@Singleton
public interface HashTagRepository extends CrudRepository<HashTag, Long> {
    @Query(value = "select h.* from HashTag h where lower(h.content) like lower(?1)", nativeQuery=true)
	List<HashTag> findHashTags(String hashTag);
    @Query(value = "select h.* from HashTag h where h.commentId=?1", nativeQuery=true)
	List<HashTag> findHashTagsByCommentId(Long commentId);
}
