package it.skeith.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.skeith.entity.Photos;
import it.skeith.service.CategoryService;
import it.skeith.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Path("/photos")
@ApplicationScoped
public class PhotosController {
    @Inject
    CategoryService categoryService;
    @Inject
    ProductService productService;

    @ConfigProperty(name = "image.size")
    Long imageSize;
    @ConfigProperty(name = "image.extensions")
    List<String> extensions;

    @Schema(type = SchemaType.STRING, format = "binary")
    public static class UploadItemSchema {
    }

    @PermitAll
    @Path("/add/{productId}")
    @POST()
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @ReactiveTransactional
    public Uni<Response> addPhotos(@PathParam("productId") Long productId, @RestForm @Schema(implementation = UploadItemSchema.class) FileUpload photo) {

        return   Panache.withTransaction(()->productService.getById(productId)).onItem().ifNull().failWith(new WebApplicationException("product not found"))
                .onItem().ifNotNull().transform(Unchecked.function(product ->{
                    if (photo.size() > imageSize) {
                        throw new WebApplicationException("invalid size");
                    }

                    boolean result = extensions.stream().anyMatch(e -> photo.contentType().contentEquals("image/" + e));

                    if (!result) {
                        throw new WebApplicationException("invalid extension");
                    }

                    byte[] bytes;

                    try {
                        bytes = Files.readAllBytes(photo.filePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return new Photos(productId,bytes,photo.contentType(),photo.fileName());

                })).onItem().transformToUni(i->Panache.withTransaction(()->Photos.persist(i)).onItem().transform(p -> Response.status(Response.Status.ACCEPTED).build()));
    }

    @GET
    @PermitAll
    @Path("/getphoto/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getPhotos(@PathParam("productId") Long productId) {
        return Photos.list(productId).map(Unchecked.function(photos -> {

                    return Response.ok().entity(photos).type(MediaType.APPLICATION_JSON_TYPE).build();
                })
        );


    }

    @GET
    @PermitAll
    @Path("/getRandomPhoto/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getRandomPhoto(@PathParam("productId") Long productId) {
        return Photos.list(productId).map(Unchecked.function(photos -> {
                    List<byte[]> bytePhotos = new ArrayList<>();
                    photos.stream().map(Photos::getPhoto).forEach(bytePhotos::add);
                    List<BufferedImage> img = new ArrayList<>();
                    Random random = new Random();
                    int randomIndex = random.nextInt(photos.size());
                    return Response.ok().entity(bytePhotos.get(randomIndex)).type(photos.get(randomIndex).getFormat()).build();
                })
        );
    }


    @DELETE
    @PermitAll
    @Path("/deletePhoto")

    public Uni<Response> deletePhoto(@QueryParam("photoName")String photoName) {

        return Panache.withTransaction(()->Photos.delete("photoName",photoName)).onItem().transform(result->{
            if(result==0){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return  Response.status(Response.Status.ACCEPTED).build();
        });
    }

}
