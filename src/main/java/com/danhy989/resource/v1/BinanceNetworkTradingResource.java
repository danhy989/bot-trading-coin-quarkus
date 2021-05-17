package com.danhy989.resource.v1;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danhy989.service.CryptocurrencySocketService;

@Path("/v1/trading/bsc")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BinanceNetworkTradingResource {

  @Inject
  CryptocurrencySocketService cryptocurrencySocketService;

  private final Logger log = LoggerFactory.getLogger(BinanceNetworkTradingResource.class);

  /* localhost:8080/v1/trading/bsc/start/btcusdt */
  @GET
  @Path("/start/{pair}")
  public Response startWebSocket(@PathParam String pair) {
    final boolean result = cryptocurrencySocketService.startSocketClient(pair);
    if (!result) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    return Response.ok().build();
  }

  /* localhost:8080/v1/trading/bsc/stop */
  @GET
  @Path("/stop")
  public Response stopWebSocket() {
    final boolean result = cryptocurrencySocketService.stopSocketClient();
    if (!result) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    return Response.ok().build();
  }
}
