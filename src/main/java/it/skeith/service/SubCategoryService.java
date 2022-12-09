package it.skeith.service;


import io.smallrye.mutiny.Uni;
import it.skeith.entity.SubCategory;
import it.skeith.repo.SubCategoryRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SubCategoryService {

    @Inject  SubCategoryRepo subCategoryRepo;

    public Uni<SubCategory> SaveSubCategory(SubCategory subCategory) {
        return subCategoryRepo.persist(subCategory);
    }

    public Uni<SubCategory> existSubCategory(String name) {
        return subCategoryRepo.find("name", name).firstResult();

    }

    public Uni<List<SubCategory>> findByIds(List<Long> subCategoryId) {
        return subCategoryRepo.GetBySubCategoryIds(subCategoryId);
    }




}
