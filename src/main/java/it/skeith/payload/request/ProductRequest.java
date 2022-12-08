package it.skeith.payload.request;

import it.skeith.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class ProductRequest {
   @NotBlank @Size(min=1,max =15)
   private String name;
   @NotBlank @Size(min = 3)
   private String description;
   @Positive
   private Float price;
   @Positive @NotEmpty
   private Long discount;
   @Min(1)
   private Long quantity;
   @Min(1)
   private Long categoryId;
   @Min(1) @NotEmpty
   private List<Long> subCategoryId;
}
