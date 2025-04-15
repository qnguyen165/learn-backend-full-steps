package com.qnguyendev.backendservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPasswordRequest implements Serializable {

    @NotNull(message = "id must be not null")
    Long id;

    @NotBlank(message = "password must be not null")
    String password;

    @NotBlank(message = "confirm password must be not null")
    String confirmPassword;
}
