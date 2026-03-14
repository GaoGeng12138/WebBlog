package com.gaog.weblog.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Custom UserDetails implementation to store additional user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * Avatar URL
     */
    private String avatar;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Email
     */
    private String email;

    /**
     * Introduction/Bio
     */
    private String introduction;

    /**
     * Social media links
     */
    private String githubUrl;
    private String twitterUrl;
    private String weiboUrl;

    /**
     * User roles
     */
    private Collection<String> roles;

    /**
     * Account enabled status
     */
    private boolean enabled = true;

    /**
     * Account non-expired status
     */
    private boolean accountNonExpired = true;

    /**
     * Credentials non-expired status
     */
    private boolean credentialsNonExpired = true;

    /**
     * Account non-locked status
     */
    private boolean accountNonLocked = true;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
