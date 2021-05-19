package com.github.cloudyrock.mongock.runner.core.executor.operation.list;

import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;

public class ListChangesOp extends Operation<ListChangesResult> {

  public static final String ID = "LIST";

  public ListChangesOp() {
    super(ID);
  }
}
