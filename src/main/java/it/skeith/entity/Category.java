package it.skeith.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor



@NamedQueries({
        @NamedQuery(name = "Category.getByIdVisible", query = "select c from Category c left join fetch c.subCategories  where c.id=:categoryId  and c.visible=true"),
        @NamedQuery(name = "Category.getBySubId", query = "select c from Category c left join c.subCategories cs where cs.id=:id")
})

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true, length = 15)
    String name;
    @JsonIgnore
    private boolean visible = true;


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
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
