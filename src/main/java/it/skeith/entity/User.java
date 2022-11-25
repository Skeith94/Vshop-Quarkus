package it.skeith.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@NamedQueries({
        @NamedQuery(name = "User.getByNameEmail", query = "select count(u) from User u where u.username=:username or u.email=:email"),
})

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length =15)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(unique = true,nullable = false)
    private String email;

    private String role="User";
    @Column(nullable = false,length = 15)
    private LocalDate birth;
    @Column(nullable = false,length = 15)
    private String name;
    @Column(nullable = false,length = 15)
    private String surname;

    @Column(nullable = false,length = 30)
    private String address;
    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    private boolean enabled = true;

    private String reason;



    public User(String username, String password, String email, String name, String surname, LocalDate birth, String address,Country country){
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.birth = birth;
        this.address = address;
        this.country=country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }




}
