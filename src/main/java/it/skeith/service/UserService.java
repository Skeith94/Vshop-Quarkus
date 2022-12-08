package it.skeith.service;


import io.smallrye.mutiny.Uni;
import it.skeith.entity.User;
import it.skeith.repo.UserRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class UserService {
    @Inject
    UserRepo userRepo;


    public Uni<User> getUserRepo(String username) {
        return userRepo.findByUsername(username);
    }


    public Uni<Long>getByNameEmail(String email){
        return userRepo.getByNameEmail(email);
    }


    public Uni<User> persistFlushUser(User user){
        return userRepo.persistAndFlush(user);
    }

    public Uni<User> getByEmail(String email) {
        return userRepo.getByEmail(email);
    }

    public Uni<User> findById(Long id) {
        return userRepo.getByid(id);
    }

    public Uni<User> saveorUpdate(User user) {
       return userRepo.saveorUpdate(user);
    }
}
