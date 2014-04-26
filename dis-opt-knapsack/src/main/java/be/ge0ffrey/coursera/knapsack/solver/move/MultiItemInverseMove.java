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

package be.ge0ffrey.coursera.knapsack.solver.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import be.ge0ffrey.coursera.knapsack.domain.Item;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class MultiItemInverseMove implements Move {

    private final Set<Item> itemSet;

    public MultiItemInverseMove(Set<Item> itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return true;
    }

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new MultiItemInverseMove(itemSet);
    }

    @Override
    public void doMove(ScoreDirector scoreDirector) {
        for (Item item : itemSet) {
            scoreDirector.beforeVariableChanged(item, "inside");
            item.setInside(!item.getInside().booleanValue());
            scoreDirector.afterVariableChanged(item, "inside");
        }
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return itemSet;
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(Boolean.TRUE, Boolean.FALSE);
    }

    @Override
    public String toString() {
        return itemSet.toString();
    }
}
