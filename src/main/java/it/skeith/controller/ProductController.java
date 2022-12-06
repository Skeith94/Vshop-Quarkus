package it.skeith.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.Category;
import it.skeith.entity.Photos;
import it.skeith.entity.Product;
import it.skeith.payload.request.ProductRequest;
import it.skeith.service.CategoryService;
import it.skeith.service.ProductService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@ApplicationScoped
@Path("/product")
public class ProductController {
    @Inject
    CategoryService categoryService;
    @Inject
    ProductService productService;

    @ConfigProperty(name = "image.size")
    Long imageSize;
    @ConfigProperty(name = "image.extensions")
    List<String> extensions;

    @Path("/save")
    @POST
    @ReactiveTransactional
    public Uni<Response> save(@RequestBody @Valid ProductRequest request) {

        Uni<Product> findName = Panache.withTransaction(() -> productService.findByname(request.getName())).onItem().ifNotNull().failWith(new WebApplicationException("product name already use"));

        Uni<Category> findCategory = Panache.withTransaction(() -> categoryService.findById(request.getCategoryId())).onItem().ifNull().failWith(new WebApplicationException("category not found"));

        return Uni.combine().all().unis(findName, findCategory).asTuple().onItem().transform(tuple -> {
            Product product=new Product(request.getName(),request.getDescription(),request.getPrice(),request.getQuantity(),tuple.getItem2());
           return product;

        }).onItem().transformToUni(product->Panache.withTransaction(()->productService.persist(product)).onItem().transform(p->Response.status(Response.Status.CREATED).entity(p.getId()).build()));
    }




    @Schema(type = SchemaType.STRING, format = "binary")
    public static class UploadItemSchema {
    }

    @PermitAll
    @Path("/addphotos/{productId}")
    @POST()
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> addPhotos(@PathParam("productId")Long productId, @RestForm @Schema(implementation = UploadItemSchema.class)  FileUpload photo) throws IOException {

        if (photo.size() > imageSize) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity("image max size is " + imageSize).type(MediaType.TEXT_PLAIN_TYPE).build());
        }

        boolean result = extensions.stream().anyMatch(e -> photo.contentType().contentEquals("image/" + e));

        if (!result) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity(photo.contentType() + " not allowed").type(MediaType.TEXT_PLAIN_TYPE).build());
        }

        byte[] bytes = Files.readAllBytes(photo.filePath());
        Photos photos=new Photos(productId,bytes);

        return Panache.withTransaction(()->Photos.persist(photos)).onItem().transform(i->Response.ok().build());

    }









}
