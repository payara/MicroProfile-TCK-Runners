/*
 *    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *    Copyright (c) [2022] Payara Foundation and/or its affiliates. All rights reserved.
 *
 *    The contents of this file are subject to the terms of either the GNU
 *    General Public License Version 2 only ("GPL") or the Common Development
 *    and Distribution License("CDDL") (collectively, the "License").  You
 *    may not use this file except in compliance with the License.  You can
 *    obtain a copy of the License at
 *    https://github.com/payara/Payara/blob/master/LICENSE.txt
 *    See the License for the specific
 *    language governing permissions and limitations under the License.
 *
 *    When distributing the software, include this License Header Notice in each
 *    file and include the License file at glassfish/legal/LICENSE.txt.
 *
 *    GPL Classpath Exception:
 *    The Payara Foundation designates this particular file as subject to the "Classpath"
 *    exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *    file that accompanied this code.
 *
 *    Modifications:
 *    If applicable, add the following below the License Header, with the fields
 *    enclosed by brackets [] replaced by your own identifying information:
 *    "Portions Copyright [year] [name of copyright owner]"
 *
 *    Contributor(s):
 *    If you wish your version of this file to be governed by only the CDDL or
 *    only the GPL Version 2, indicate your decision by adding "[Contributor]
 *    elects to include this software in this distribution under the [CDDL or GPL
 *    Version 2] license."  If you don't indicate a single choice of license, a
 *    recipient has the option to distribute your version of this file under
 *    either the CDDL, the GPL Version 2 or to extend the choice of license to
 *    its licensees as provided above.  However, if you add GPL Version 2 code
 *    and therefore, elected the GPL Version 2 license, then the option applies
 *    only if the new code is made subject to such option by the copyright
 *    holder.
 */
package fish.payara.internal.tcksummarizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TxtParser {

    private int tests = 0;
    private int failures = 0;
    private int errors = 0;
    private int skipped = 0;

    public String parseSummaryReport(String inputFilePath, String testSuiteName) throws IOException {

        String result = new String();

        try {

            File file = new File(inputFilePath);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String currentLine = sc.nextLine();

                if (!currentLine.isEmpty()) {
                    tests++;
                }
                if (currentLine.contains("Failed.")) {
                    failures++;
                }
            }

            result = "### " + testSuiteName + "\n \nCompleted running " + tests + " tests\n";
            result += "Number of tests failed " + failures + "\n";
            result += "Number of tests with errors " + errors + "\n";
            result += "Number of tests skipped " + skipped + "\n";


        } catch (FileNotFoundException e) {
            throw new IOException("Error during parsing test suite " + testSuiteName + ", file " + inputFilePath + ": " + e.getMessage(), e);
        }

        System.out.println(result);

        return result;
    }

    public String parseTestSetReport(Collection<String> inputFilePaths, String testSuiteName) {

        String result;

        List<Exception> exceptions = new ArrayList<>();
        inputFilePaths.forEach(new Consumer<String>() {
            @Override
            public void accept(String inputFilePath) {
                try {

                    // pass the path to the file as a parameter
                    File file = new File(inputFilePath);
                    Scanner sc = new Scanner(file);

                    while (sc.hasNextLine()) {
                        String currentLine = sc.nextLine();

                        if (currentLine.contains("Tests run: ")) {
                            // Example of the line we parse: "Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 27.655 s"
                            // To ease parsing the line, we remove every white space
                            currentLine = currentLine.replaceAll("\\s+", "");
                            String[] splitLine = currentLine.split(",", 5);
                            // Parsing "Testsrun:1" to get the integer
                            String testNumber = splitLine[0].split(":")[1];
                            tests += Integer.parseInt(testNumber);
                            // Parsing "Failures:0" to get the integer
                            String failureNumber = splitLine[1].split(":")[1];
                            failures += Integer.parseInt(failureNumber);
                            // Parsing "Errors:0" to get the integer
                            String errorNumber = splitLine[2].split(":")[1];
                            errors += Integer.parseInt(errorNumber);
                            // Parsing "Skipped:0" to get the integer
                            String skippedNumber = splitLine[3].split(":")[1];
                            skipped += Integer.parseInt(skippedNumber);
                        }

                    }
                    sc.close();
                } catch (FileNotFoundException | NumberFormatException e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
            }
        });

        result = "### " + testSuiteName + "\n \nCompleted running " + tests + " tests\n";
        result += "Number of tests failed " + failures + "\n";
        result += "Number of tests with errors " + errors + "\n";
        result += "Number of tests skipped " + skipped + "\n";

        result += exceptions.stream()
                .map(e -> "Exception captured! " + e.getMessage() + "\n")
                .collect(Collectors.joining());

        System.out.println(result);

        return result;
    }

    public String mergeSummaries(Collection<String> inputFilePaths, String testSuiteName) {

        String result = null;

        List<Exception> exceptions = new ArrayList<>();
        List<String> fileContent = new ArrayList<>();
        inputFilePaths.forEach(new Consumer<String>() {
            @Override
            public void accept(String inputFilePath) {
                try {

                    // pass the path to the file as a parameter
                    File file = new File(inputFilePath);
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        String currentLine = sc.nextLine();
                        fileContent.add(currentLine + "\n");
                    }
                    sc.close();
                } catch (FileNotFoundException | NumberFormatException e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
            }
        });

        result = String.join("", fileContent);

        result += exceptions.stream()
                .map(e -> "Exception captured! " + e.getMessage() + "\n")
                .collect(Collectors.joining());

        System.out.println(result);

        return result;
    }
}
