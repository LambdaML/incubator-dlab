package com.epam.dlab.backendapi.resources;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.resources.dto.ExploratoryActionFormDTO;
import com.epam.dlab.backendapi.resources.dto.ExploratoryCreateFormDTO;
import com.epam.dlab.backendapi.service.ExploratoryService;
import com.epam.dlab.exceptions.DlabException;
import com.epam.dlab.model.exloratory.Exploratory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ExploratoryResourceTest {

	private ExploratoryService exploratoryService = mock(ExploratoryService.class);

	@Rule
	public final ResourceTestRule resources =
			TestHelper.getResourceTestRuleInstance(new ExploratoryResource(exploratoryService));

	@Before
	public void setup() throws AuthenticationException {
		TestHelper.authSetup();
	}

	@Test
	public void create() {
		when(exploratoryService.create(any(UserInfo.class), any(Exploratory.class))).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.put(Entity.json(getExploratoryCreateFormDTO()));

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals("someUuid", response.readEntity(String.class));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).create(TestHelper.getUserInfo(), getExploratory(getExploratoryCreateFormDTO()));
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void createWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		when(exploratoryService.create(any(UserInfo.class), any(Exploratory.class))).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.put(Entity.json(getExploratoryCreateFormDTO()));

		assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(exploratoryService);
	}

	@Test
	public void createWithException() {
		doThrow(new DlabException("Could not create exploratory environment"))
				.when(exploratoryService).create(any(UserInfo.class), any(Exploratory.class));
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.put(Entity.json(getExploratoryCreateFormDTO()));

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String expectedJson = "\"code\":500,\"message\":\"There was an error processing your request. " +
				"It has been logged";
		String actualJson = response.readEntity(String.class);
		assertTrue(actualJson.contains(expectedJson));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).create(TestHelper.getUserInfo(), getExploratory(getExploratoryCreateFormDTO()));
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void start() {
		when(exploratoryService.start(any(UserInfo.class), anyString())).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.post(Entity.json(getExploratoryActionFormDTO()));

		assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
		assertEquals("{\"errors\":[\"notebookInstanceName may not be empty\"]}", response.readEntity(String.class));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(exploratoryService);
	}

	@Test
	public void startWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		when(exploratoryService.start(any(UserInfo.class), anyString())).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.post(Entity.json(getExploratoryActionFormDTO()));

		assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(exploratoryService);
	}

	@Test
	public void stop() {
		when(exploratoryService.stop(any(UserInfo.class), anyString())).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/someName/stop")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.delete();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals("someUuid", response.readEntity(String.class));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).stop(TestHelper.getUserInfo(), "someName");
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void stopWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		when(exploratoryService.stop(any(UserInfo.class), anyString())).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/someName/stop")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.delete();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals("someUuid", response.readEntity(String.class));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).stop(TestHelper.getUserInfo(), "someName");
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void stopWithException() {
		doThrow(new DlabException("Could not stop exploratory environment"))
				.when(exploratoryService).stop(any(UserInfo.class), anyString());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/someName/stop")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.delete();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String expectedJson = "\"code\":500,\"message\":\"There was an error processing your request. " +
				"It has been logged";
		String actualJson = response.readEntity(String.class);
		assertTrue(actualJson.contains(expectedJson));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).stop(TestHelper.getUserInfo(), "someName");
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void terminate() {
		when(exploratoryService.terminate(any(UserInfo.class), anyString())).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/someName/terminate")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.delete();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals("someUuid", response.readEntity(String.class));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).terminate(TestHelper.getUserInfo(), "someName");
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void terminateWithFailedAuth() throws AuthenticationException {
		TestHelper.authFailSetup();
		when(exploratoryService.terminate(any(UserInfo.class), anyString())).thenReturn("someUuid");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/someName/terminate")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.delete();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals("someUuid", response.readEntity(String.class));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).terminate(TestHelper.getUserInfo(), "someName");
		verifyNoMoreInteractions(exploratoryService);
	}

	@Test
	public void terminateWithException() {
		doThrow(new DlabException("Could not terminate exploratory environment"))
				.when(exploratoryService).terminate(any(UserInfo.class), anyString());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/someName/terminate")
				.request()
				.header("Authorization", "Bearer " + TestHelper.TOKEN)
				.delete();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String expectedJson = "\"code\":500,\"message\":\"There was an error processing your request. " +
				"It has been logged";
		String actualJson = response.readEntity(String.class);
		assertTrue(actualJson.contains(expectedJson));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryService).terminate(TestHelper.getUserInfo(), "someName");
		verifyNoMoreInteractions(exploratoryService);
	}

	private ExploratoryCreateFormDTO getExploratoryCreateFormDTO() {
		ExploratoryCreateFormDTO ecfDto = new ExploratoryCreateFormDTO();
		ecfDto.setImage("someImage");
		ecfDto.setTemplateName("someTemplateName");
		ecfDto.setName("someName");
		ecfDto.setShape("someShape");
		ecfDto.setVersion("someVersion");
		ecfDto.setImageName("someImageName");
		return ecfDto;
	}

	private ExploratoryActionFormDTO getExploratoryActionFormDTO() {
		return new ExploratoryActionFormDTO();
	}

	private Exploratory getExploratory(@Valid @NotNull ExploratoryCreateFormDTO formDTO) {
		return Exploratory.builder()
				.name(formDTO.getName())
				.dockerImage(formDTO.getImage())
				.imageName(formDTO.getImageName())
				.templateName(formDTO.getTemplateName())
				.version(formDTO.getVersion())
				.shape(formDTO.getShape()).build();
	}
}
