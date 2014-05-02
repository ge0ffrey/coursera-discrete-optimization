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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.ge0ffrey.coursera.coloring.domain.Color;
import be.ge0ffrey.coursera.coloring.domain.ColoringSolution;
import be.ge0ffrey.coursera.coloring.domain.Edge;
import be.ge0ffrey.coursera.coloring.domain.Node;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class ColoringImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new ColoringImporter().convertAll();
    }

    public ColoringImporter() {
        super(new ColoringDao());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new ColoringInputBuilder();
    }

    public static class ColoringInputBuilder extends TxtInputBuilder {

        private ColoringSolution solution;

        private int nodeListSize;
        private int edgeListSize;

        public Solution readSolution() throws IOException {
            solution = new ColoringSolution();
            solution.setId(0L);
            readHeaders();
            createNodeList();
            readEdgeList();
            createColorList();

            BigInteger possibleSolutionSize = BigInteger.valueOf(solution.getColorList().size())
                    .pow(solution.getNodeList().size());
            logger.info("ColoringSolution {} has {} colors, {} nodes and {} edges with a search space of {}.",
                    getInputId(),
                    solution.getColorList().size(),
                    solution.getNodeList().size(),
                    solution.getEdgeList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        private void readHeaders() throws IOException {
            String[] headerTokens = splitBySpace(bufferedReader.readLine(), 2);
            nodeListSize = Integer.valueOf(headerTokens[0]);
            edgeListSize = Integer.valueOf(headerTokens[1]);
        }

        private void createNodeList() {
            List<Node> nodeList = new ArrayList<Node>(nodeListSize);
            long id = 0L;
            for (int i = 0; i < nodeListSize; i++) {
                Node node = new Node();
                node.setId(id);
                id++;
                node.setEdgeList(new ArrayList<Edge>(nodeListSize / 10));
                nodeList.add(node);
            }
            solution.setNodeList(nodeList);
        }

        private void readEdgeList() throws IOException {
            List<Node> nodeList = solution.getNodeList();
            List<Edge> edgeList = new ArrayList<Edge>(edgeListSize);
            long id = 0L;
            for (int i = 0; i < edgeListSize; i++) {
                String[] lineTokens = splitBySpace(bufferedReader.readLine(), 2);
                Edge edge = new Edge();
                edge.setId(id);
                id++;
                Node leftNode = nodeList.get(Integer.parseInt(lineTokens[0]));
                edge.setLeftNode(leftNode);
                leftNode.getEdgeList().add(edge);
                Node rightNode = nodeList.get(Integer.parseInt(lineTokens[1]));
                edge.setRightNode(rightNode);
                rightNode.getEdgeList().add(edge);
                edgeList.add(edge);
            }
            solution.setEdgeList(edgeList);
        }

        private void createColorList() {
            int colorListSize = nodeListSize / 2;
            List<Color> colorList = new ArrayList<Color>(colorListSize);
            long id = 0L;
            for (int i = 0; i < colorListSize; i++) {
                Color color = new Color();
                color.setId(id);
                id++;
                colorList.add(color);
            }
            solution.setColorList(colorList);
        }

    }

}
