package com.market.jobmarket.project.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.market.jobmarket.project.domain.project.Project;
import com.market.jobmarket.project.service.ProjectService;

@Controller
@Path("/project")
public class ProjectResource {

	@Autowired
	ProjectService projectService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllOpenProjects() {
		List<Project> projects = new ArrayList<Project>();
		try {
			projects = projectService.getAllOpenProjects();
		} catch (Exception e) {
			Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Exception occured while getting all the open projects ").build();
		}
		return Response.ok(projects).build();
	}

	@GET
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProjectById(@PathParam("/projectId") Integer projectId) {
		Project project = projectService.getProject(projectId);
		if (null == project) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Project found for projectId " + projectId)
					.build();
		}
		return Response.ok(project).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addProject(Project project) {
		if (null == project || null == project.getSeller() || null == project.getSeller().getId()
				|| project.getSeller().getId() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request").build();
		}
		projectService.addProject(project);
		return Response.created(UriBuilder.fromResource(ProjectResource.class).build(project)).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProject(Project project) {
		if (null == project || null == project.getId() || project.getId() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request").build();
		}
		projectService.updateProject(project);
		return Response.ok(project).build();
	}

	@DELETE
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("projectId") Integer projectId) {
		Project project = projectService.getProject(projectId);
		if (null == project) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Project not found with projectId=" + projectId)
					.build();
		}
		projectService.delete(projectId);
		return Response.ok().build();
	}

}
