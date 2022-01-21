package com.shilin.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-24 11:49:38
 */
public class Cart {
    private List<CartItem> items;
    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量
    private BigDecimal countAmount;//商品总价
    private BigDecimal reduce = new BigDecimal("0.00");//商品优惠价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        this.countNum = 0;
        if (this.items != null && this.items.size() > 0){
            for (CartItem item : this.items) {
                this.countNum += item.getCount();
            }
        }
        return countNum;
    }

    public Integer getCountType() {
        this.countType = 0;
        if (this.items != null && this.items.size() > 0){
            for (CartItem item : this.items) {
                this.countType += 1;
            }
        }
        return countType;
    }

    public BigDecimal getCountAmount() {
        this.countAmount = new BigDecimal("0.00");
        if (this.items != null && this.items.size() > 0){
            for (CartItem item : this.items) {
                if (item.getCheck()) {
                    BigDecimal totalPrice = item.getTotalPrice();
                    this.countAmount = this.countAmount.add(totalPrice);
                }
            }
        }
        this.countAmount = this.countAmount.subtract(this.getReduce());
        return countAmount;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
