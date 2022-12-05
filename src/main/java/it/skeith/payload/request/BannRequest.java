package it.skeith.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class BannRequest {
    @Email
  private String email;
    @NotBlank
  private String reason;
}
