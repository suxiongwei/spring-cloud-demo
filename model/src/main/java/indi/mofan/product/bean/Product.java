package indi.mofan.product.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiongweisu
 * @date 2025/3/23 17:24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private Long id;
    private BigDecimal price;
    private String productName;
    private int num;
    private String port;
}
