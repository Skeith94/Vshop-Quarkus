package it.skeith.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class ProductRequest {
    @Size(min=1,max =15)
   private String name;
   @NotBlank @Size(min = 3)
   private String description;
   @Positive
   private Float price;

   private Long discount;
   @Min(1)
   private Long quantity;
   @Min(1)
   private Long categoryId;

   private List<Long> subCategoryId;
}
