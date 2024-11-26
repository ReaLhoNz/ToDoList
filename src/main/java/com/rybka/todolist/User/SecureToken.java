package com.rybka.todolist.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "secure_Tokens")
@Getter
@Setter
public class SecureToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp timeStamp;

    @Column(updatable = false, nullable = false)
    private LocalDateTime expireAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Transient
    public boolean isExpired() {
        return expireAt.isBefore(LocalDateTime.now());
    }


}
