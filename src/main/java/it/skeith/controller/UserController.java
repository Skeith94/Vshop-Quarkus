package it.skeith.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.json.JsonObject;
import it.skeith.entity.Country;
import it.skeith.entity.Role;
import it.skeith.entity.User;
import it.skeith.payload.request.BannRequest;
import it.skeith.payload.request.LoginRequest;
import it.skeith.payload.request.RegistrerRequest;
import it.skeith.payload.response.LoginResponse;
import it.skeith.service.UserService;
import lombok.extern.slf4j.Slf4j;


import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestSseElementType;


import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;





@Slf4j
@Path("/user")
@ApplicationScoped

public class UserController {

    @Inject ManagedExecutor executor;
    @Inject
    UserService userService;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> getUserRepo(String username) {
        return Panache.withTransaction(()->userService.getUserRepo(username)).onItem()
                .ifNotNull().transform(user->Response.ok(user).build())
                .onItem().ifNull().continueWith(Response.ok().status(Response.Status.NOT_FOUND).build());
    }


    @POST
    @Path("/login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> login(@RequestBody @Valid LoginRequest request) {
        return Panache.withTransaction(()->userService.getByEmail(request.getEmail())).onItem()

    .ifNull().failWith(new WebApplicationException("wrong email")).onItem().transform(logged -> {

    if (BCrypt.verifyer().verify(request.getPassword().toCharArray(), logged.getPassword().toCharArray()).verified) {
               if (!logged.isEnabled()) {
                 return Response.ok("you are banned for: " + logged.getReason()).type(TEXT_PLAIN_TYPE).build();
                 }

      String token = Jwt.issuer("Vshop")
      .upn(logged.getUsername())
      .groups(new HashSet<>(Arrays.asList(logged.getRole().getName()))).innerSign().encrypt();

       System.out.println(token);
       return Response.ok(new LoginResponse(logged.getUsername(), logged.getRole().getName(), token)).type(APPLICATION_JSON_TYPE).build();
    }
    return Response.ok("wrong password").type(TEXT_PLAIN_TYPE).build();
    });


    }



    @RolesAllowed({"Admin"})
    @Transactional
    @POST
    @Path("/bann")
    @Blocking
    public Uni<Response> bannUser(@RequestBody @Valid BannRequest request)  {

    return  Panache.withTransaction(()->userService.getByEmail(request.getEmail())).onItem().ifNull().failWith(new WebApplicationException("wrong email"))
            .onItem().transform(user -> {
                user.setEnabled(false);
                user.setReason(request.getReason());
                return user;

            }).onItem().transformToUni(user ->userService.persistFlushUser(user)).onItem().transform(userFinish->Response.ok("\"user with id:" + userFinish.getId() + " email:" + userFinish.getEmail() + " banned").type(TEXT_PLAIN_TYPE).build());

    }

    @POST
    @RolesAllowed({"Admin"})
    @Path("/createRole")
    @ReactiveTransactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createRole(@Size(min = 1, max = 15) @RestQuery String role){
        return Panache.withTransaction(()->Role.findByName(role)).onItem().ifNotNull()
                .failWith(new WebApplicationException("role already exist"))
                .onItem().transform(empty->new Role(role)).onItem().transformToUni(roleToADD->Role.persist(roleToADD)).onItem()
                .transform(roleFinish->Response.ok("role created").type(TEXT_PLAIN_TYPE).build());

    }


    @POST
    @PermitAll
    @ReactiveTransactional
    @Path("/addRole/{userId}/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addRole(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) throws SystemException, InterruptedException {

       Uni<User>user=Panache.withTransaction(()->userService.findById(userId)).onItem().ifNull().failWith(new WebApplicationException("user id no present"));

       Uni<Role>role=Panache.withTransaction(()->Role.findById(roleId)).onItem().ifNull().failWith(new WebApplicationException("role id no present"));

       return Uni.combine().all().unis(user,role).asTuple().onItem().transform(tuple->{

          tuple.getItem1().setRole(tuple.getItem2());
          return tuple.getItem1();

       })
       .onItem().transformToUni(userUp->userService.persistFlushUser(userUp))
       .onItem().transform(userFinish->Response.ok("user:"+userFinish.getEmail()+" role:"+userFinish.getRole().getName()+" updated").type(TEXT_PLAIN_TYPE).build());

    }


    @PermitAll
    @POST
    @ReactiveTransactional
    @Path("/registrer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> register(@RequestBody @Valid RegistrerRequest request) throws ExecutionException, InterruptedException {

        Uni<Country>countryCheck=  Panache.withTransaction(()->Country.findById(request.getCountry_id())).onItem().ifNull()
                .failWith(new WebApplicationException("country id no present"));

        Uni<Long> emailCheck = Panache.withTransaction(() -> userService.getByNameEmail(request.getEmail()));
        Uni<Long> userCheck = Panache.withTransaction(() -> userService.getByNameEmail(request.getEmail()));

       return Uni.combine().all().unis(countryCheck,emailCheck,userCheck).asTuple().onItem()

               .transform(Unchecked.function(tuple->{
                                                     if(tuple.getItem2()!=0){
                                                     throw new WebApplicationException("email already use");
               }

        return  new User(request.getUsername(), request.getPassword(), request.getEmail(), request.getName(), request.getSurname(), request.getBirth(), request.getAddress(), tuple.getItem1());

        })
     ).onItem().transformToUni(user -> userService.persistFlushUser(user)).onItem().transform(i->Response.ok().build());






    }


}

