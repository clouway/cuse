package com.clouway.cuse.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class IndexingSchema {

  public static IndexingSchemaBuilder aNewIndexingSchema() {
    return new IndexingSchemaBuilder();
  }

  public static final class IndexingSchemaBuilder {

    private List<String> fields = new ArrayList<String>();
    private List<String> wordFields = new ArrayList<String>();
    private List<String> fullText = new ArrayList<String>();

    public IndexingSchemaBuilder fields(String... fields) {

      Collections.addAll(this.fields, fields);
      return this;
    }

    public IndexingSchemaBuilder fullTextFields(String... fields) {

      Collections.addAll(this.fullText, fields);
      return this;
    }

    public IndexingSchemaBuilder fullWordFields(String... fields) {

      Collections.addAll(this.wordFields, fields);
      return this;
    }

    public IndexingSchema build() {

      IndexingSchema indexingSchema = new IndexingSchema();
      indexingSchema.fields = this.fields;
      indexingSchema.wordFields = this.wordFields;
      indexingSchema.fullText = this.fullText;

      return indexingSchema;
    }
  }

  private List<String> fields;
  private List<String> wordFields;
  private List<String> fullText;

  private IndexingSchema() {
  }

  public List<String> getFields() {
    return fields;
  }

  public List<String> getFullText() {
    return fullText;
  }

  public List<String> getWordFields() {
    return wordFields;
  }
}
