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
import java.util.List;
import java.util.Map;

import be.ge0ffrey.coursera.coloring.domain.Color;
import be.ge0ffrey.coursera.coloring.domain.ColoringSolution;
import be.ge0ffrey.coursera.coloring.domain.Edge;
import be.ge0ffrey.coursera.coloring.domain.Node;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class ColoringEasyScoreCalculator implements EasyScoreCalculator<ColoringSolution> {

    public HardMediumSoftLongScore calculateScore(ColoringSolution coloringSolution) {
        long hardScore = 0L;
        for (Edge edge : coloringSolution.getEdgeList()) {
            Color leftColor = edge.getLeftNode().getColor();
            Color rightColor = edge.getRightNode().getColor();
            if (leftColor != null && leftColor == rightColor) {
                hardScore--;
            }
        }
        Map<Color, Integer> colorCountMap = new HashMap<Color, Integer>(coloringSolution.getColorList().size());
        for (Node node : coloringSolution.getNodeList()) {
            Color color = node.getColor();
            if (color != null) {
                Integer colorCount = colorCountMap.get(color);
                colorCountMap.put(color, (colorCount == null ? 1 : colorCount + 1));
            }
        }
        long mediumScore = - colorCountMap.size();
        long softScore = 0L;
        for (Integer colorCount : colorCountMap.values()) {
            softScore += (long) colorCount * (long) colorCount;
        }
        return HardMediumSoftLongScore.valueOf(hardScore, mediumScore, softScore);
    }

}
