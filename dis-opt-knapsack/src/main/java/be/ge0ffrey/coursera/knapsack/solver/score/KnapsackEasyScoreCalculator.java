/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ge0ffrey.coursera.knapsack.solver.score;

import be.ge0ffrey.coursera.knapsack.domain.Item;
import be.ge0ffrey.coursera.knapsack.domain.Knapsack;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class KnapsackEasyScoreCalculator implements EasyScoreCalculator<Knapsack> {

    public HardSoftScore calculateScore(Knapsack knapsack) {
        int free = knapsack.getCapacity();
        int valueTotal = 0;
        for (Item item : knapsack.getItemList()) {
            if (item.getInside() != null) {
                if (item.getInside().booleanValue()) {
                    free -= item.getWeight();
                    valueTotal += item.getValue();
                }
            }
        }
        return HardSoftScore.valueOf(free >= 0 ? 0 : free, valueTotal);
    }

}
