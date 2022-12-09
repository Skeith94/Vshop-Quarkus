package it.skeith.payload.response;


import it.skeith.entity.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter @Getter @NoArgsConstructor

public class CategorySubCatResponse {
    private Long id;
    String name;
    private Set<SubCategory> subCategories ;

    public CategorySubCatResponse(Long id, String name, Set<SubCategory> subCategories) {
        this.id = id;
        this.name = name;
        this.subCategories = subCategories;
    }
}
