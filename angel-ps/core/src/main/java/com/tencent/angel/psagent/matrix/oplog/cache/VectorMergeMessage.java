/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */


package com.tencent.angel.psagent.matrix.oplog.cache;

import com.tencent.angel.ml.math2.vector.Vector;
import com.tencent.angel.psagent.task.TaskContext;

public class VectorMergeMessage extends OpLogMessage {
  private final Vector update;

  public VectorMergeMessage(int seqId, TaskContext context, Vector update) {
    super(seqId, update.getMatrixId(), OpLogMessageType.VECTOR_MERGE, context);
    this.update = update;
  }

  public Vector getUpdate() {
    return update;
  }

  @Override public String toString() {
    return "OpLogMergeMessage [update=" + update + ", toString()=" + super.toString() + "]";
  }
}
