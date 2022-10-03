package com.intellias.intellistart.interviewplanning.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    private Long id;
    private String email;
    private String password;
}
