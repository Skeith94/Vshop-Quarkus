package it.skeith.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
    public Long subCategoryId;
    public String subCategoryName;

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
