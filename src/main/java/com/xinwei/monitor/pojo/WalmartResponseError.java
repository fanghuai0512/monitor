package com.xinwei.monitor.pojo;

import lombok.Data;

import java.util.List;

@Data
public class WalmartResponseError {

    private List<WalmartError> error;

}
