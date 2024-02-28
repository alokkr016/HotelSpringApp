package com.cn.hotel.dto;

import lombok.*;

import java.util.Collection;
import java.util.Collections;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String userId;
    private String accessToken;
    private long expireAt;
    private Collection<String> authorities;
}
