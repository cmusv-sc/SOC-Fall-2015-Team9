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
public interface UserRepository extends CrudRepository<User, Long>{
    @Query(value = "select u.* from User u where u.userName = ?1", nativeQuery = true)
	List<User> findByUserName(String userName);
    @Query(value = "select u.* from User u where u.email = ?1", nativeQuery = true)
	User findByEmail(String email);
    @Query(value = "select u.userName from User u where u.email = ?1", nativeQuery = true)
	String getUsernameByEmail(String email);
    @Query(value = "select u.id from User u where u.email = ?1", nativeQuery = true)
	Long getUserIdByEmail(String email);
    @Query(value = "select u.userName from User u where u.id = ?1", nativeQuery = true)
	String getUsernameById(Long id);
    @Query(value = "select u.userName from User u", nativeQuery = true)
	List<String> getAllUsername();
}
