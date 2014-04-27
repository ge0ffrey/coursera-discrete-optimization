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

package be.ge0ffrey.coursera.faculty.solver.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.ge0ffrey.coursera.faculty.domain.Customer;
import be.ge0ffrey.coursera.faculty.domain.Faculty;
import be.ge0ffrey.coursera.faculty.domain.FacultyPlacerSolution;
import be.ge0ffrey.coursera.knapsack.domain.Item;
import be.ge0ffrey.coursera.knapsack.domain.Knapsack;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class FacultyPlacerEasyScoreCalculator implements EasyScoreCalculator<FacultyPlacerSolution> {

    public HardSoftLongScore calculateScore(FacultyPlacerSolution facultyPlacerSolution) {
        List<Faculty> facultyList = facultyPlacerSolution.getFacultyList();
        Map<Faculty, Integer> facultyFreeMap = new HashMap<Faculty, Integer>(facultyList.size());
        long softScore = 0L;
        for (Customer customer : facultyPlacerSolution.getCustomerList()) {
            Faculty faculty = customer.getFaculty();
            if (faculty != null) {
                softScore -= customer.getLocation().getDistance(faculty.getLocation());
                Integer free = facultyFreeMap.get(faculty);
                if (free == null) {
                    softScore -= faculty.getSetupCost();
                    free = faculty.getCapacity();
                }
                facultyFreeMap.put(faculty, free - customer.getDemand());
            }
        }
        long hardScore = 0L;
        for (Map.Entry<Faculty, Integer> entry : facultyFreeMap.entrySet()) {
            int free = entry.getValue();
            if (free < 0) {
                hardScore += free;
            }
        }
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
