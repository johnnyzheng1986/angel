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


package com.tencent.angel.ml.core.conf


import com.tencent.angel.ml.core.utils.{MLException, RowTypeUtils}
import com.tencent.angel.ml.math2.utils.RowType
import org.apache.commons.logging.{Log, LogFactory}
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods.{compact, parse, render}

import scala.collection.mutable

class SharedConf private() extends Serializable {

  private val dataMap: mutable.HashMap[String, String] = mutable.HashMap[String, String]()
  private var graphJson: JObject = _

  def apply(key: String): String = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key)
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def update(key: String, value: String): SharedConf = synchronized {
    dataMap(key) = value
    this
  }

  def hasKey(key: String): Boolean = synchronized {
    dataMap.contains(key)
  }

  def allKeys(): List[String] = synchronized {
    dataMap.keys.toList
  }

  def get(key: String): String = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key)
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def get(key: String, default: String): String = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key)
    } else {
      default
    }
  }

  def getBoolean(key: String, default: Boolean): Boolean = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toBoolean
    } else {
      dataMap(key) = default.toString
      default
    }
  }

  def getInt(key: String, default: Int): Int = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toInt
    } else {
      dataMap(key) = default.toString
      default
    }
  }

  def getLong(key: String, default: Long): Long = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toLong
    } else {
      dataMap(key) = default.toString
      default
    }
  }

  def getFloat(key: String, default: Float): Float = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toFloat
    } else {
      dataMap(key) = default.toString
      default
    }
  }

  def getDouble(key: String, default: Double): Double = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toDouble
    } else {
      dataMap(key) = default.toString
      default
    }
  }

  def getString(key: String, default: String): String = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key)
    } else {
      dataMap(key) = default.toString
      default
    }
  }

  def getBoolean(key: String): Boolean = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toBoolean
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def getInt(key: String): Int = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toInt
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def getLong(key: String): Long = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toLong
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def getFloat(key: String): Float = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toFloat
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def getDouble(key: String): Double = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key).toDouble
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def getString(key: String): String = synchronized {
    if (dataMap.contains(key)) {
      dataMap(key)
    } else {
      throw MLException(s"The key: $key is not exist!")
    }
  }

  def set(key: String, value: String): SharedConf = synchronized {
    dataMap(key) = value
    this
  }

  def setBoolean(key: String, value: Boolean): SharedConf = synchronized {
    dataMap(key) = value.toString
    this
  }

  def setInt(key: String, value: Int): SharedConf = synchronized {
    dataMap(key) = value.toString
    this
  }

  def setLong(key: String, value: Long): SharedConf = synchronized {
    dataMap(key) = value.toString
    this
  }

  def setFloat(key: String, value: Float): SharedConf = synchronized {
    dataMap(key) = value.toString
    this
  }

  def setDouble(key: String, value: Double): SharedConf = synchronized {
    dataMap(key) = value.toString
    this
  }

  def setString(key: String, value: String): SharedConf = synchronized {
    dataMap(key) = value
    this
  }

  def setJson(jast: JObject = null): Unit = synchronized {
    graphJson = jast
  }

  def getJson: JObject = synchronized {
    graphJson
  }

  override def toString: String = {
    val fields = dataMap.toList.map{ case (key: String, value: String) =>
      JField(key, JString(value))
    }
    val res =  if (graphJson != null) {
      new JObject(fields) ~ JField("graphJson", graphJson)
    } else {
      new JObject(fields)
    }

    compact(render(res))
  }
}

object SharedConf {
  val LOG: Log = LogFactory.getLog("SharedConfObject")

  private val sctl: ThreadLocal[SharedConf] = new ThreadLocal[SharedConf]()

  def fromString(confString: String): SharedConf = synchronized {

    if (sctl.get() == null) {
      sctl.set(new SharedConf)
    }

    val sc = sctl.get()

    parse(confString) match {
      case JObject(obj: List[JField]) =>
        obj.foreach{
          case (key, JString(value)) if ! key.equalsIgnoreCase("graphJson") =>
            sc.set(key, value)
          case (key, value: JObject) if key.equalsIgnoreCase("graphJson")=>
            sc.setJson(value)
          case _ => throw new Exception("Shared Conf Error!")
        }
    }

    sc
  }

  def get(): SharedConf = synchronized {
    if (sctl.get() == null) {
      sctl.set(new SharedConf)
    }

    sctl.get()
  }

  def addMap(map: Map[String, String]): Unit = {
    val sc = get()

    map.map {
      case (key: String, value: String) if key.startsWith("angel.") || key.startsWith("ml.") =>
        sc(key) = value
      case _ =>
    }
  }

  def actionType(): String = {
    val sc = get()

    "Train"
  }

  def keyType(): String = {
    val sc = get()

    RowTypeUtils.keyType(RowType.valueOf(
      sc.get(MLCoreConf.ML_MODEL_TYPE, MLCoreConf.DEFAULT_ML_MODEL_TYPE)
    ))
  }

  def valueType(): String = {
    val sc = get()

    RowTypeUtils.valueType(RowType.valueOf(
      sc.get(MLCoreConf.ML_MODEL_TYPE, MLCoreConf.DEFAULT_ML_MODEL_TYPE)
    ))
  }

  def storageType: String = {
    val sc = get()

    RowTypeUtils.storageType(RowType.valueOf(
      sc.get(MLCoreConf.ML_MODEL_TYPE, MLCoreConf.DEFAULT_ML_MODEL_TYPE)
    ))
  }

  def denseModelType: RowType = {
    val sc = get()

    RowTypeUtils.getDenseModelType(RowType.valueOf(
      sc.get(MLCoreConf.ML_MODEL_TYPE, MLCoreConf.DEFAULT_ML_MODEL_TYPE)
    ))
  }

  def numClass: Int = {
    val sc = get()

    sc.getInt(MLCoreConf.ML_NUM_CLASS, MLCoreConf.DEFAULT_ML_NUM_CLASS)
  }

  def modelType: RowType = {
    val sc = get()

    RowType.valueOf(sc.get(MLCoreConf.ML_MODEL_TYPE, MLCoreConf.DEFAULT_ML_MODEL_TYPE))
  }

  def indexRange: Long = {
    val sc = get()

    val ir = sc.getLong(MLCoreConf.ML_FEATURE_INDEX_RANGE, MLCoreConf.DEFAULT_ML_FEATURE_INDEX_RANGE)

    //    if (ir == -1) {
    //      throw MLException("ML_FEATURE_INDEX_RANGE must be set!")
    //    } else {
    //      ir
    //    }
    ir
  }

  def inputDataFormat: String = {
    val sc = get()

    sc.getString(MLCoreConf.ML_DATA_INPUT_FORMAT,
      MLCoreConf.DEFAULT_ML_DATA_INPUT_FORMAT)
  }

  def batchSize: Int = {
    val sc = get()

    sc.getInt(MLCoreConf.ML_MINIBATCH_SIZE, MLCoreConf.DEFAULT_ML_MINIBATCH_SIZE)
  }

  def numUpdatePerEpoch: Int = {
    val sc = get()

    sc.getInt(MLCoreConf.ML_NUM_UPDATE_PER_EPOCH, MLCoreConf.DEFAULT_ML_NUM_UPDATE_PER_EPOCH)
  }

  def blockSize: Int = {
    val sc = get()

    sc.getInt(MLCoreConf.ML_BLOCK_SIZE, MLCoreConf.DEFAULT_ML_BLOCK_SIZE)
  }

  def epochNum: Int = {
    val sc = get()

    sc.getInt(MLCoreConf.ML_EPOCH_NUM, MLCoreConf.DEFAULT_ML_EPOCH_NUM)
  }

  def modelSize: Long = {
    val sc = get()

    val ms = sc.getLong(MLCoreConf.ML_MODEL_SIZE, MLCoreConf.DEFAULT_ML_MODEL_SIZE)
    if (ms == -1) {
      indexRange
    } else {
      ms
    }
  }

  def validateRatio: Double = {
    val sc = get()

    sc.getDouble(MLCoreConf.ML_VALIDATE_RATIO, MLCoreConf.DEFAULT_ML_VALIDATE_RATIO)
  }

  def decay: Double = {
    val sc = get()

    sc.getDouble(MLCoreConf.ML_LEARN_DECAY, MLCoreConf.DEFAULT_ML_LEARN_DECAY)
  }

  def learningRate: Double = {
    val sc = get()

    sc.getDouble(MLCoreConf.ML_LEARN_RATE, MLCoreConf.DEFAULT_ML_LEARN_RATE)
  }

  def modelClassName: String = {
    val sc = get()

    val modelClass = sc.getString(MLCoreConf.ML_MODEL_CLASS_NAME, MLCoreConf.DEFAULT_ML_MODEL_CLASS_NAME)
    if (modelClass == "") {
      throw MLException("ml.model.class.name must be set for graph based algorithms!")
    } else {
      modelClass
    }
  }

  def useShuffle: Boolean = {
    val sc = get()

    sc.getBoolean(MLCoreConf.ML_DATA_USE_SHUFFLE, MLCoreConf.DEFAULT_ML_DATA_USE_SHUFFLE)
  }

  def posnegRatio(): Double = {
    val sc = get()

    sc.getDouble(MLCoreConf.ML_DATA_POSNEG_RATIO, MLCoreConf.DEFAULT_ML_DATA_POSNEG_RATIO)
  }

  def optJsonProvider(): String = {
    val sc = get()

    sc.getString(MLCoreConf.ML_OPTIMIZER_JSON_PROVIDER,
      MLCoreConf.DEFAULT_ML_OPTIMIZER_JSON_PROVIDER
    )
  }

  def storageLevel: String = {
    val sc = get()

    sc.get(MLCoreConf.ML_DATA_STORAGE_LEVEL,
      MLCoreConf.DEFAULT_ML_DATA_STORAGE_LEVEL)
  }

  def stepSizeScheduler: String = {
    val sc = get()

    sc.get(MLCoreConf.ML_OPT_DECAY_CLASS_NAME,
      MLCoreConf.DEFAULT_ML_OPT_DECAY_CLASS_NAME)
  }

  def isSparse: Boolean = {
    val sc = get()

    val ipFormat = inputDataFormat
    sc.getBoolean(MLCoreConf.ML_IS_DATA_SPARSE,
      ipFormat == "libsvm" || ipFormat == "dummy"
    )
  }
}
