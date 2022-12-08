package it.skeith.controller;


import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.Category;
import it.skeith.entity.Photos;
import it.skeith.entity.Role;
import it.skeith.entity.SubCategory;
import it.skeith.payload.response.CategorySubCatResponse;
import it.skeith.service.CategoryService;
import it.skeith.service.SubCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.function.Supplier;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;


@Slf4j
@Path("/category")
@ApplicationScoped
public class CategoryController {

    @Inject
    CategoryService categoryService;
    @Inject
    SubCategoryService subCategoryService;

    @POST()
    @Path("/save/category")
    public Uni<Response> SaveCategory(@QueryParam("name") String name) {

        Category category = new Category(name.trim().toLowerCase());
        return Panache.withTransaction(() -> categoryService.existCategory(name.trim().toLowerCase()))
                .onItem().ifNotNull().transform(e -> Response.status(Response.Status.BAD_REQUEST).entity("category already exist").type(TEXT_PLAIN_TYPE).build())
                .onItem().ifNull().switchTo(Panache.withTransaction(() -> categoryService.SaveCategory(category)).onItem().transform(c -> Response.status(Response.Status.ACCEPTED).entity(c).build()));

    }

    @POST()
    @Path("/save/sub-category")
    public Uni<Response> SaveSubCategory(@QueryParam("name") String name) {

        SubCategory subCategory=new SubCategory(name.trim().toLowerCase());

        return Panache.withTransaction(()->categoryService.existCategory(name.trim().toLowerCase()))
                .onItem().ifNotNull().transform(e -> Response.status(Response.Status.BAD_REQUEST).entity("you can't use category name for subcategory").type(TEXT_PLAIN_TYPE).build())
                .onItem().ifNull().switchTo(
                        Panache.withTransaction(()->subCategoryService.existSubCategory(name.trim().toLowerCase()))
                                .onItem().ifNotNull().transform(e->Response.status(Response.Status.BAD_REQUEST).entity("category already exist").type(TEXT_PLAIN_TYPE).build())
                                .onItem().ifNull().switchTo(Panache.withTransaction(()->subCategoryService.SaveSubCategory(subCategory).onItem().transform(c -> Response.status(Response.Status.ACCEPTED).entity(c).build())))

                );
    }


    @ReactiveTransactional
    @POST
    @Path("/addSubtoCategory/{categoryId}")
    public Uni<Response>AddSubtoCategory(@PathParam("categoryId") Long categoryId,@QueryParam("subCategoryId")List<Long> subCategoryId){
        Uni<Category>category= Panache.withTransaction(()->categoryService.findById(categoryId)).onItem().ifNull().failWith(new WebApplicationException("category not found"));
        Uni<List<SubCategory>> subCategorys = Panache.withTransaction(() -> subCategoryService.findByIds(subCategoryId)).onItem().transform(sub->{
            if (sub.isEmpty()){
                throw new  WebApplicationException("no sub category found");
            }
            return sub;
        });

        return  Uni.combine().all().unis(category,subCategorys).asTuple().onItem().transform(tuple->{
              tuple.getItem1().setSubCategories(tuple.getItem2());
            CategorySubCatResponse categorySubCatResponse=new CategorySubCatResponse(tuple.getItem1().getId(),tuple.getItem1().getName(),tuple.getItem1().getSubCategories());
            return Response.ok().entity(categorySubCatResponse).build();

        });

    }


    @GET
    @Path("/getCategorySub/{categoryId}")
    public Uni<Response>GetCategory(@PathParam("categoryId") Long categoryId) {
        return Panache.withTransaction(()->categoryService.findById(categoryId)).onItem().transform(c->{
            CategorySubCatResponse categorySubCatResponse=new CategorySubCatResponse(c.getId(),c.getName(),c.getSubCategories());
           return Response.ok().entity(categorySubCatResponse).build();
        });
    }








}




