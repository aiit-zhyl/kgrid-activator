package org.kgrid.activator;

import com.fasterxml.jackson.databind.JsonNode;
import org.kgrid.adapter.api.Executor;

/**
 * Defines an activated knowledge object endpoint
 *
 * */
public class EndPoint {

  private Executor executor;
  private String knowledgeObjectEndPointPath;
  private String endPointPath;
  private JsonNode inputSchema;

  public EndPoint( Executor executor, String endPointPath, String knowledgeObjectEndPointPath, JsonNode inputSchema) {
    this.endPointPath= endPointPath;
    this.executor = executor;
    this.knowledgeObjectEndPointPath = knowledgeObjectEndPointPath;
    this.inputSchema = inputSchema;
  }

  public Object executeEndPoint(Object input){
    return executor.execute( input );
  }

  public Executor getExecutor() { return executor; }

  public String getKnowledgeObjectEndPointPath() {
    return knowledgeObjectEndPointPath;
  }

  public String getEndPointPath() {
    return endPointPath;
  }

  public String getEndPointAbsolutePath() {
    return getKnowledgeObjectEndPointPath() + getEndPointPath();
  }

  public JsonNode getInputSchema() {
    return inputSchema;
  }
}
