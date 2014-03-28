package com.clouway.cuse.spi;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public interface IndexSchemaFillAction {

  void fill(IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder, String propertyName);

}
