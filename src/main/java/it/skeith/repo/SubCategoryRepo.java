package it.skeith.repo;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.SubCategory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;


@ApplicationScoped
public class SubCategoryRepo implements PanacheRepository<SubCategory> {

    public Uni<List<SubCategory>> GetBySubCategoryIds(List<Long> subcategoryIds){
        return  list("#SubCategory.getSubCategoryIds", Parameters.with("ids",subcategoryIds));
    }



}
