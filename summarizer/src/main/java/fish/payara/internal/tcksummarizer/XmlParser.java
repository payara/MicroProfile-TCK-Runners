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
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class XmlParser {

    public String parsejUnitReport(String inputFilePath, String testSuiteName) {

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        String result = new String();

        List<String> resultList = new ArrayList<>();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(inputFilePath));

            doc.getDocumentElement().normalize();

            // get <staff>
            NodeList testSuites = doc.getElementsByTagName("testsuite");

            for (int temp = 0; temp < testSuites.getLength(); temp++) {

                String testResult = "";

                Node node = testSuites.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    if (testSuiteName.equals("unknown suite")) {
                        testSuiteName = element.getAttribute("name");
                    }

                    String tests = element.getAttribute("tests");
                    String failures = element.getAttribute("failures");
                    String errors = element.getAttribute("errors");
                    String skipped = element.getAttribute("skipped");

                    testResult = "### " + testSuiteName + "\n \nCompleted running " + tests + " tests\n";
                    testResult += "Number of tests failed " + failures + "\n";
                    testResult += "Number of tests with errors " + errors + "\n";
                    testResult += "Number of tests skipped " + skipped + "\n";

                }
                if (!testResult.isEmpty()) {
                    resultList.add(testResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!resultList.isEmpty()) {
            for (int temp = 0; temp < resultList.size(); temp++) {
                result += resultList.get(temp) + "\n";
            }
        }
        System.out.println(result);

        return result;
    }

    public String parseFailsafeReport(String inputFilePath, String testSuiteName) throws IOException {

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        String result = new String();

        List<String> resultList = new ArrayList<>();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(inputFilePath));

            doc.getDocumentElement().normalize();

            // get <staff>
            NodeList testSuites = doc.getElementsByTagName("failsafe-summary");

            for (int temp = 0; temp < testSuites.getLength(); temp++) {

                String testResult = "";

                Node node = testSuites.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String tests = element.getElementsByTagName("completed").item(0).getTextContent();
                    String failures = element.getElementsByTagName("failures").item(0).getTextContent();
                    String errors = element.getElementsByTagName("errors").item(0).getTextContent();
                    String skipped = element.getElementsByTagName("skipped").item(0).getTextContent();

                    testResult = "### " + testSuiteName + "\n \nCompleted running " + tests + " tests\n";
                    testResult += "Number of tests failed " + failures + "\n";
                    testResult += "Number of tests with errors " + errors + "\n";
                    testResult += "Number of tests skipped " + skipped + "\n";

                }
                if (!testResult.isEmpty()) {
                    resultList.add(testResult);
                }
            }
        } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
            throw new IOException("Error during parsing test suite " + testSuiteName + ", file " + inputFilePath + ": " + e.getMessage(), e);
        }
        if (!resultList.isEmpty()) {
            for (int temp = 0; temp < resultList.size(); temp++) {
                result += resultList.get(temp) + "\n";
            }
        }

        System.out.println(result);

        return result;
    }

}
