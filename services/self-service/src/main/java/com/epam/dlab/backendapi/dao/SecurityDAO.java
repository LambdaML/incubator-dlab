/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.epam.dlab.backendapi.dao;

import com.epam.dlab.auth.dto.UserCredentialDTO;
import com.epam.dlab.exceptions.DlabException;
import com.epam.dlab.util.UsernameUtils;
import com.google.inject.Singleton;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

import static com.epam.dlab.backendapi.dao.MongoCollections.LOGIN_ATTEMPTS;
import static com.epam.dlab.backendapi.dao.MongoCollections.ROLES;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;

/**
 * DAO write the attempt of user login into DLab.
 */
@Singleton
public class SecurityDAO extends BaseDAO {

	/**
	 * Write the attempt of user login into Mongo database.
	 *
	 * @param credentials user credentials.
	 */
	public void writeLoginAttempt(UserCredentialDTO credentials) {
		insertOne(LOGIN_ATTEMPTS,
				() -> new Document("login", credentials.getUsername()).append("iamlogin", UsernameUtils.removeDomain
						(credentials.getUsername())));
	}

	/**
	 * Return the roles or throw exception if roles collection does not exists.
	 */
	public FindIterable<Document> getRoles() {
		if (!collectionExists(ROLES)) {
			throw new DlabException("Collection \"" + ROLES + "\" does not exists.");
		}
		return find(ROLES, ne(ID, "_Example"), fields(exclude("description")));
	}

	public Map<String, Set<String>> getGroups() {
		return stream(find("userGroups"))
				.collect(Collectors.toMap(d -> d.getString(ID).toLowerCase(), this::toUsers));

	}

	@SuppressWarnings("unchecked")
	private Set<String> toUsers(Document d) {
		final Object users = d.get("users");
		return users == null ? Collections.emptySet() : new HashSet<>((List<String>) users);
	}
}
