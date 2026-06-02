package com.hospital.dto;

import java.math.BigDecimal;

public class DrugStockReportDTO {

    private Long drugId;

    private String drugCode;

    private String drugName;

    private String specification;

    private String unit;

    private Integer beginStock;

    private Integer inQuantity;

    private Integer outQuantity;

    private Integer endStock;

    private BigDecimal unitPrice;

    private BigDecimal stockValue;

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

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
