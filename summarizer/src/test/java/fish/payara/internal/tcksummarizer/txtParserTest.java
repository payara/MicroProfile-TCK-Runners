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

import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.assertTrue;

public class txtParserTest {
    private static Collection<String> inputFiles = new ArrayList<>();
    
    @BeforeClass
    public static void init() {
    inputFiles.add("src/test/sample/ee.jakarta.tck.jsonp.api.collectortests.CollectorTests.txt");
    inputFiles.add("src/test/sample/ee.jakarta.tck.jsonp.api.exceptiontests.ClientTests.txt");
    inputFiles.add("src/test/sample/ee.jakarta.tck.jsonp.api.jsonarraytests.ClientTests.txt");
    inputFiles.add("src/test/sample/ee.jakarta.tck.authentication.test.ejbasyncauthentication.AsyncAuthenticationPublicTest.txt");
    }
    

    @Test
    public void txtParserSummaryTest() throws IOException, ParserConfigurationException {
        TxtParser txtParser = new TxtParser();
        String result = txtParser.parseSummaryReport("src/test/sample/summary.txt", "testSuiteSummary");
        assertTrue(result.contains("### testSuiteSummary"));
        assertTrue(result.contains("Completed running 1694 tests"));
        assertTrue(result.contains("Number of tests failed 6"));
        assertTrue(result.contains("Number of tests with errors 0"));
        assertTrue(result.contains("Number of tests skipped 0"));
    }

    @Test
    public void parseTestSetReport() throws IOException, ParserConfigurationException {
        TxtParser txtParser = new TxtParser();
        String result = txtParser.parseTestSetReport(inputFiles, "testSuiteTestSet");
        assertTrue(result.contains("### testSuiteTestSet"));
        assertTrue(result.contains("Completed running 19 tests"));
        assertTrue(result.contains("Number of tests failed 3"));
        assertTrue(result.contains("Number of tests with errors 2"));
        assertTrue(result.contains("Number of tests skipped 1"));
    }
    
}
