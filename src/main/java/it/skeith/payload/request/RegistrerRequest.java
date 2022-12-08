package it.skeith.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter @Setter
public class RegistrerRequest {
 //   @Size(min=4,max = 15)
    private String username;
    @NotBlank
    private String password;
   // @Email
    private String email;
    @PastOrPresent
    private LocalDate birth;
    @Size(min=3,max = 15)
    private String name;
    @Size(min=3,max = 15)
    private String surname;
    @Size(min=3,max = 30)
    private String address;
    @Positive
    private Long country_id;


}
