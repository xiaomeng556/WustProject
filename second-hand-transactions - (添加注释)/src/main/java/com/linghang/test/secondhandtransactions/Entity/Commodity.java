package com.linghang.test.secondhandtransactions.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商品表
 * @TableName products
 */
@TableName(value ="commodity")
@Data
@AllArgsConstructor
public class Commodity {
    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Long pid;

    /**
     * 所属用户ID
     */
    private String uid;

    /**
     * 商品名称
     */
    @TableField(value = "name", typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String name;

    /**
     * 商品价格(分
     */
    private Integer price;

    /**
     * 创建时间
     */
    private Date date;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 状态(0:出售/1:求购)
     */
    private Integer status;

    /**
     * 商品类型(1-5)电子商品，生活商品，虚拟商品，学习物品，其他服务或商品
     */
    @TableField("type")
    private Integer type;

    /**
     * 商品介绍
     */
    private String introduce;//100字以内

    /**
     * 
     */
    @JsonProperty("image_path")
    private String imagePath;

    public Commodity(String uid, String name, Integer price, Date date, String contact, Integer status, Integer type, String introduce, String imagePath) {
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.date = date;
        this.contact = contact;
        this.status = status;
        this.type = type;
        this.introduce = introduce;
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Commodity other = (Commodity) that;
        return (this.getPid() == null ? other.getPid() == null : this.getPid().equals(other.getPid()))
            && (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
            && (this.getContact() == null ? other.getContact() == null : this.getContact().equals(other.getContact()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getIntroduce() == null ? other.getIntroduce() == null : this.getIntroduce().equals(other.getIntroduce()))
            && (this.getImagePath() == null ? other.getImagePath() == null : this.getImagePath().equals(other.getImagePath()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPid() == null) ? 0 : getPid().hashCode());
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getContact() == null) ? 0 : getContact().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getIntroduce() == null) ? 0 : getIntroduce().hashCode());
        result = prime * result + ((getImagePath() == null) ? 0 : getImagePath().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", pid=").append(pid);
        sb.append(", uid=").append(uid);
        sb.append(", name=").append(name);
        sb.append(", price=").append(price);
        sb.append(", date=").append(date);
        sb.append(", contact=").append(contact);
        sb.append(", status=").append(status);
        sb.append(", type=").append(type);
        sb.append(", introduce=").append(introduce);
        sb.append(", imagePath=").append(imagePath);
        sb.append("]");
        return sb.toString();
    }

}