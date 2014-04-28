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
import java.util.Map;

import be.ge0ffrey.coursera.faculty.domain.Customer;
import be.ge0ffrey.coursera.faculty.domain.Faculty;
import be.ge0ffrey.coursera.faculty.domain.FacultyPlacerSolution;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;

public class FacultyPlacerIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<FacultyPlacerSolution> {

    private Map<Faculty, Integer> facultyFreeMap;
    private Map<Faculty, Integer> facultyCountMap;
    
    private long hardScore;
    private long softScore;

    public void resetWorkingSolution(FacultyPlacerSolution facultyPlacerSolution) {
        int facultyListSize = facultyPlacerSolution.getFacultyList().size();
        facultyFreeMap = new HashMap<Faculty, Integer>(facultyListSize);
        facultyCountMap = new HashMap<Faculty, Integer>(facultyListSize);
        for (Faculty faculty : facultyPlacerSolution.getFacultyList()) {
            facultyFreeMap.put(faculty, faculty.getCapacity());
            facultyCountMap.put(faculty, 0);
        }
        hardScore = 0L;
        softScore = 0L;
        for (Customer customer : facultyPlacerSolution.getCustomerList()) {
            insert(customer);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        // TODO the maps should probably be adjusted
        insert((Customer) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Customer) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((Customer) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((Customer) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
        // TODO the maps should probably be adjusted
    }

    private void insert(Customer customer) {
        Faculty faculty = customer.getFaculty();
        if (faculty != null) {
            softScore -= customer.getLocation().getDistance(faculty.getLocation());

            int oldFree = facultyFreeMap.get(faculty);
            int newFree = oldFree - customer.getDemand();
            hardScore += Math.min(newFree, 0) - Math.min(oldFree, 0);
            facultyFreeMap.put(faculty, newFree);

            int oldFacultyCount = facultyCountMap.get(faculty);
            if (oldFacultyCount == 0) {
                softScore -= faculty.getSetupCost();
            }
            int newFacultyCount = oldFacultyCount + 1;
            facultyCountMap.put(faculty, newFacultyCount);
        }
    }

    private void retract(Customer customer) {
        Faculty faculty = customer.getFaculty();
        if (faculty != null) {
            softScore += customer.getLocation().getDistance(faculty.getLocation());

            int oldFree = facultyFreeMap.get(faculty);
            int newFree = oldFree + customer.getDemand();
            hardScore += Math.min(newFree, 0) - Math.min(oldFree, 0);
            facultyFreeMap.put(faculty, newFree);

            int oldFacultyCount = facultyCountMap.get(faculty);
            int newFacultyCount = oldFacultyCount - 1;
            if (newFacultyCount == 0) {
                softScore += faculty.getSetupCost();
            }
            facultyCountMap.put(faculty, newFacultyCount);
        }
    }

    public HardSoftLongScore calculateScore() {
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

}
