package com.billeasy.billeasymoviedb.api.model;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Configuration {

    @JsonProperty("images")
    public Images images;
    @JsonProperty("change_keys")
    public List<String> changeKeys = null;

}
