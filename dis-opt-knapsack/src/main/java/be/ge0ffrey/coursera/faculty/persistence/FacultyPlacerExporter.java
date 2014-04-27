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

package be.ge0ffrey.coursera.faculty.persistence;

import java.io.IOException;

import be.ge0ffrey.coursera.faculty.domain.Customer;
import be.ge0ffrey.coursera.faculty.domain.FacultyPlacerSolution;
import be.ge0ffrey.coursera.knapsack.domain.Item;
import be.ge0ffrey.coursera.knapsack.domain.Knapsack;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;

public class FacultyPlacerExporter extends AbstractTxtSolutionExporter {

    private static final String OUTPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new FacultyPlacerExporter().convertAll();
    }

    public FacultyPlacerExporter() {
        super(new FacultyPlacerDao());
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new FacultyPlacerOutputBuilder();
    }

    public static class FacultyPlacerOutputBuilder extends TxtOutputBuilder {

        private FacultyPlacerSolution solution;

        public void setSolution(Solution solution) {
            this.solution = (FacultyPlacerSolution) solution;
        }

        public void writeSolution() throws IOException {
            bufferedWriter.write((- solution.getScore().getSoftScore() / 1000L) + " 0\n");
            for (Customer customer : solution.getCustomerList()) {
                bufferedWriter.write(customer.getFaculty().getId() + " ");
            }
            bufferedWriter.write("\n");
        }
    }

}
