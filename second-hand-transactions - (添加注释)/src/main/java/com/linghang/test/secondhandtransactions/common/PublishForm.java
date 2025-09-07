package com.linghang.test.secondhandtransactions.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Data;

import java.util.Date;
@Data
public class PublishForm {

    private String name;
    private Integer price;
    private Date date;
    private String contact;
    private Integer status;
    private Integer type;
    private String introduce;
    private String imagePath;

}
