package it.skeith.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class LoginRequest {
    //@NotBlank @Email
    String email;
    @NotBlank
    String password;
}
