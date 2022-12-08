package it.skeith.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@NamedQueries({
        @NamedQuery(name = "SubCategory.getSubCategoryIds", query = "select s from SubCategory s where s.id IN :ids and s.visible=true")
})

public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true,length =15)
    String name;

    @JsonIgnore
    boolean visible=true;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="Product_SubCategory",
            joinColumns = {@JoinColumn(name="subCategory_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="Product_id", referencedColumnName = "id")}
    )
    private Set<Product>products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubCategory that = (SubCategory) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public SubCategory(String name) {
        this.name = name;
    }
}
