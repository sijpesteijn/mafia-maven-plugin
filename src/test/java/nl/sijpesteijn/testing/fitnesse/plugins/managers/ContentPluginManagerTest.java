package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig.Builder;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ContentPluginManagerTest extends AbstractPluginManagerTest {

	private static final String TARGET = "target/";
	private static final String FITNESSE_ROOT = "FitNesseRoot";
	private Builder builder;

	@Override
	@Before
	public void setup() {
		super.setup();
		builder = new ContentPluginConfig.Builder();
		builder.setBaseDir(".");
		builder.setNameRootPage(FITNESSE_ROOT);
		builder.setWikiRoot(testDirectory + TARGET);
	}

	@Test
	public void createEmptyContent() throws Exception {
		new ContentPluginManager(builder.build()).run();
		final String content = getContentFile();
		assertFalse(content.contains("!note "));
	}

	@Test
	public void createContent() throws Exception {
		final List<String> statics = new ArrayList<String>();
		statics.add("!define SOME_VAR {value}");
		statics.add("!define TEST_SYSTEM {slim}");
		builder.setStatics(statics);
		final List<String> compileClasspathElements = new ArrayList<String>();
		compileClasspathElements.add(REPO + JUNIT_JAR);
		compileClasspathElements.add(REPO + LOG4J_JAR);
		compileClasspathElements.add(REPO + FITNESSE_JAR);
		builder.setCompileClasspathElements(compileClasspathElements);
		new ContentPluginManager(builder.build()).run();
		final String content = getContentFile();
		assertTrue(content.contains("!note Statics:"));
	}

	private String getContentFile() throws Exception {
		final StringBuilder contents = new StringBuilder();
		final File file = new File(testDirectory + TARGET + FITNESSE_ROOT + "/content.txt");
		final BufferedReader input = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = input.readLine()) != null) {
			contents.append(line);
		}
		input.close();
		return contents.toString();

	}

}
