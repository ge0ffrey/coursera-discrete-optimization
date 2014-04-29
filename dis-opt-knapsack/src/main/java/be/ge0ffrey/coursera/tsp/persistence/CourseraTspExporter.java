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

package be.ge0ffrey.coursera.tsp.persistence;

import java.io.IOException;

import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;

public class CourseraTspExporter extends AbstractTxtSolutionExporter {

    private static final String OUTPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new CourseraTspExporter().convertAll();
    }

    public CourseraTspExporter() {
        super(new CourseraTspDao());
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new TspOutputBuilder();
    }

    public static class TspOutputBuilder extends TxtOutputBuilder {

        private TravelingSalesmanTour solution;

        public void setSolution(Solution solution) {
            this.solution = (TravelingSalesmanTour) solution;
        }

        public void writeSolution() throws IOException {
            bufferedWriter.write((- solution.getScore().getScore() / 1000L) + " 0\n");
            Standstill standstill = solution.getDomicile();
            while (standstill != null) {
                bufferedWriter.write(standstill.getCity().getId() + " ");
                standstill = findNextVisit(standstill);
            }
        }

        private Standstill findNextVisit(Standstill standstill) {
            for (Visit visit : solution.getVisitList()) {
                if (visit.getPreviousStandstill() == standstill) {
                    return visit;
                }
            }
            return null;
        }
    }

}
