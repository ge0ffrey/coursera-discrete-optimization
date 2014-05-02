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

package be.ge0ffrey.coursera.coloring.persistence;

import java.io.IOException;

import be.ge0ffrey.coursera.coloring.domain.ColoringSolution;
import be.ge0ffrey.coursera.coloring.domain.Node;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;

public class ColoringExporter extends AbstractTxtSolutionExporter {

    private static final String OUTPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new ColoringExporter().convertAll();
    }

    public ColoringExporter() {
        super(new ColoringDao());
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new ColoringOutputBuilder();
    }

    public static class ColoringOutputBuilder extends TxtOutputBuilder {

        private ColoringSolution solution;

        public void setSolution(Solution solution) {
            this.solution = (ColoringSolution) solution;
        }

        public void writeSolution() throws IOException {
            bufferedWriter.write((- solution.getScore().getMediumScore()) + " 0\n");
            for (Node node : solution.getNodeList()) {
                bufferedWriter.write(node.getColor().getId() + " ");
            }
            bufferedWriter.write("\n");
        }
    }

}
