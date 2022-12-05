package it.skeith.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jboss.logging.annotations.Field;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor



@NamedQueries({
        @NamedQuery(name = "Category.getByIdVisible", query = "select c from Category c where c.id=:categoryId and c.visible=true")
})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true, length = 15)
    String name;
    private boolean visible = true;

    @OneToMany(fetch = FetchType.LAZY)
    Set<Product>products=new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<SubCategory> subCategories = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Category(String name) {
        this.name = name;
    }

    public void setSubCategories(List<SubCategory>subCategoriesAdd){
        this.subCategories.addAll(subCategoriesAdd);
    }



    public Category(String name, Set<SubCategory> subCategories) {
        this.name = name;
        this.subCategories = subCategories;
    }
}
