package com.openclassrooms.mddapi.dtos.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class AuthRefreshResponse {
    String token;
}
