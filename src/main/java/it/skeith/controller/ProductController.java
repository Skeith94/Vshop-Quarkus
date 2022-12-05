package it.skeith.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.skeith.entity.Category;
import it.skeith.entity.Product;
import it.skeith.payload.request.ProductRequest;
import it.skeith.service.CategoryService;
import it.skeith.service.ProductService;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/product")
public class ProductController {
    @Inject
    CategoryService categoryService;
    @Inject
    ProductService productService;

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

}
