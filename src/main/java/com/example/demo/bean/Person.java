package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Liu YingJie
 * @date 2021-10-28 16:14
 * @return
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Person {


    /**
     * personId : b3c4c3e6687f48acb4d58dac6dc9fa5b
     * personName : sadmin01
     * personType : 1
     * jobNo : 34444
     */

    private String personId;
    private String personName;
    private String jobNo;
    private String orgIndexCode;
    private List personPhoto;
}
