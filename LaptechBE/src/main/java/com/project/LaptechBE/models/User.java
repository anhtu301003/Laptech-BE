package com.project.LaptechBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.LaptechBE.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexOptions;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "users")
@Validated
public class User implements UserDetails {
    @Id
    @Field("_id")
    @JsonProperty("_id")  // Giữ tên trường là _id trong JSON trả về
    @JsonSerialize(using = ToStringSerializer.class)  // Chuyển ObjectId thành chuỗi khi trả về
    private ObjectId id;

    @NotBlank
    private String name;

    private Date birthDate;

    @NotBlank
    @Email
    @Indexed(unique = true)
    private String email;
    @NotBlank
    private String password;
    private String avatar;
    private String phone;

    @Builder.Default
    private Boolean isAdmin = false;

    @Builder.Default
    private ArrayList<Address> addresses = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(isAdmin){
            return List.of(new SimpleGrantedAuthority(RoleEnum.ADMIN.name()));
        }
        else{
            return List.of(new SimpleGrantedAuthority(RoleEnum.USER.name()));
        }
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
