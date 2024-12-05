package com.tango.experiment.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 6207115757117125450L;
    private Integer userId;
    private String username;
    private String password;
    private String role;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
