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
 * SiteUsersProfile
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2021-01-14T16:24:25.829Z")
public class SiteUsersProfile {
  @SerializedName("userProfileId")
  private Long userProfileId = null;

  @SerializedName("userIds")
  private List<Long> userIds = null;

  public SiteUsersProfile userProfileId(Long userProfileId) {
    this.userProfileId = userProfileId;
    return this;
  }

   /**
   * ID of user profile
   * @return userProfileId
  **/
  @ApiModelProperty(example = "2", value = "ID of user profile")
  public Long getUserProfileId() {
    return userProfileId;
  }

  public void setUserProfileId(Long userProfileId) {
    this.userProfileId = userProfileId;
  }

  public SiteUsersProfile userIds(List<Long> userIds) {
    this.userIds = userIds;
    return this;
  }

  public SiteUsersProfile addUserIdsItem(Long userIdsItem) {
    if (this.userIds == null) {
      this.userIds = new ArrayList<Long>();
    }
    this.userIds.add(userIdsItem);
    return this;
  }

   /**
   * Array User ID
   * @return userIds
  **/
  @ApiModelProperty(value = "Array User ID")
  public List<Long> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<Long> userIds) {
    this.userIds = userIds;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SiteUsersProfile siteUsersProfile = (SiteUsersProfile) o;
    return Objects.equals(this.userProfileId, siteUsersProfile.userProfileId) &&
        Objects.equals(this.userIds, siteUsersProfile.userIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userProfileId, userIds);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SiteUsersProfile {\n");
    
    sb.append("    userProfileId: ").append(toIndentedString(userProfileId)).append("\n");
    sb.append("    userIds: ").append(toIndentedString(userIds)).append("\n");
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
