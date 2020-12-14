package com.test.springboot.activiti.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 变量值实体列
 */
@Data
public class VariableEntity implements Serializable {

    private String id;
    private String name;
    private double days;

    public VariableEntity() {
    }

    public VariableEntity(String id, String name, double days) {
        this.id = id;
        this.name = name;
        this.days = days;
    }
}
