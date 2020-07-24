package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @Length(min = 4,max = 30,message = "用户名长度必须在4-30之间")
    private String username;// 用户名

    @JsonIgnore
    @Column(name = "password")
    @Length(min = 4,max = 30,message = "密码长度必须在8-16之间")
    private String password;// 密码

    @Column(name = "phone")
    @Pattern(regexp = "^1[35789]\\d{9}$",message = "手机号不符合标准")
    private String phone;// 电话


    @Column(name = "created")
    private Date created;// 创建时间

    @JsonIgnore
    @Column(name = "salt")
    private String salt;// 密码的盐值
}