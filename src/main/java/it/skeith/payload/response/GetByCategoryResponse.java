package it.skeith.payload.response;

import it.skeith.entity.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
public class GetByCategoryResponse {

    private Long id;

    private String name;
    private String description;
    private Float price;
    private Long discount;

    private Long quantity;

    private Long categoryId;
    private String categoryName;

    private Set<SubCategory> subCategory= new HashSet<>();

    public GetByCategoryResponse(Long id, String name, String description, Float price, Long discount, Long quantity, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
