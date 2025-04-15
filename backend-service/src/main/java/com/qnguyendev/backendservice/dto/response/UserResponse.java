package com.qnguyendev.backendservice.dto.response;

import com.qnguyendev.backendservice.common.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date birthday;
    private String username;
    private String email;
    private String phone;
    // more
}
