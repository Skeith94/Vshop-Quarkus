package it.skeith.payload.request;

import it.skeith.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class ProductRequest {
   @NotBlank @Size(min=1,max =15)
   private String name;
   @NotBlank @Size(min = 3)
   private String description;
   @Positive
   private Long price;
   @Min(1)
   private Long quantity;
   @Min(1)
   private Long categoryId;
}
