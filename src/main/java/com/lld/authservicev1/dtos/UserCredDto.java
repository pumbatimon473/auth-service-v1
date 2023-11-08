package com.lld.authservicev1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCredDto {
    // Fields
    private String email;
    private String password;

}
