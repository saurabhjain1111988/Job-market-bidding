package com.market.jobmarket.project.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.market.jobmarket.project.domain.order.BidOrder;
import com.market.jobmarket.project.service.BiddingService;

@Controller
@Path("/bid")
public class BidResource {

	@Autowired
	BiddingService biddingService;

	@GET
	@Path("/user/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUserBids(@PathParam("userId") Integer userId) {
		List<BidOrder> allBids = biddingService.getAllBids(userId);
		return Response.ok(allBids).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response placeBid(BidOrder bidOrder) {
		if (null == bidOrder || bidOrder.isInvalid()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request, Bid Order is Invalid").build();
		}
		biddingService.placeBid(bidOrder);
		return Response.created(UriBuilder.fromResource(BidResource.class).build(bidOrder)).build();
	}

	@DELETE
	@Path("/{bidOrderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response cancelBid(@PathParam("bidOrderId") Integer bidOrderId) {
		BidOrder bidOrder = biddingService.getBid(bidOrderId);
		if (null == bidOrder) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Request, Bid Order not found").build();
		}
		biddingService.cancelBid(bidOrderId);
		return Response.ok().build();
	}

}
