package it.skeith.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryProductResponse {
    public Long subCategoryId;
    public String subCategoryName;
    public Long productId;
}
