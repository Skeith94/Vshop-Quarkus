package it.skeith.repo;


import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import it.skeith.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PostPersist;


@ApplicationScoped
public class UserRepo  implements PanacheRepository<User>  {
    public Uni<User> findByUsername(String username){
        return find("username",username).firstResult();
    }

    public Uni<User>getByEmail(String email){
        return find("email",email).firstResult();
    }

    public Uni<Long> getByNameEmail(String username, String email){
        return count("#User.getByNameEmail", Parameters.with("username",username).and("email",email).map());
    }








}
