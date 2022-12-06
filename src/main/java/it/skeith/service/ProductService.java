package it.skeith.service;

import io.smallrye.mutiny.Uni;
import it.skeith.entity.Product;
import it.skeith.repo.ProductRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProductService {

    @Inject ProductRepo productRepo;

    public Uni<Product>persist(Product product){
        return productRepo.persist(product);
    }


    public Uni<Product>findByname(String name){
        return productRepo.findByname(name);
    }

    public Uni<Product>findById(Long id){
        return productRepo.findById(id);
    }



}
