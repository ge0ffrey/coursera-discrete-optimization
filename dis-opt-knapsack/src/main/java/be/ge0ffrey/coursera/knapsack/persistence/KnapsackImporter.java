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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import be.ge0ffrey.coursera.knapsack.domain.Item;
import be.ge0ffrey.coursera.knapsack.domain.Knapsack;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class KnapsackImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new KnapsackImporter().convertAll();
    }

    public KnapsackImporter() {
        super(new KnapsackDao());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new KnapsackInputBuilder();
    }

    public static class KnapsackInputBuilder extends TxtInputBuilder {

        private Knapsack knapsack;

        private int itemListSize;

        public Solution readSolution() throws IOException {
            knapsack = new Knapsack();
            knapsack.setId(0L);
            knapsack.setName(FilenameUtils.getBaseName(inputFile.getName()));
            readHeaders();
            readItems();

            BigInteger possibleSolutionSize = BigInteger.valueOf(2).pow(knapsack.getItemList().size());
            logger.info("Knapsack {} has {} items with a search space of {}.",
                    getInputId(),
                    knapsack.getItemList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return knapsack;
        }

        private void readHeaders() throws IOException {
            String[] headerTokens = splitBySpace(bufferedReader.readLine(), 2);
            itemListSize = Integer.valueOf(headerTokens[0]);
            knapsack.setCapacity(Integer.valueOf(headerTokens[1]));
        }

        private void readItems() throws IOException {
            List<Item> itemList = new ArrayList<Item>(itemListSize);
            long id = 0L;
            for (int i = 0; i < itemListSize; i++) {
                String[] lineTokens = splitBySpace(bufferedReader.readLine(), 2, 2);
                Item item = new Item();
                item.setId(id);
                id++;
                item.setValue(Integer.valueOf(lineTokens[0]));
                item.setWeight(Integer.valueOf(lineTokens[1]));
                itemList.add(item);
            }
            knapsack.setItemList(itemList);
        }

    }

}
