package net.fullstack7.swc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
@Entity
public class Admin {
    @Id
    @Column(length = 20, columnDefinition = "VARCHAR(20) COMMENT '아이디'")
    private String id;
    @Column(length = 128, columnDefinition = "VARCHAR(128) COMMENT '비밀번호'")
    private String password;
}
