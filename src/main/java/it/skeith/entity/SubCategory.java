package it.skeith.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@NamedQuery(name = "SubCategory.getByProductId", query = "select new it.skeith.payload.response.GetByProductIdResponse(s,p.id) from SubCategory s left join s.products p  where p.id IN :ids and s.visible=true")
@NamedQueries({
        @NamedQuery(name = "SubCategory.getSubCategoryIds", query = "select s from SubCategory s where s.id IN :ids and s.visible=true"),
})

public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true,length =15)
    String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "subCategory",fetch = FetchType.LAZY)
    Set<Product> products= new HashSet<>();


    @JsonIgnore
    boolean visible=true;


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
