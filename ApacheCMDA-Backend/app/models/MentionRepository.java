/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import java.util.List;
import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many standard
 * operations here.
 */
@Named
@Singleton
public interface MentionRepository extends CrudRepository<Mention, Long>{
    @Query(value = "select m.commentId from Mention m where m.username = ?1 order by m.commentId desc", nativeQuery = true)
	List<BigInteger> findAllCommentIdByUsername(String username);
    @Query(value = "select m.* from Mention m where m.commentId = ?1", nativeQuery = true)
	List<Mention> findAllMentionByCommentId(Long commentId);
    @Query(value = "select m.* from Mention m where m.commentId = ?1 and username = ?2", nativeQuery = true)
	Mention findMentionByCommentIdAndName(Long commentId, String username);
}
