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

package be.ge0ffrey.coursera.coloring.solver.score;

import java.util.HashMap;
import java.util.Map;

import be.ge0ffrey.coursera.coloring.domain.Color;
import be.ge0ffrey.coursera.coloring.domain.ColoringSolution;
import be.ge0ffrey.coursera.coloring.domain.Edge;
import be.ge0ffrey.coursera.coloring.domain.Node;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;

public class ColoringIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<ColoringSolution> {

    private Map<Color, Long> colorCountMap;
    
    private long hardScore;
    private long mediumScore;
    private long softScore;

    public void resetWorkingSolution(ColoringSolution coloringSolution) {
        int colorListSize = coloringSolution.getColorList().size();
        colorCountMap = new HashMap<Color, Long>(colorListSize);
        for (Color color : coloringSolution.getColorList()) {
            colorCountMap.put(color, 0L);
        }
        hardScore = 0L;
        mediumScore = 0L;
        softScore = 0L;
        for (Node node : coloringSolution.getNodeList()) {
            insert(node);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        // TODO the maps should probably be adjusted
        insert((Node) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Node) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((Node) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((Node) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
        // TODO the maps should probably be adjusted
    }

    private void insert(Node node) {
        Color color = node.getColor();
        if (color != null) {
            for (Edge edge : node.getEdgeList()) {
                Color otherColor = edge.getOtherNode(node).getColor();
                if (otherColor == color) {
                    hardScore--;
                }
            }

            long oldColorCount = colorCountMap.get(color);
            long newColorCount = oldColorCount + 1L;
            if (oldColorCount == 0) {
                mediumScore--;
            }
            softScore += (newColorCount * newColorCount) - (oldColorCount * oldColorCount);
            colorCountMap.put(color, newColorCount);
        }
    }

    private void retract(Node node) {
        Color color = node.getColor();
        if (color != null) {
            for (Edge edge : node.getEdgeList()) {
                Color otherColor = edge.getOtherNode(node).getColor();
                if (otherColor == color) {
                    hardScore++;
                }
            }

            long oldColorCount = colorCountMap.get(color);
            long newColorCount = oldColorCount - 1L;
            if (newColorCount == 0) {
                mediumScore++;
            }
            softScore += (newColorCount * newColorCount) - (oldColorCount * oldColorCount);
            colorCountMap.put(color, newColorCount);
        }
    }

    public HardMediumSoftLongScore calculateScore() {
        return HardMediumSoftLongScore.valueOf(hardScore, mediumScore, softScore);
    }

}
