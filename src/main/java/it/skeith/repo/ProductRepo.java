package it.skeith.repo;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.Product;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepo implements PanacheRepository<Product> {

    public Uni<Product>save(Product product){
        return persist(product);
    }

    public Uni<Product> findByname(String name){
        return find("name",name).firstResult();
    }


}
