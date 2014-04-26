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

package be.ge0ffrey.coursera.knapsack.persistence;

import java.io.IOException;

import be.ge0ffrey.coursera.knapsack.domain.Item;
import be.ge0ffrey.coursera.knapsack.domain.Knapsack;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;

public class KnapsackExporter extends AbstractTxtSolutionExporter {

    private static final String OUTPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new KnapsackExporter().convertAll();
    }

    public KnapsackExporter() {
        super(new KnapsackDao());
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new KnapsackOutputBuilder();
    }

    public static class KnapsackOutputBuilder extends TxtOutputBuilder {

        private Knapsack knapsack;

        public void setSolution(Solution solution) {
            knapsack = (Knapsack) solution;
        }

        public void writeSolution() throws IOException {
            bufferedWriter.write(knapsack.getScore().getSoftScore() + " 0\n");
            for (Item item : knapsack.getItemList()) {
                bufferedWriter.write((item.getInside() ? "1" : "0") + " ");
            }
            bufferedWriter.write("\n");
        }
    }

}
