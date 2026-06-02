package com.hospital.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

public class DrugStockExcelVO {

    @ExcelProperty("药品编码")
    private String drugCode;

    @ExcelProperty("药品名称")
    private String drugName;

    @ExcelProperty("规格")
    private String specification;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("期初库存")
    private Integer beginStock;

    @ExcelProperty("入库数量")
    private Integer inQuantity;

    @ExcelProperty("出库数量")
    private Integer outQuantity;

    @ExcelProperty("期末库存")
    private Integer endStock;

    @ExcelProperty("单价")
    private BigDecimal unitPrice;

    @ExcelProperty("库存金额")
    private BigDecimal stockValue;

    public String getDrugCode() {
        return drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getBeginStock() {
        return beginStock;
    }

    public void setBeginStock(Integer beginStock) {
        this.beginStock = beginStock;
    }

    public Integer getInQuantity() {
        return inQuantity;
    }

    public void setInQuantity(Integer inQuantity) {
        this.inQuantity = inQuantity;
    }

    public Integer getOutQuantity() {
        return outQuantity;
    }

    public void setOutQuantity(Integer outQuantity) {
        this.outQuantity = outQuantity;
    }

    public Integer getEndStock() {
        return endStock;
    }

    public void setEndStock(Integer endStock) {
        this.endStock = endStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = stockValue;
    }
}
