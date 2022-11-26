package it.skeith.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import it.skeith.entity.Country;
import it.skeith.entity.User;
import it.skeith.payload.request.BannRequest;
import it.skeith.payload.request.LoginRequest;
import it.skeith.payload.request.RegistrerRequest;
import it.skeith.payload.response.LoginResponse;
import it.skeith.service.UserService;
import lombok.extern.slf4j.Slf4j;


import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import org.jboss.resteasy.reactive.RestResponse;


import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
@Path("/user")
public class UserController  {


    @Inject
    UserService userService;
    @GET
    @RolesAllowed({"User","Admin"})
    @Path("/{username}")
    public Uni<RestResponse<?>> getUserRepo(String username) {
       return userService.getUserRepo(username).onItem().transform(u->RestResponse.ok(u, APPLICATION_JSON_TYPE));
    }



    @POST
    @Path("/login")
    @PermitAll
    @Blocking
    public Uni<RestResponse<?>>login(@RequestBody @Valid LoginRequest request){
       Optional<User> logged=Optional.ofNullable(userService.getByEmail(request.getEmail()).await().indefinitely());
        if(logged.isPresent()){
            if(BCrypt.verifyer().verify(request.getPassword().toCharArray(),logged.get().getPassword().toCharArray()).verified){
                if(!logged.get().isEnabled()){
                    return Uni.createFrom().item(RestResponse.ok("you are banned for: "+logged.get().getReason(),TEXT_PLAIN_TYPE));
                }
                String token= Jwt.issuer("Vshop")
                        .upn(logged.get().getUsername())
                        .groups(new HashSet<>(Arrays.asList(logged.get().getRole().getName()))).innerSign().encrypt();

                System.out.println(token);
                return Uni.createFrom().item(RestResponse.ok(new LoginResponse(logged.get().getUsername(),logged.get().getRole().getName(),token),APPLICATION_JSON_TYPE));
            }
            return Uni.createFrom().item(RestResponse.status(RestResponse.Status.BAD_REQUEST, new JsonObject("{\"error\":\"wrong password\"}")));
        }
        return Uni.createFrom().item(RestResponse.status(RestResponse.Status.BAD_REQUEST, new JsonObject("{\"error\":\"wrong email\"}")));
    }




    @PermitAll
    @Transactional
    @POST
    @Path("/register")
    @Blocking
    public Uni<RestResponse<?>> register(@RequestBody @Valid RegistrerRequest request) throws SystemException{


      Optional<Country> country=Country.findById(request.getCountry_id()).await().asOptional().indefinitely();

        if(country.isEmpty()){
           return Uni.createFrom().item(RestResponse.status(RestResponse.Status.BAD_REQUEST, new JsonObject("{\"error\":\"country no exist\"}")));
        }

      User user=new User(request.getUsername(), request.getPassword(), request.getEmail(),request.getName(),request.getSurname(),request.getBirth(),request.getAddress(),country.get());


      Long a=userService.getByNameEmail(request.getUsername(),request.getEmail()).await().indefinitely();

      if(a==0){
          return  userService.persistFlushUser(user).onItem().transform(u->RestResponse.ok(u, APPLICATION_JSON_TYPE));

      }

        return Uni.createFrom().item(RestResponse.status(RestResponse.Status.BAD_REQUEST, new JsonObject("{\"error\":\"user or email already use\"}")));

    }


    @RolesAllowed({"Admin"})
    @Transactional
    @POST
    @Path("/bann")
    @Blocking
    public Uni<RestResponse<?>> bannUser(@RequestBody @Valid BannRequest request) throws SystemException{

       Optional<User> user= Optional.ofNullable(userService.getByEmail(request.getEmail()).await().indefinitely());

        if(user.isEmpty()){
            return Uni.createFrom().item(RestResponse.status(RestResponse.Status.BAD_REQUEST, new JsonObject("{\"error\":\"email not found\"}")));
        }

        user.get().setEnabled(false);
        user.get().setReason(request.getReason());
        return userService.persistFlushUser(user.get()).onItem().transform(i->RestResponse.ok("user with id:"+user.get().getId()+" email:"+user.get().getEmail()+" banned",TEXT_PLAIN_TYPE));
    }








}
