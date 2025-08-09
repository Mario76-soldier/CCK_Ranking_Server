package com.cubingclub.ranking.RsaKeys.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class RsaKeys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(nullable = false, unique = true)
    private String keyId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String privateKey;
}
