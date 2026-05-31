package com.nhpdev.backendservicesecond.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "permissions")
public class Permission extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

}
