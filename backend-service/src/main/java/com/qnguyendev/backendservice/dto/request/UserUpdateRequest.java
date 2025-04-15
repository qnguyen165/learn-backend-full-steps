package com.qnguyendev.backendservice.dto.request;

import com.qnguyendev.backendservice.common.Gender;
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
public class UserUpdateRequest implements Serializable {
    Long id;
    String firstName;
    String lastName;
    Gender gender;
    Date birthday;
    String username;
    String email;
    String phone;
    List<AddressRequest> addresses;
}
