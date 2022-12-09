package it.skeith.repo;


import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.Product;
import it.skeith.payload.response.GetByCategoryResponse;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductRepo implements PanacheRepository<Product> {

    SessionFactory sf;
    public ProductRepo(SessionFactory sf) {
        this.sf = sf;
    }



    public Uni<Product>save(Product product){
        return persist(product);
    }

    public Uni<Product> findByname(String name){
        return find("name",name).firstResult();
    }

    public Uni<Product> getById(Long id) {
        return find("#Product.getById",Parameters.with("id",id).map()).firstResult();
    }


    public Uni<List<GetByCategoryResponse>> getByCategory(Long id) {
        return sf.withTransaction((s,t)->s.createQuery(
                        "select new it.skeith.payload.response.GetByCategoryResponse(p.id, p.name, p.description, p.price, p.discount, p.quantity,c.id,c.name) from Product p join  p.category c where c.id = :id and p.visible = true",GetByCategoryResponse.class)
                .setParameter("id",id)
                .getResultList());
    }
}

