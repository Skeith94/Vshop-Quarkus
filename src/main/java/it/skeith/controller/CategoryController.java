package it.skeith.controller;


import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.Photos;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
@Path("/category")

@Transactional
public class CategoryController {
    @POST
    @PermitAll
    @Transactional
    @Path("/prova")
    public Uni<RestResponse<String>> getUserRepo(String username) {
        Photos photos=new Photos();
        photos.setProductId(2L);
        photos.persist();
        return  Uni.createFrom().item(RestResponse.ok("you are banned for: ",TEXT_PLAIN_TYPE));
    }
}
