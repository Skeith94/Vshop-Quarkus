package it.skeith.service;

import it.skeith.repo.CategoryRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepo categoryRepo;


}
