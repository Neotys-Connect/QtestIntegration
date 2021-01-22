/*
 * qTest Manager API Version 8.6 - 10.2
 * qTest Manager API Version 8.6 - 10.2
 *
 * OpenAPI spec version: 8.6 - 10.2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.neotys.qtest.api.client.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * FieldResource
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-01-14T16:24:25.829Z")
public class FieldResource {
  @SerializedName("instanceType")
  private String instanceType = null;

  @SerializedName("links")
  private List<Link> links = null;

  @SerializedName("id")
  private Long id = null;

  @SerializedName("label")
  private String label = null;

  @SerializedName("required")
  private Boolean required = false;

  @SerializedName("constrained")
  private Boolean constrained = false;

  @SerializedName("order")
  private Integer order = null;

  @SerializedName("allowed_values")
  private List<AllowedValueResource> allowedValues = null;

  @SerializedName("multiple")
  private Boolean multiple = false;

  @SerializedName("data_type")
  private Long dataType = null;

  @SerializedName("searchable")
  private Boolean searchable = false;

  @SerializedName("default_value")
  private String defaultValue = null;

  @SerializedName("system_field")
  private Boolean systemField = false;

  @SerializedName("original_name")
  private String originalName = null;

  @SerializedName("is_active")
  private Boolean isActive = false;

  public FieldResource instanceType(String instanceType) {
    this.instanceType = instanceType;
    return this;
  }

   /**
   * Number Data Type
   * @return instanceType
  **/
  @ApiModelProperty(example = "NumberDataType", value = "Number Data Type")
  public String getInstanceType() {
    return instanceType;
  }

  public void setInstanceType(String instanceType) {
    this.instanceType = instanceType;
  }

   /**
   * Get links
   * @return links
  **/
  @ApiModelProperty(value = "")
  public List<Link> getLinks() {
    return links;
  }

  public FieldResource id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * ID of the Field Setting
   * @return id
  **/
  @ApiModelProperty(example = "1", value = "ID of the Field Setting")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public FieldResource label(String label) {
    this.label = label;
    return this;
  }

   /**
   * Label of the Field Setting
   * @return label
  **/
  @ApiModelProperty(example = "Name", required = true, value = "Label of the Field Setting")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public FieldResource required(Boolean required) {
    this.required = required;
    return this;
  }

   /**
   * Is required or not
   * @return required
  **/
  @ApiModelProperty(example = "true", value = "Is required or not")
  public Boolean isRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public FieldResource constrained(Boolean constrained) {
    this.constrained = constrained;
    return this;
  }

   /**
   * Constrained or not
   * @return constrained
  **/
  @ApiModelProperty(value = "Constrained or not")
  public Boolean isConstrained() {
    return constrained;
  }

  public void setConstrained(Boolean constrained) {
    this.constrained = constrained;
  }

  public FieldResource order(Integer order) {
    this.order = order;
    return this;
  }

   /**
   * Display order
   * @return order
  **/
  @ApiModelProperty(example = "1", value = "Display order")
  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public FieldResource allowedValues(List<AllowedValueResource> allowedValues) {
    this.allowedValues = allowedValues;
    return this;
  }

  public FieldResource addAllowedValuesItem(AllowedValueResource allowedValuesItem) {
    if (this.allowedValues == null) {
      this.allowedValues = new ArrayList<AllowedValueResource>();
    }
    this.allowedValues.add(allowedValuesItem);
    return this;
  }

   /**
   * Allowed values
   * @return allowedValues
  **/
  @ApiModelProperty(value = "Allowed values")
  public List<AllowedValueResource> getAllowedValues() {
    return allowedValues;
  }

  public void setAllowedValues(List<AllowedValueResource> allowedValues) {
    this.allowedValues = allowedValues;
  }

  public FieldResource multiple(Boolean multiple) {
    this.multiple = multiple;
    return this;
  }

   /**
   * Is allowed multiple value
   * @return multiple
  **/
  @ApiModelProperty(example = "false", value = "Is allowed multiple value")
  public Boolean isMultiple() {
    return multiple;
  }

  public void setMultiple(Boolean multiple) {
    this.multiple = multiple;
  }

  public FieldResource dataType(Long dataType) {
    this.dataType = dataType;
    return this;
  }

   /**
   * Data type of the Field Setting
   * minimum: 1
   * @return dataType
  **/
  @ApiModelProperty(example = "1", value = "Data type of the Field Setting")
  public Long getDataType() {
    return dataType;
  }

  public void setDataType(Long dataType) {
    this.dataType = dataType;
  }

  public FieldResource searchable(Boolean searchable) {
    this.searchable = searchable;
    return this;
  }

   /**
   * Allowed full text search or not
   * @return searchable
  **/
  @ApiModelProperty(example = "true", value = "Allowed full text search or not")
  public Boolean isSearchable() {
    return searchable;
  }

  public void setSearchable(Boolean searchable) {
    this.searchable = searchable;
  }

  public FieldResource defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

   /**
   * Default value of the Field Setting
   * @return defaultValue
  **/
  @ApiModelProperty(value = "Default value of the Field Setting")
  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public FieldResource systemField(Boolean systemField) {
    this.systemField = systemField;
    return this;
  }

   /**
   * Is system field or not
   * @return systemField
  **/
  @ApiModelProperty(example = "true", value = "Is system field or not")
  public Boolean isSystemField() {
    return systemField;
  }

  public void setSystemField(Boolean systemField) {
    this.systemField = systemField;
  }

  public FieldResource originalName(String originalName) {
    this.originalName = originalName;
    return this;
  }

   /**
   * Original name of the Field Setting
   * @return originalName
  **/
  @ApiModelProperty(example = "Name", value = "Original name of the Field Setting")
  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public FieldResource isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

   /**
   * Is active or disabled
   * @return isActive
  **/
  @ApiModelProperty(example = "true", value = "Is active or disabled")
  public Boolean isIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldResource fieldResource = (FieldResource) o;
    return Objects.equals(this.instanceType, fieldResource.instanceType) &&
        Objects.equals(this.links, fieldResource.links) &&
        Objects.equals(this.id, fieldResource.id) &&
        Objects.equals(this.label, fieldResource.label) &&
        Objects.equals(this.required, fieldResource.required) &&
        Objects.equals(this.constrained, fieldResource.constrained) &&
        Objects.equals(this.order, fieldResource.order) &&
        Objects.equals(this.allowedValues, fieldResource.allowedValues) &&
        Objects.equals(this.multiple, fieldResource.multiple) &&
        Objects.equals(this.dataType, fieldResource.dataType) &&
        Objects.equals(this.searchable, fieldResource.searchable) &&
        Objects.equals(this.defaultValue, fieldResource.defaultValue) &&
        Objects.equals(this.systemField, fieldResource.systemField) &&
        Objects.equals(this.originalName, fieldResource.originalName) &&
        Objects.equals(this.isActive, fieldResource.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(instanceType, links, id, label, required, constrained, order, allowedValues, multiple, dataType, searchable, defaultValue, systemField, originalName, isActive);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FieldResource {\n");
    
    sb.append("    instanceType: ").append(toIndentedString(instanceType)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    required: ").append(toIndentedString(required)).append("\n");
    sb.append("    constrained: ").append(toIndentedString(constrained)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("    allowedValues: ").append(toIndentedString(allowedValues)).append("\n");
    sb.append("    multiple: ").append(toIndentedString(multiple)).append("\n");
    sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
    sb.append("    searchable: ").append(toIndentedString(searchable)).append("\n");
    sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
    sb.append("    systemField: ").append(toIndentedString(systemField)).append("\n");
    sb.append("    originalName: ").append(toIndentedString(originalName)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
