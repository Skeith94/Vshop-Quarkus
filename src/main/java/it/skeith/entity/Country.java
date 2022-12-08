package it.skeith.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Country extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 2)
    private String iso;

    @Column(length = 80)
    private String name;
    @Column(length = 80)
    private String niceName;

    @Column(length = 3)
    private String iso3;

    private short numCode;

    private short phoneCode;

    public Country(String iso, String name, String niceName, String iso3, short numCode, short phoneCode) {
        this.iso = iso;
        this.name = name;
        this.niceName = niceName;
        this.iso3 = iso3;
        this.numCode = numCode;
        this.phoneCode = phoneCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        return id.equals(country.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static Uni<Country> findById(Long id){
        return  find("id",id).firstResult();
    }
}