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
public interface VersionRepository extends CrudRepository<Version, Long>{
    @Query(value = "select v.* from Version v order by v.serviceId, v.versionId", nativeQuery = true)
	List<Version> getAllVersions();
    @Query(value = "select v.* from Version v where v.serviceId = ?1 order by v.versionId", nativeQuery = true)
	List<Version> getAllVersionsById(Long serviceId);
    @Query(value = "select v.* from Version v order by v.latestAccessTimeStamp desc limit 3", nativeQuery = true)
	List<Version> getTop3MostRecentUsed();
    @Query(value = "select v.* from Version v where v.url=?1 and v.versionId=?2", nativeQuery = true)
	Version getOneByUrlAndVersion(String url, Long version);
    @Query(value = "select v.url from Version v where v.serviceId = ?1 and v.versionId=?2", nativeQuery = true)
	String getUrlByServiceAndVersion(Long serviceId, Long versionId);
}
