This is a maven plugin for FitNesse.
=================================== 

mafia-maven-plugin  **MA**aven **FI**tnesse **A**dapter

This plugin let's you control: 

- **STARTING & STOPPING** FitNesse.
- **RUN TEST(S), SUITE(S), OR TAG FILTERED TESTS**.
- **CLASSPATH ENTRIES & DEFINITIONS** can be written to root page of FitNesse using the pom configuration.
- **COLLECT REPORTING**.

**1 - STARTING FitNesse.**

goal: start
phase: install
command: mvn mafia:start

This maven goal will start the FitNesse server. You can create a configuration section in your pom to change
default behaviour.

Configuration:

<table>
    <tr>
        <td><b>Property</b></td>
        <td><b>Default</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>fitNessePort</td>
        <td>9090</td>
        <td>Port for FitNesse to listen on.</td>
    </tr>
    <tr>
        <td>wikiRoot</td>
        <td>${basedir}</td>
        <td>Root of FitNesse.</td>
    </tr>
    <tr>
        <td>nameRootPage</td>
        <td>FitNesseRoot</td>
        <td>Name of the root page</td>
    </tr>
    <tr>
        <td>retainDays</td>
        <td>14</td>
        <td>Number of days to retain older versions (.zip files)</td>
    </tr>
    <tr>
        <td>jvmArguments</td>
        <td></td>
        <td>Arguments for the jvm.</td>
    </tr>
    <tr>
        <td>jvmDependencies</td>
        <td></td>
        <td>Classpath entries for the jvm.</td>
    </tr>
    <tr>
        <td>logDirectory</td>
        <td>${basedir}/log/</td>
        <td>Where to put and what to call the run log.</td>
    </tr>
 </table>

Configuration example with defaults:

<pre>
  <code>
	&lt;plugin&gt;
		&lt;groupId&gt;nl.sijpesteijn.testing.fitnesse.plugins&lt;/groupId&gt;
		&lt;artifactId&gt;mafia-maven-plugin&lt;/artifactId&gt;
		&lt;configuration&gt;
			&lt;fitNessePort&gt;9090&lt;/fitNessePort&gt;
			&lt;wikiRoot&gt;${basedir}&lt;/wikiRoot&gt;
			&lt;nameRootPage&gt;FitNesseRoot&lt;/nameRootPage&gt;
			&lt;retainDays&gt;14&lt;/retainDays&gt;
			&lt;jvmArguments&gt;
				&lt;jvmArgument&gt;CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem&lt;/jvmArgument&gt;
			&lt;/jvmArguments&gt;
			&lt;jvmDependencies&gt;
				&lt;dependency&gt;
					&lt;groupId&gt;nl.abc.fitnesse&lt;/groupId&gt;
					&lt;artifactId&gt;fitnesse-plugin&lt;/artifactId&gt;
				&lt;/dependency&gt;
			&lt;/jvmDependencies&gt;
			&lt;log>${basedir}/log/&lt;/log&gt;
		&lt;/configuration&gt;
	 &lt;/plugin&gt;
  </code>
</pre>


**2 - STOPPING FitNesse**

goal: stop
phase: clean
command: mvn mafia:stop

This maven goal will stop the FitNesse server. You can create a configuration section in your pom to change
default behaviour.

Configuration:

<table>
    <tr>
        <td><b>Property</b></td>
        <td><b>Default</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>fitNessePort</td>
        <td>9090</td>
        <td>Port for FitNesse to listen on.</td>
    </tr>
    <tr>
        <td>jvmArguments</td>
        <td></td>
        <td>Arguments for the jvm.</td>
    </tr>
    <tr>
        <td>jvmDependencies</td>
        <td></td>
        <td>Classpath entries for the jvm.</td>
    </tr>
 </table>

Configuration example with defaults:

<pre>
  <code>
	&lt;plugin&gt;
		&lt;groupId&gt;nl.sijpesteijn.testing.fitnesse.plugins&lt;/groupId&gt;
		&lt;artifactId&gt;mafia-maven-plugin&lt;/artifactId&gt;
		&lt;configuration&gt;
			&lt;fitNessePort&gt;9090&lt;/fitNessePort&gt;
			&lt;jvmArguments&gt;
				&lt;jvmArgument&gt;CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem&lt;/jvmArgument&gt;
			&lt;/jvmArguments&gt;
			&lt;jvmDependencies&gt;
				&lt;dependency&gt;
					&lt;groupId&gt;nl.abc.fitnesse&lt;/groupId&gt;
					&lt;artifactId&gt;fitnesse-plugin&lt;/artifactId&gt;
				&lt;/dependency&gt;
			&lt;/jvmDependencies&gt;
		&lt;/configuration&gt;
	&lt;/plugin&gt;
  </code>
</pre>


**3 - RUN TEST(S), SUITE(S), OR TAG FILTERED TESTS.**

goal: test
phase: integration-test
command: mvn mafia:test

This maven goal will run the tests listed in the configuration section. Note that the tests are run on a FitNesse
server running on separate port, default 9091.

Configuration:

<table>
    <tr>
        <td><b>Property</b></td>
        <td><b>Default</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>fitNesseRunPort</td>
        <td>9091</td>
        <td>Port for FitNesse to listen on.</td>
    </tr>
    <tr>
        <td>jvmArguments</td>
        <td></td>
        <td>Arguments for the jvm.</td>
    </tr>
    <tr>
        <td>jvmDependencies</td>
        <td></td>
        <td>Classpath entries for the jvm.</td>
    </tr>
    <tr>
        <td>host</td>
        <td>localhost</td>
        <td>host to run the test on.</td>
    </tr>
    <tr>
        <td>testResultsDirectory</td>
        <td>${basedir}/FitNesse/files/mafiaTestResults</td>
        <td>Output directory for the mafia test results.</td>
    </tr>
    <tr>
        <td>stopTestsOnIgnore</td>
        <td>false</td>
        <td>Stop the run when an ignore exception occurs.</td>
    </tr>
    <tr>
        <td>stopTestsOnException</td>
        <td>true</td>
        <td>Stop the run when an exception occurs.</td>
    </tr>
    <tr>
        <td>stopTestsOnWrong</td>
        <td>true</td>
        <td>Stop the run when a wrong exception occurs.</td>
    </tr>
    <tr>
        <td>tests</td>
        <td></td>
        <td>List of <test/> elements with tests.</td>
    </tr>
    <tr>
        <td>suites</td>
        <td></td>
        <td>List of <suite/> elements with suites.</td>
    </tr>
    <tr>
        <td>suitePageName</td>
        <td></td>
        <td>Root page of filtered suites to run.</td>
    </tr>
    <tr>
        <td>suiteFilter</td>
        <td></td>
        <td>tag filter to use. Used in combination with a suitePageName</td>
    </tr>
 </table>

Configuration with defaults. tests, suites and a suiteFilter can be mixed. A suiteFilter needs a suitePageName.

<pre>
  <code>
	&lt;plugin&gt;
		&lt;groupId&gt;nl.sijpesteijn.testing.fitnesse.plugins&lt;/groupId&gt;
		&lt;artifactId&gt;mafia-maven-plugin&lt;/artifactId&gt;
		&lt;configuration&gt;
			&lt;fitNesseRunPort&gt;9091&lt;/fitNesseRunPort&gt;
			&lt;jvmArguments&gt;
				&lt;jvmArgument&gt;CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem&lt;/jvmArgument&gt;
			&lt;/jvmArguments&gt;
			&lt;jvmDependencies&gt;
				&lt;dependency&gt;
					&lt;groupId&gt;nl.abc.fitnesse&lt;/groupId&gt;
					&lt;artifactId&gt;fitnesse-plugin&lt;/artifactId&gt;
				&lt;/dependency&gt;
			&lt;/jvmDependencies&gt;
			&lt;host&gt;localhost&lt;/host&gt;
			&lt;testResultsDirectory&gt;${project.build.directory}/fitnesse/&lt;/testResultsDirectory&gt;
			&lt;stopTestsOnIgnore&gt;false&lt;/stopTestsOnIgnore&gt;
			&lt;stopTestsOnException&gt;true&lt;/stopTestsOnException&gt;
			&lt;stopTestsOnWrong&gt;true&lt;/stopTestsOnWrong&gt;
			&lt;tests&gt;
				&lt;test&gt;FrontPage.IntegrationTest&lt;/test&gt;
				&lt;test&gt;...&lt;/test&gt;
			&lt;/tests&gt;
			&lt;suites&gt;
				&lt;suite&gt;FrontPage.IntegrationSuite&lt;/suite&gt;
				&lt;suite&gt;...&lt;/suite&gt;
			&lt;/suites&gt;
			&lt;suitePageName&gt;FrontPage.SomeSuite&lt;/suitePageName&gt;
			&lt;suiteFilter&gt;critical_tests&lt;/suiteFilter&gt;
		&lt;/configuration&gt;
	&lt;/plugin&gt;
  </code>
</pre>


**4 CLASSPATH ENTRIES & DEFINITIONS**

goal: content  
phase: process-resources  
command: mvn mafia:content  

This maven goal will create the content.txt file, root page, for FitNesse, filling it with 
classpath entries (!path <location>) and definitions (!define <definition>). Automatically including the compile and
runtime dependencies, but you can exclude dependencies in the configuration.

Configuration:

<table>
    <tr>
        <td><b>Property</b></td>
        <td><b>Default</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>wikiRoot</td>
        <td>${basedir}</td>
        <td>Root of FitNesse.</td>
    </tr>
    <tr>
        <td>nameRootPage</td>
        <td>FitNesseRoot</td>
        <td>Name of the root page</td>
    </tr>
    <tr>
        <td>statics</td>
        <td></td>
        <td>Values ares copied as is to content.txt (This could also be wiki text)</td>
    </tr>
    <tr>
        <td>resources</td>
        <td></td>
        <td>Values are prefixed with !define and copied to content.txt</td>
    </tr>
    <tr>
        <td>targets</td>
        <td></td>
        <td>Values are prefixed with !define and postfixed with /target/classes/ and copied to content.txt (useful
        for hot-deployment).</td>
    </tr>
    <tr>
        <td>excludeDependencies</td>
        <td></td>
        <td>Listed dependencies are excluded from the classpath entries. (Most likely the ones listed in the targets
        configuration section).</td>
    </tr>
</table>


Example:

<pre>
  <code>
	&lt;plugin&gt;
		&lt;groupId&gt;nl.sijpesteijn.testing.fitnesse.plugins&lt;/groupId&gt;
		&lt;artifactId&gt;mafia-maven-plugin&lt;/artifactId&gt;
		&lt;configuration&gt;
		    &lt;wikiRoot&gt;${basedir}&lt;/wikiRoot&gt;
		    &lt;nameRootPage&gt;FitNesseRoot&lt;/nameRootPage&gt;
			&lt;statics&gt;
				&lt;static&gt;!define TEST_SYSTEM {slim}&lt;/static&gt;
			&lt;/statics&gt;
			&lt;resources&gt;
				&lt;resource&gt;/resource_location/resources/&lt;/resource&gt;
			&lt;/resources&gt;
			&lt;targets&gt;
				&lt;target&gt;../application-to-test/&lt;/target&gt;
			&lt;/targets&gt;
			&lt;excludeDependencies&gt;
				&lt;dependency&gt;
					&lt;groupId&gt;nl.abc.dependency&lt;/groupId&gt;
					&lt;artifactId&gt;some-artifact&lt;/artifactId&gt;
					&lt;version&gt;1.0.0&lt;/version&gt;
				&lt;/dependency&gt;
			&lt;/excludeDependencies&gt;
		&lt;/configuration&gt;
	&lt;/plugin&gt;
  </code>
</pre>


**5 - COLLECT REPORTING.**

goal: report  
phase: site  
command: mvn mafia:report  

This maven goal will generate a report of the last run tests. 

Configuration:

<table>
    <tr>
        <td><b>Property</b></td>
        <td><b>Default</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>wikiRoot</td>
        <td>${basedir}</td>
        <td>Root of FitNesse.</td>
    </tr>
    <tr>
        <td>nameRootPage</td>
        <td>FitNesseRoot</td>
        <td>Name of the root page</td>
    </tr>
</table>

Configuration with defaults:

<pre>
  <code>
    &lt;reporting&gt;
        &lt;plugins&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;nl.sijpesteijn.testing.fitnesse.plugins&lt;/groupId&gt;
                &lt;artifactId&gt;mafia-maven-plugin&lt;/artifactId&gt;
                &lt;configuration&gt;
                    &lt;wikiRoot&gt;${basedir}&lt;/wikiRoot&gt;
                    &lt;nameRootPage&gt;FitNesseRoot&lt;/nameRootPage&gt;
                &lt;/configuration&gt;
            &lt;/plugin&gt;
        &lt;/plugins&gt;
    &lt;/reporting&gt;
 </code>
</pre>
