package com.xinwei.monitor.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface Status {

    @Getter
    @AllArgsConstructor
    public enum Product{
        Delete(0,"不监控"),
        Up(1,"上架"),
        Down(2,"下架");

        private Integer status ;
        private String msg ;
    }

}
