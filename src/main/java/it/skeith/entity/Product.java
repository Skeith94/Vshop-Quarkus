package it.skeith.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.skeith.payload.request.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique =true,length =15)
    private String name;

    @Column(columnDefinition="TEXT")
    private String description;
    @Column(nullable=false)
    private Float price;
    private Long discount;
    @Column(nullable=false)
    private Long quantity;
    @JsonIgnore
    private boolean visible=true;
    @Column(updatable = false)
    private LocalDateTime insertAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="Product_SubCategory",
            joinColumns = {@JoinColumn(name="Product_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="subCategory_id", referencedColumnName = "id")}
    )
    Set<SubCategory> subCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Product(String name, String description, Float price, Long quantity, Category category,Long discount,Set<SubCategory>subCategories) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.insertAt= LocalDateTime.now();
        this.discount=discount;
        this.subCategory=subCategories;
    }

    public void  updateProduct(UpdateProductRequest request){
        if(request.getName()!=null){
            this.name=request.getName();
        }
        if(request.getDescription()!=null){
            this.description=request.getDescription();
        }
        if(request.getPrice()!=null){
            this.price=request.getPrice();
        }
        if(request.getQuantity()!=null){
            this.quantity=request.getQuantity();
        }
        if(request.getDiscount()!=null){
            this.discount=request.getDiscount();
        }
    }

}
