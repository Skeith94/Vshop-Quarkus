package it.skeith.service;

import io.smallrye.mutiny.Uni;
import it.skeith.entity.Category;
import it.skeith.repo.CategoryRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepo categoryRepo;

    public Uni<Category> SaveCategory(Category category) {
        return categoryRepo.save(category);
    }

    public Uni<Category> persistAndFlush(Category category) {
        return categoryRepo.persistAndFlush(category);
    }

    public Uni<Category> existCategory(String name) {
        return categoryRepo.find("name", name).firstResult();

    }

    public Uni<Category>findById(Long categoryId) {
        return categoryRepo.getById(categoryId);
    }


}
