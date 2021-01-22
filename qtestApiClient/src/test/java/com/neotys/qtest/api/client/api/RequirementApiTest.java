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


package com.neotys.qtest.api.client.api;

import com.neotys.qtest.api.client.model.CommentResource;
import com.neotys.qtest.api.client.model.PagedResourceCommentResource;
import com.neotys.qtest.api.client.model.RequirementResource;
import com.neotys.qtest.api.client.model.TraceabilityRequirement;
import com.neotys.qtest.api.client.QtestApiException;
import org.junit.Test;
import org.junit.Ignore;

import java.util.List;

/**
 * API tests for RequirementApi
 */
@Ignore
public class RequirementApiTest {

    private final RequirementApi api = new RequirementApi();

    
    /**
     * Adds a Comment to a Requirement
     *
     * To add a comment to a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 7.5+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void addCommentTest() throws QtestApiException {
        Long projectId = null;
        String idOrKey = null;
        CommentResource body = null;
        CommentResource response = api.addComment(projectId, idOrKey, body);

        // TODO: test validations
    }
    
    /**
     * Creates a Requirement
     *
     * To create a new Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 4+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void createRequirementTest() throws QtestApiException {
        Long projectId = null;
        RequirementResource body = null;
        Long parentId = null;
        RequirementResource response = api.createRequirement(projectId, body, parentId);

        // TODO: test validations
    }
    
    /**
     * Deletes a Requirement
     *
     * To delete a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 6+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void deleteTest() throws QtestApiException {
        Long projectId = null;
        Long requirementId = null;
        Object response = api.delete(projectId, requirementId);

        // TODO: test validations
    }
    
    /**
     * Deletes a Comment of a Requirement
     *
     * To delete a comment of a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 7.5+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void deleteCommentTest() throws QtestApiException {
        Long projectId = null;
        String idOrKey = null;
        Long commentId = null;
        Object response = api.deleteComment(projectId, idOrKey, commentId);

        // TODO: test validations
    }
    
    /**
     * Gets a Comment of a Requirement
     *
     * To retrieve a comment of a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 7.5+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void getCommentTest() throws QtestApiException {
        Long projectId = null;
        String idOrKey = null;
        Long commentId = null;
        CommentResource response = api.getComment(projectId, idOrKey, commentId);

        // TODO: test validations
    }
    
    /**
     * Gets all Comments of a Requirement
     *
     * To retrieve all comments of a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 7.6+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void getCommentsTest() throws QtestApiException {
        Long projectId = null;
        String idOrKey = null;
        PagedResourceCommentResource response = api.getComments(projectId, idOrKey);

        // TODO: test validations
    }
    
    /**
     * Gets Requirement Traceability Matrix Report
     *
     * To retrieve a report of Requirements with their covering Test Cases
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void getPublicTraceabilityMatrixReportTest() throws QtestApiException {
        Long projectId = null;
        Long page = null;
        Integer size = null;
        String fieldIds = null;
        List<TraceabilityRequirement> response = api.getPublicTraceabilityMatrixReport(projectId, page, size, fieldIds);

        // TODO: test validations
    }
    
    /**
     * Gets a Requirement
     *
     * To retrieve a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 6+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void getRequirementTest() throws QtestApiException {
        Long projectId = null;
        Long requirementId = null;
        RequirementResource response = api.getRequirement(projectId, requirementId);

        // TODO: test validations
    }
    
    /**
     * Gets multiple Requirements
     *
     * To retrieve all Requirements or Requirements under a specific Module
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void getRequirementsTest() throws QtestApiException {
        Long projectId = null;
        Long parentId = null;
        Long page = null;
        Integer size = null;
        List<RequirementResource> response = api.getRequirements(projectId, parentId, page, size);

        // TODO: test validations
    }
    
    /**
     * Updates a Comment of a Requirement
     *
     * To modify a comment of a Requirement  &lt;strong&gt;qTest Manager version:&lt;/strong&gt; 7.5+
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void updateCommentTest() throws QtestApiException {
        Long projectId = null;
        String idOrKey = null;
        Long commentId = null;
        CommentResource body = null;
        CommentResource response = api.updateComment(projectId, idOrKey, commentId, body);

        // TODO: test validations
    }
    
    /**
     * Updates a Requirement
     *
     * To update properties of an Requirement or to move it to other parent Module
     *
     * @throws QtestApiException
     *          if the Api call fails
     */
    @Test
    public void updateRequirementTest() throws QtestApiException {
        Long projectId = null;
        Long requirementId = null;
        RequirementResource body = null;
        Long parentId = null;
        RequirementResource response = api.updateRequirement(projectId, requirementId, body, parentId);

        // TODO: test validations
    }
    
}