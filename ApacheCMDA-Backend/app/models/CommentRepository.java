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

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface CommentRepository extends CrudRepository<Comment, Long>{
    @Query(value = "select c.* from Comment c where c.elementId = ?1 and c.versionId = ?2 and c.parentId = ?3 order by postedDate desc", nativeQuery = true)
	List<Comment> findAllByClimateServiceIdAndVersionIdAndParentId(Long elementId, Long versionId, Long parentId);
    @Query(value = "select count(*) from Comment where elementId = ?1 and versionId = ?2", nativeQuery = true)
	Long countComments(Long elementId, Long versionId);
    @Query(value = "select c.* from Comment c where c.commentId = ?1", nativeQuery = true)
	Comment findCommentById(Long commentId);
}
