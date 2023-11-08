package com.lld.authservicev1.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseModel {
    // Fields
    private String fullName;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Session> activeSessions;
}
