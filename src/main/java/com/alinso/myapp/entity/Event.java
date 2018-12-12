package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Event extends BaseEntity {

    @Column
    @NotBlank(message = "Konu yazmalısınız")
    private String detail;

    @OneToOne
    @NotNull
    private User creator;

    @OneToMany
    private List<User> attendants;

}
