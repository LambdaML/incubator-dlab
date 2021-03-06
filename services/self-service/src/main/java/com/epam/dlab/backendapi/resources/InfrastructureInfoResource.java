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
import com.epam.dlab.backendapi.resources.dto.HealthStatusPageDTO;
import com.epam.dlab.backendapi.resources.dto.InfrastructureInfo;
import com.epam.dlab.backendapi.resources.swagger.SwaggerSecurityInfo;
import com.epam.dlab.backendapi.roles.UserRoles;
import com.epam.dlab.backendapi.service.InfrastructureInfoService;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Provides the REST API for the basic information about infrastructure.
 */
@Path("/infrastructure")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Infrastructure info service", authorizations = @Authorization(SwaggerSecurityInfo.TOKEN_AUTH))
@Slf4j
public class InfrastructureInfoResource {

	private InfrastructureInfoService infrastructureInfoService;

	@Inject
	public InfrastructureInfoResource(InfrastructureInfoService infrastructureInfoService) {
		this.infrastructureInfoService = infrastructureInfoService;
	}

	/**
	 * Return status of self-service.
	 */
	@GET
	@ApiOperation("Returns status of self-service")
	@ApiResponses(@ApiResponse(code = 200, message = "Self-service's status fetched successfully"))
	public Response status() {
		return Response.status(Response.Status.OK).build();
	}

	/**
	 * Returns the status of infrastructure: edge.
	 *
	 * @param userInfo user info.
	 */
	@GET
	@Path("/status")
	@ApiOperation("Returns EDGE's status")
	public HealthStatusPageDTO status(@ApiParam(hidden = true) @Auth UserInfo userInfo,
									  @ApiParam(value = "Full version of report required", defaultValue = "0")
									  @QueryParam("full") @DefaultValue("0") int fullReport) {
		return infrastructureInfoService
				.getHeathStatus(userInfo.getName(), fullReport != 0, UserRoles.isAdmin(userInfo));
	}

	/**
	 * Returns the list of the provisioned user resources.
	 *
	 * @param userInfo user info.
	 */
	@GET
	@Path("/info")
	@ApiOperation("Returns list of user's resources")
	public InfrastructureInfo getUserResources(@ApiParam(hidden = true) @Auth UserInfo userInfo) {
		return infrastructureInfoService.getUserResources(userInfo.getName());

	}
}
