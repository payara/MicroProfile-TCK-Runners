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
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class SmokeTest {

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    @Test
    public void fileCreationTest() throws IOException, ParserConfigurationException, SAXException {
        String[] args = new String[]{"**/*junit-report.xml", "jUnitReport", "testSuiteCreation"};
        Main.main(args);

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = "summary-" + args[2] + "-" + timestamp + ".txt";

        File file = new File("results/" + filename);
        assertTrue(file.exists());

        deleteDir(file);
    }

    @Test
    public void fileCreationCustomPathTest() throws IOException, ParserConfigurationException, SAXException {
        String[] args = new String[]{"**/*tck-junit-report.xml", "jUnitReport", "testSuiteCustomPath", "testFolder/subFolder/"};
        Main.main(args);

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = "summary-" + args[2] + "-" + timestamp + ".txt";

        File file = new File("testFolder/subFolder/" + filename);
        assertTrue(file.exists());

        deleteDir(new File("testFolder"));
    }

    @Test
    public void fileCreationMultipleInput() throws IOException, ParserConfigurationException, SAXException {
        String[] args = new String[]{"**/ee.jakarta*txt", "testSet", "testSuiteMultipleFiles"};
        Main.main(args);

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = "summary-" + args[2] + "-" + timestamp + ".txt";

        File file = new File("results/" + filename);
        assertTrue(file.exists());

        deleteDir(file);
    }

    @Test
    public void fileCreationCollection() throws IOException, ParserConfigurationException, SAXException {
        String[] args1 = new String[]{"**/ee.jakarta*txt", "testSet", "testSuiteMultipleFilesForCollection"};
        Main.main(args1);

        String timestamp1 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename1 = "summary-" + args1[2] + "-" + timestamp1 + ".txt";
        File file1 = new File("results/" + filename1);

        String[] args2 = new String[]{"**/*tck-junit-report.xml", "jUnitReport", "testSuiteJunitReportForCollection"};
        Main.main(args2);

        String timestamp2 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename2 = "summary-" + args2[2] + "-" + timestamp2 + ".txt";
        File file2 = new File("results/" + filename2);

        String[] args3 = new String[]{"./results/summary-*txt", "collection", "Collected"};
        Main.main(args3);

        String timestamp3 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename3 = "summary-" + args3[2] + "-" + timestamp3 + ".txt";
        File file3 = new File("results/" + filename3);
        assertTrue(file3.exists());

        deleteDir(file1);
        deleteDir(file2);
        deleteDir(file3);
    }
}
