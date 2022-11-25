package it.skeith.payload.request;


import io.smallrye.common.constraint.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.jboss.resteasy.reactive.DateFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter @Setter
public class RegistrerRequest {
    @Size(min=4,max = 15)
    private String username;
    @NotBlank
    private String password;
    @Email
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
