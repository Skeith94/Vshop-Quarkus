package it.skeith.repo;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import it.skeith.entity.Category;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryRepo implements PanacheRepository<Category> {

}
