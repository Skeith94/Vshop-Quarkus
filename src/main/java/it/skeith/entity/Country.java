package it.skeith.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;

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

    public static Uni<Country> findById(Long id){
        return  find("id",id).firstResult();
    }
}