package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.render.RenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.junit.Ignore;
import org.junit.Test;

import fitnesse.responders.run.TestSummary;
import fitnesse.wiki.PageType;

@Ignore
public class MafiaReportGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        final Locale locale = Locale.getDefault();
        final ResourceBundle bundle =
                ResourceBundle.getBundle("mafia-report", locale, this.getClass().getClassLoader());

        final File basedir = new File("target/report/").getAbsoluteFile();
        final String fileName = "fitnesse.html";
        final RenderingContext context = new RenderingContext(basedir, fileName);

        final SiteRendererSink sink = new SiteRendererSink(context);

        final MafiaReportGenerator generator =
                new MafiaReportGenerator(sink, bundle, basedir.getAbsolutePath(), getMafiaTestResults());
        generator.generate();

        final String title = sink.getTitle();
        // assertTrue("MAven FItnesse Adaptor".equals(title));

        final String body = sink.getBody();
        final String head = sink.getHead();
    }

    private List<MafiaTestResult> getMafiaTestResults() {
        final List<MafiaTestResult> mafiaTestResults = new ArrayList<MafiaTestResult>();
        final TestSummary summary1 = new TestSummary();
        summary1.wrong = 2;
        summary1.ignores = 1;
        summary1.right = 4;
        final TestSummary summary2 = new TestSummary();
        summary2.wrong = 0;
        summary2.ignores = 0;
        summary2.right = 3;
        final MafiaTestResult result1 =
                new MafiaTestResult(PageType.TEST, "DummyTest1", summary1,
                    "some html 1<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>end", true);
        final MafiaTestResult result2 =
                new MafiaTestResult(PageType.TEST, "DummyTest2", summary2,
                    "some html 2<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>end", true);
        mafiaTestResults.add(result1);
        mafiaTestResults.add(result2);
        return mafiaTestResults;
    }
}
