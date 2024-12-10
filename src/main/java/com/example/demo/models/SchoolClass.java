package com.example.demo.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class SchoolClass {
    private Integer id;
    private String name;
    private List<Classes> classes = new ArrayList<>();

    public void AddClass(Classes classes1){
        if (classes == null) classes = new ArrayList<>();
        classes.add(classes1);
    }
}
