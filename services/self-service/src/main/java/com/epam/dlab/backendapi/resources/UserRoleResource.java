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
package com.epam.dlab.backendapi.resources;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.resources.dto.UserRoleDto;
import com.epam.dlab.backendapi.resources.swagger.SwaggerSecurityInfo;
import com.epam.dlab.backendapi.service.UserRoleService;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("role")
@RolesAllowed("/roleManagement")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "User's roles resource", authorizations = @Authorization(SwaggerSecurityInfo.TOKEN_AUTH))
public class UserRoleResource {

	private final UserRoleService userRoleService;

	@Inject
	public UserRoleResource(UserRoleService roleService) {
		this.userRoleService = roleService;
	}

	@GET
	@ApiOperation("List user's roles present in application")
	@ApiResponses(value = @ApiResponse(code = 200, message = "User roles present in application"))
	public Response getRoles(@ApiParam(hidden = true) @Auth UserInfo userInfo) {
		log.debug("Getting all roles for admin {}...", userInfo.getName());
		return Response.ok(userRoleService.getUserRoles()).build();
	}

	@POST
	@ApiOperation(value = "Creates new user role")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Validation exception occurred"),
			@ApiResponse(code = 200, message = "User role is successfully added")}
	)
	public Response createRole(@Auth UserInfo userInfo, UserRoleDto dto) {
		log.info("Creating new role {} on behalf of admin {}...", dto, userInfo.getName());
		userRoleService.createRole(dto);
		return Response.ok().build();
	}
}
