package it.skeith.repo;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.SubCategory;
import it.skeith.payload.response.SubCategoryProductResponse;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;


@ApplicationScoped
public class SubCategoryRepo implements PanacheRepository<SubCategory> {

    Mutiny.SessionFactory sf;
    public SubCategoryRepo(Mutiny.SessionFactory sf) {
        this.sf = sf;
    }

    public Uni<List<SubCategory>> GetBySubCategoryIds(List<Long> subcategoryIds){
        return  list("#SubCategory.getSubCategoryIds", Parameters.with("ids",subcategoryIds));
    }

    public Uni<List<SubCategoryProductResponse>>getSubCategoryProduct(List<Long> ids){
        return  sf.withTransaction((s,t)->s.createQuery(
                        "select new it.skeith.payload.response.SubCategoryProductResponse(s.id,s.name,p.id) from SubCategory s join s.products p  where p.id in :ids ",SubCategoryProductResponse.class)
                .setParameter("ids",ids)
                .getResultList());

    }


}
