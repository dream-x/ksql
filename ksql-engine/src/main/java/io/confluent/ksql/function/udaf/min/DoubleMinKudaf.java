/**
 * Copyright 2017 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package io.confluent.ksql.function.udaf.min;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.kstream.Merger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.confluent.ksql.function.KsqlAggregateFunction;
import io.confluent.ksql.parser.tree.Expression;

public class DoubleMinKudaf extends KsqlAggregateFunction<Double, Double> {

  DoubleMinKudaf(Integer argIndexInValue) {
    super(argIndexInValue, () -> Double.MAX_VALUE, Schema.FLOAT64_SCHEMA,
          Arrays.asList(Schema.FLOAT64_SCHEMA)
    );
  }

  @Override
  public Double aggregate(Double currentVal, Double currentAggVal) {
    if (currentVal < currentAggVal) {
      return currentVal;
    }
    return currentAggVal;
  }

  @Override
  public Merger<String, Double> getMerger() {
    return (aggKey, aggOne, aggTwo) -> {
      if (aggOne < aggTwo) {
        return aggOne;
      }
      return aggTwo;
    };
  }

  @Override
  public KsqlAggregateFunction<Double, Double> getInstance(Map<String, Integer> expressionNames,
                                                           List<Expression> functionArguments) {
    int udafIndex = expressionNames.get(functionArguments.get(0).toString());
    return new DoubleMinKudaf(udafIndex);
  }
}
