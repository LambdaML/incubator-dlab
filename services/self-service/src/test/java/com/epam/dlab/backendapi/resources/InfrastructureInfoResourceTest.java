package com.epam.dlab.backendapi.resources;

import com.epam.dlab.backendapi.resources.dto.HealthStatusPageDTO;
import com.epam.dlab.backendapi.resources.dto.InfrastructureInfo;
import com.epam.dlab.backendapi.service.InfrastructureInfoService;
import com.epam.dlab.exceptions.DlabException;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class InfrastructureInfoResourceTest {

	private InfrastructureInfoService infrastructureInfoService = mock(InfrastructureInfoService.class);

	@Rule
	public final ResourceTestRule resources =
			TestHelper.getResourceTestRuleInstance(new InfrastructureInfoResource(infrastructureInfoService));

	@Before
	public void setup() throws AuthenticationException {
		TestHelper.authSetup();
	}

	@Test
	public void status() {
		final Response response = resources.getJerseyTest()
				.target("/infrastructure")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertNull(response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(infrastructureInfoService);
	}

	@Test
	public void statusWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		final Response response = resources.getJerseyTest()
				.target("/infrastructure")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertNull(response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(infrastructureInfoService);
	}

	@Test
	public void healthStatus() {
		HealthStatusPageDTO hspDto = getHealthStatusPageDTO();
		when(infrastructureInfoService.getHeathStatus(anyString(), anyBoolean(), anyBoolean())).thenReturn(hspDto);
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/status")
				.queryParam("full", "1")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(hspDto.getStatus(), response.readEntity(HealthStatusPageDTO.class).getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getHeathStatus(eq(TestHelper.USER.toLowerCase()), eq(true), anyBoolean());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	@Test
	public void healthStatusWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		HealthStatusPageDTO hspDto = getHealthStatusPageDTO();
		when(infrastructureInfoService.getHeathStatus(anyString(), anyBoolean(), anyBoolean())).thenReturn(hspDto);
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/status")
				.queryParam("full", "1")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(hspDto.getStatus(), response.readEntity(HealthStatusPageDTO.class).getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getHeathStatus(eq(TestHelper.USER.toLowerCase()), eq(true), anyBoolean());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	@Test
	public void healthStatusWithDefaultQueryParam() {
		HealthStatusPageDTO hspDto = getHealthStatusPageDTO();
		when(infrastructureInfoService.getHeathStatus(anyString(), anyBoolean(), anyBoolean())).thenReturn(hspDto);
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/status")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(hspDto.getStatus(), response.readEntity(HealthStatusPageDTO.class).getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getHeathStatus(eq(TestHelper.USER.toLowerCase()), eq(false), anyBoolean());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	@Test
	public void healthStatusWithException() {
		doThrow(new DlabException("Could not return status of resources for user"))
				.when(infrastructureInfoService).getHeathStatus(anyString(), anyBoolean(), anyBoolean());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/status")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getHeathStatus(eq(TestHelper.USER.toLowerCase()), eq(false), anyBoolean());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	@Test
	public void getUserResources() {
		InfrastructureInfo info = getInfrastructureInfo();
		when(infrastructureInfoService.getUserResources(anyString())).thenReturn(info);
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/info")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(info.toString(), response.readEntity(InfrastructureInfo.class).toString());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getUserResources(TestHelper.USER.toLowerCase());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	@Test
	public void getUserResourcesWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		InfrastructureInfo info = getInfrastructureInfo();
		when(infrastructureInfoService.getUserResources(anyString())).thenReturn(info);
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/info")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(info.toString(), response.readEntity(InfrastructureInfo.class).toString());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getUserResources(TestHelper.USER.toLowerCase());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	@Test
	public void getUserResourcesWithException() {
		doThrow(new DlabException("Could not load list of provisioned resources for user"))
				.when(infrastructureInfoService).getUserResources(anyString());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure/info")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(infrastructureInfoService).getUserResources(TestHelper.USER.toLowerCase());
		verifyNoMoreInteractions(infrastructureInfoService);
	}

	private HealthStatusPageDTO getHealthStatusPageDTO() {
		HealthStatusPageDTO hspdto = new HealthStatusPageDTO();
		hspdto.setStatus("someStatus");
		return hspdto;
	}

	private InfrastructureInfo getInfrastructureInfo() {
		return new InfrastructureInfo(Collections.emptyMap(), Collections.emptyList());
	}
}
