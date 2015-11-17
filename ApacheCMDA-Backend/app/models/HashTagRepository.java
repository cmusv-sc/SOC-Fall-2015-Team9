package models;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by seckcoder on 11/17/15.
 */

@Named
@Singleton
public interface HashTagRepository extends CrudRepository<HashTag, Long> {
}
