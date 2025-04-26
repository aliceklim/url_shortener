package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "hash")
public class Hash {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public String hash;
}
