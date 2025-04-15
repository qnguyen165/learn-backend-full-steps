package com.qnguyendev.backendservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Group")
@Table(name = "tbl_group")
public class Group extends AuditableEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "group")
    private Set<GroupHasUser> users = new HashSet<>();
}
