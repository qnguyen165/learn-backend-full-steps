package com.qnguyendev.backendservice.dto.request;

import com.qnguyendev.backendservice.common.Gender;
import com.qnguyendev.backendservice.common.UserType;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest implements Serializable {
    String firstName;
    String lastName;
    Gender gender;
    Date birthday;
    String username;

    @Email()
    String email;
    String phone;
    UserType type;
    List<AddressRequest> addresses;
}
