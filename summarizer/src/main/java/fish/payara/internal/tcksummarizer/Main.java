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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {

    private final String path;
    private final String format;
    private final String testSuiteName;
    private final String outputPath;
    private String report;
    private static Collection<String> files;

    public Main(String path, String format) {
        this.path = path;
        this.format = format;
        this.outputPath = "results/";
        this.testSuiteName = "unknown suite";
    }

    public Main(String path, String format, String testSuiteName) {
        this.path = path;
        this.format = format;
        this.testSuiteName = testSuiteName;
        this.outputPath = "results/";
    }

    public Main(String path, String format, String testSuiteName, String outputPath) {
        this.path = path;
        this.format = format;
        this.testSuiteName = testSuiteName;
        this.outputPath = outputPath;
    }

    private static Collection<String> createInputFileList(String pathPattern) {
        final Collection<String> fileCollection = new ArrayList<>();
        final Path dir = Paths.get(".");

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pathPattern);

        try {
            Stream<Path> results = Files.find(dir,
                    Integer.MAX_VALUE,
                    (path, basicFileAttributes) -> matcher.matches(path)
            );

            results.forEach(p -> fileCollection.add(p.toString()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return fileCollection;
    }

    private void process() throws IOException {
        Optional<String> firstFile = files.stream().findFirst();
        TxtParser txtParser = new TxtParser();
        XmlParser xmlParser = new XmlParser();
        report = "";
        if (firstFile.isPresent()) {
            String file = firstFile.get();
            switch (format) {
                case "jUnitReport":
                    System.out.println("Parsing jUnit report");
                    report = xmlParser.parsejUnitReport(file, testSuiteName);
                    break;
                case "failsafeSummary":
                    System.out.println("Parsing Failsafe Summary");
                    report = xmlParser.parseFailsafeReport(file, testSuiteName);
                    break;
                case "summaryTxt":
                    System.out.println("Parsing summary.txt");
                    report = txtParser.parseSummaryReport(file, testSuiteName);
                    break;
                case "testSet":
                    System.out.println("Parsing test set results");
                    report = txtParser.parseTestSetReport(files, testSuiteName);
                    break;
                case "collection":
                    System.out.println("Merging summary files");
                    report = txtParser.mergeSummaries(files, testSuiteName);
                    break;
                default:
                    System.out.println("Format not recognised");
                    report = "Format not recognised for the test suite " + testSuiteName;
            }
        }
    }

    public static void main(String... args) throws IOException {
        if (args.length < 1 || args.length > 4) {
            System.out.println("Usage: java -jar <path to .jar> <tck report file> <format:jUnitReport|summaryTxt|testSet|failsafeSummary> [testSuiteName] [<output report path>]");
            return;
        }
        String path = args[0];
        String format = args[1];

        files = createInputFileList(path);

        switch (args.length) {
            case 2: {
                Main main = new Main(path, format);
                main.process();
                main.print();
                break;
            }
            case 3: {
                String testSuiteName = args[2];
                Main main = new Main(path, format, testSuiteName);
                main.process();
                main.print();
                break;
            }
            case 4: {
                String testSuiteName = args[2];
                String outputPath = args[3];
                Main main = new Main(path, format, testSuiteName, outputPath);
                main.process();
                main.print();
                break;
            }
        }
    }

    private String outputFilename() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "summary-" + testSuiteName + "-" + timestamp + ".txt";
    }

    private void print() throws IOException {
        Path outDir = Paths.get(this.outputPath);
        Files.createDirectories(outDir);
        Path outPath = Paths.get(this.outputPath + outputFilename());
        try ( PrintWriter out = new PrintWriter(Files.newBufferedWriter(outPath, StandardCharsets.UTF_8))) {
            out.println(report);
        }
        System.out.println("Report created: " + outPath);
    }
}
