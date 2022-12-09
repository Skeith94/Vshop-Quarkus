package it.skeith.service;

import io.smallrye.mutiny.Uni;
import it.skeith.entity.Product;
import it.skeith.payload.response.GetByCategoryResponse;
import it.skeith.repo.ProductRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject ProductRepo productRepo;

    public Uni<Product>persist(Product product){
        return productRepo.persist(product);
    }


    public Uni<Product>findByname(String name){
        return productRepo.findByname(name);
    }


    public Uni<Product>getById(Long id){
        return productRepo.getById(id);
    }


    public Uni<List<GetByCategoryResponse>> getByCategory(Long id) {
        return productRepo.getByCategory(id);
    }
}
