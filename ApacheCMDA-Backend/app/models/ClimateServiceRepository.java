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
public interface ClimateServiceRepository extends CrudRepository<ClimateService, Long> {
    List<ClimateService> findAllByName(String name);
    ClimateService findFirstByName(String oldName);
    List<ClimateService> findAllByPurpose(String purpose);
    List<ClimateService> findByOrderByCreateTimeDesc();

    @Query(value = "select c.* from ClimateService c where lower(c.name) like lower(?1)", nativeQuery = true)
	List<ClimateService> findServiceWithName(String name);
    @Query(value = "select c.* from ClimateService c where lower(c.purpose) like lower(?1)", nativeQuery = true)
	List<ClimateService> findServiceWithPurpose(String purpose);
    @Query(value = "select c.* from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by sum(s.count) desc", nativeQuery = true)
	List<ClimateService> getClimateServiceOrderByCount();
    @Query(value = "select c.* from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by s.latestAccessTimeStamp desc", nativeQuery = true)
	List<ClimateService> getClimateServiceOrderByLatestAccessTime();
}
