package com.example.teamcity.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Module extends BaseModel {
    @Builder.Default
    private String name = "HTTP-Basic";
    private Properties properties;
}
