package com.qnguyendev.backendservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Role")
@Table(name = "tbl_role")
public class Role extends AuditableEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<RoleHasPermission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<UserHasRole> users = new HashSet<>();
}
