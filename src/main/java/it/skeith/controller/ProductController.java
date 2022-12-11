package it.skeith.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.Category;
import it.skeith.entity.Product;
import it.skeith.entity.SubCategory;
import it.skeith.payload.request.ProductRequest;
import it.skeith.payload.request.UpdateProductRequest;
import it.skeith.payload.response.GetByCategoryResponse;
import it.skeith.repo.ProductRepo;
import it.skeith.service.CategoryService;
import it.skeith.service.ProductService;
import it.skeith.service.SubCategoryService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Path("/product")
public class ProductController {
    @Inject
    CategoryService categoryService;
    @Inject
    ProductService productService;

    @Inject
    ProductRepo productRepo;

    @Inject
    SubCategoryService subCategoryService;

    @ConfigProperty(name = "image.size")
    Long imageSize;
    @ConfigProperty(name = "image.extensions")
    List<String> extensions;

    @Path("/save")
    @POST
    @ReactiveTransactional
    public Uni<Response> save(@Valid @RequestBody ProductRequest request) {

        Uni<Product> findName = Panache.withTransaction(() -> productService.findByname(request.getName().trim().toLowerCase()))
                .onItem().ifNotNull()
                .failWith(new WebApplicationException("product name already use"));
        Uni<Category> findCategory = Panache.withTransaction(() -> categoryService.findById(request.getCategoryId()))
                .onItem()
                .ifNull()
                .failWith(new WebApplicationException("category not found"));

        return Uni.combine()
                .all()
                .unis(findName, findCategory)
                .asTuple().
                onItem()
                .transform(tuple ->
                {
                    Set<SubCategory> subCategories = new HashSet<>();
                     for (Long id : request.getSubCategoryId())
                         tuple.getItem2().getSubCategories().stream().filter(s -> s.getId().longValue() == id.longValue()).forEach(subCategories::add);

                     return new Product(request.getName().trim().toLowerCase(), request.getDescription(), request.getPrice(), request.getQuantity(), tuple.getItem2(), request.getDiscount(), subCategories);
                })
                .onItem()
                .transformToUni(product -> Panache.withTransaction(() -> productService.persist(product))
                        .onItem()
                        .transform(p -> Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON_TYPE).entity(p).build()));
    }

    @Path("/update/{productId}")
    @PATCH
    @ReactiveTransactional
    public Uni<Response> updateProduct(@RequestBody @Valid UpdateProductRequest request, @PathParam("productId") Long id) {

        Uni<Product> productUni = Panache.withTransaction(() -> productService.getById(id))
                .onItem()
                .ifNull()
                .failWith(new WebApplicationException("product no found"));

        if (request.getCategoryId() != null) {
            Uni<Category> categoryUni = Panache.withTransaction(() -> categoryService.findById(request.getCategoryId()))
                    .onItem()
                    .ifNull()
                    .failWith(new WebApplicationException("category not found"));

            return Uni.combine().all().unis(productUni, categoryUni).asTuple().onItem().transform(tuple -> {

                tuple.getItem1().setCategory(tuple.getItem2());
                Set<SubCategory> subCategories = new HashSet<>();

                for (Long i : request.getSubCategoryId())
                    tuple.getItem2().getSubCategories().stream().filter(s -> s.getId().longValue() == i.longValue()).forEach(subCategories::add);

                tuple.getItem1().setSubCategory(subCategories);
                return Response.ok().entity(tuple.getItem1()).type(MediaType.APPLICATION_JSON_TYPE).build();
            });
        }

        return productUni.onItem().ifNotNull().transform(product -> {
            product.setName(request.getName());
            product.updateProduct(request);
            return Response.ok().entity(product).type(MediaType.APPLICATION_JSON_TYPE).build();
        });
    }

    @GET
    @Path("/GetByid/{id}")
    public Uni<Response> getProduct(@PathParam("id") Long id) {

        return Panache.withTransaction(() -> productService.getById(id)
                .onItem()
                .ifNull()
                .failWith(new WebApplicationException("product not found"))
                .onItem()
                .ifNotNull()
                .transform(product -> Response.ok().entity(product).type(MediaType.APPLICATION_JSON_TYPE).build()));
    }


    @GET
    @Path("/getByCategory/{CategoryId}")
    public Uni<Response> getByCategory(@PathParam("CategoryId") Long id) {

        List<GetByCategoryResponse>products = new ArrayList<>();

        return Panache.withTransaction(() -> productService.getByCategory(id))
                .onItem()
                .ifNull()
                .failWith(new WebApplicationException("product not found"))
                .onItem()
                .ifNotNull()
                .transform(p ->{
                    List<Long>ids=new ArrayList<>();
                    p.forEach(i->ids.add(i.getId()));
                    products.addAll(p);
                    return ids;
                })
                .onItem()
                .transformToUni(i -> Panache.withTransaction(() -> subCategoryService.getSubCategoryProduct(i))
                        .onItem()
                        .transform(subCategoryProductResponses -> {
                            products.forEach(product -> subCategoryProductResponses.stream()
                                    .filter(sub -> product.getCategoryId().longValue() == sub.productId.longValue())
                                    .forEach(result -> {
                                        product.setSubCategoryId(result.getSubCategoryId());
                                        product.setSubCategoryName(result.subCategoryName);
                                    }));
                            return products;
                        })
                ).onItem().transform(p->Response.ok().entity(p).type(MediaType.APPLICATION_JSON_TYPE).build());

    }

    @GET
    @Path("/getBySubcategory/")
    public Uni<Response>getBySubcategory(@QueryParam("SubcategoryId") List <Long> ids){
        return Panache.withTransaction(()->productService.GetBySubCategoryResponse(ids)).onItem().transform(i->Response.ok().entity(i).build());

    }





}

