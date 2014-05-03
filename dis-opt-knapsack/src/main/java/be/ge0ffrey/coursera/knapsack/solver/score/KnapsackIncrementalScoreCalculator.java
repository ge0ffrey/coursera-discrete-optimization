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
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;

public class KnapsackIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<Knapsack> {
    
    private int free;
    private int valueTotal;

    public void resetWorkingSolution(Knapsack knapsack) {
        free = knapsack.getCapacity();
        valueTotal = 0;
        for (Item item : knapsack.getItemList()) {
            insert(item);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insert((Item) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Item) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((Item) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((Item) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Item item) {
        Boolean inside = item.getInside();
        if (inside != null) {
            if (inside.booleanValue()) {
                free -= item.getWeight();
                valueTotal += item.getValue();
            }
        }
    }

    private void retract(Item item) {
        Boolean inside = item.getInside();
        if (inside != null) {
            if (inside.booleanValue()) {
                free += item.getWeight();
                valueTotal -= item.getValue();
            }
        }
    }

    public HardMediumSoftScore calculateScore() {
        return HardMediumSoftScore.valueOf(free >= 0 ? 0 : free, valueTotal, free);
    }

}
