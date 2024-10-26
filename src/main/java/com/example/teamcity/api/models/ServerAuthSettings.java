package com.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerAuthSettings extends BaseModel {
    @Builder.Default
    private String guestUsername = "guest";
    @Builder.Default
    private Boolean perProjectPermissions = true;
    @Builder.Default
    private String twoFactorAuthenticationType = "OPTIONAL";
    private Modules modules;
}
