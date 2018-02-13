package com.market.jobmarket.project.resource;


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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.market.jobmarket.project.domain.order.PurchaseOrder;
import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.service.PurchaseService;
import com.market.jobmarket.project.service.UserService;

@Controller
@Path("/user")
public class UserResource {

	@Autowired
	UserService userService;
	
	@Autowired
	PurchaseService purchaseService;

	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("userId") Integer userId) {
		BaseUser user = userService.getUser(userId);
		if(null == user) {
			return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
		}
		return Response.ok(user).build();
	}
	
	@GET
	@Path("/{userId}/purchaseOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPurchasedOrders(@PathParam("userId") Integer userId) {
		BaseUser user = userService.getUser(userId);
		if(null == user) {
			return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
		}
		List<PurchaseOrder> purchaseOrders = purchaseService.getPurchaseOrder(userId);
		return Response.ok(purchaseOrders).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(BaseUser user) {
		if(null == user || StringUtils.isEmpty(user.getDisplayName())) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request, User or User's display name cannot be null").build();
		}
		userService.registerUser(user);
		return Response.created(UriBuilder.fromResource(UserResource.class).build(user)).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(BaseUser user) {
		if(null == user || null == user.getId() || user.getId() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request").build();
		}
		userService.updateUser(user);
		return Response.ok(user).build();
	}
	
	@DELETE
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("userId") Integer userId) {
		BaseUser user = userService.getUser(userId);
		if(null == user) {
			return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
		}
		userService.deleteUser(userId);
		return Response.ok().build();
	}
	
}
