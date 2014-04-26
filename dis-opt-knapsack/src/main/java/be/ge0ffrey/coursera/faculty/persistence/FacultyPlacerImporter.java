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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import be.ge0ffrey.coursera.faculty.domain.Customer;
import be.ge0ffrey.coursera.faculty.domain.Faculty;
import be.ge0ffrey.coursera.faculty.domain.FacultyPlacerSolution;
import be.ge0ffrey.coursera.faculty.domain.Location;
import be.ge0ffrey.coursera.knapsack.domain.Item;
import be.ge0ffrey.coursera.knapsack.domain.Knapsack;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class FacultyPlacerImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = "txt";

    public static void main(String[] args) {
        new FacultyPlacerImporter().convertAll();
    }

    public FacultyPlacerImporter() {
        super(new FacultyPlacerDao());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new FacultyPlacerInputBuilder();
    }

    public static class FacultyPlacerInputBuilder extends TxtInputBuilder {

        private FacultyPlacerSolution solution;

        private int facultyListSize;
        private int customerListSize;

        public Solution readSolution() throws IOException {
            solution = new FacultyPlacerSolution();
            solution.setId(0L);
            solution.setName(FilenameUtils.getBaseName(inputFile.getName()));
            readHeaders();
            readFacultyList();
            readCustomerList();

            BigInteger possibleSolutionSize = BigInteger.valueOf(solution.getFacultyList().size())
                    .pow(solution.getCustomerList().size());
            logger.info("Knapsack {} has {} faculties and {} customers with a search space of {}.",
                    getInputId(),
                    solution.getFacultyList().size(),
                    solution.getCustomerList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        private void readHeaders() throws IOException {
            String[] headerTokens = splitBySpace(bufferedReader.readLine(), 2);
            facultyListSize = Integer.valueOf(headerTokens[0]);
            customerListSize = Integer.valueOf(headerTokens[1]);
        }

        private void readFacultyList() throws IOException {
            List<Faculty> facultyList = new ArrayList<Faculty>(facultyListSize);
            long id = 0L;
            for (int i = 0; i < facultyListSize; i++) {
                String[] lineTokens = splitBySpace(bufferedReader.readLine(), 4);
                Faculty faculty = new Faculty();
                faculty.setId(id);
                id++;
                faculty.setSetupCost(Integer.valueOf(lineTokens[0]));
                faculty.setCapacity(Integer.valueOf(lineTokens[1]));
                faculty.setLocation(new Location(Double.valueOf(lineTokens[2]), Double.valueOf(lineTokens[3])));
                facultyList.add(faculty);
            }
            solution.setFacultyList(facultyList);
        }

        private void readCustomerList() throws IOException {
            List<Customer> customerList = new ArrayList<Customer>(customerListSize);
            long id = 0L;
            for (int i = 0; i < customerListSize; i++) {
                String[] lineTokens = splitBySpace(bufferedReader.readLine(), 3);
                Customer customer = new Customer();
                customer.setId(id);
                id++;
                customer.setDemand(Integer.valueOf(lineTokens[0]));
                customer.setLocation(new Location(Double.valueOf(lineTokens[1]), Double.valueOf(lineTokens[2])));
                customerList.add(customer);
            }
            solution.setCustomerList(customerList);
        }

    }

}
