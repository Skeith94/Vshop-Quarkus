package it.skeith.payload.response;

import it.skeith.entity.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryProductResponse {
    public Long subCategoryId;
    public String subCategoryName;
    public Long productId;
}
