package it.skeith.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateProductRequest {

    @Size(min = 3,max=15)
    private String name=null;
    @Size(min=3)
    private String description=null;
    @Positive
    private Float price=null;
    @Positive
    private Long discount=null;

    @Positive
    private Long quantity=null;

    @Min(1)
    private Long categoryId=null;

    private List<Long> subCategoryId=null;
}
