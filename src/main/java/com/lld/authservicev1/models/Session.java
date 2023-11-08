package com.lld.authservicev1.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Session extends BaseModel {
    // Fields
    @ManyToOne
    private User user;
    private String token;
}
