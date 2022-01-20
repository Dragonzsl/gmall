/**
  * Copyright 2020 bejson.com
  */
package com.shilin.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Auto-generated: 2020-10-21 11:56:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MemberPrice implements Serializable {
    private static final long serialVersionUID = 3251433309384753039L;
    private Long id;
    private String name;
    private BigDecimal price;


}
