This is a maven plugin for FitNesse.
=================================== 

mafia-maven-plugin  **MA**aven **FI**tnesse **A**dapter

This plugin let's you control: 

- **STARTING & STOPPING** FitNesse.
- **RUN TEST(S), SUITE(S), OR TAG FILTERED TESTS**.
- **CLASSPATH ENTRIES & DEFINITIONS** can be written to root page of FitNesse using the pom configuration.
- **COLLECT REPORTING**.

Supported versions of FitNesse:

- 20150424
- 20150226
- 20140630
- 20140623
- 20140418
- 20140201
- 20131110
- 20131109
- 20130530
- 20130531
- 20121220
- 20111025

**NOTE if you don't want to use the fitnesse version (= latest) that comes with the mafia plugin, please include
fitnesse as a dependency of your product. IMPORTANT add the &lt;classifier&gt;standalone&lt;/classifier&gt; to include
velocity
and htmlparser dependencies.

1 - STARTING FitNesse.
<br/>

goal: start<br/>
phase: install<br/>
command: mvn mafia:start<br/>

This maven goal will start the FitNesse server. You can create a configuration section in your pom to change
default behaviour.
<br/>
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
        <td>fitNesseAuthenticateStart</td>
        <td>no auth</td>
        <td>username:password - Enforces access for one user or /auth/file/path/and/name - Enforces access for a file of users with encrypted passwords</td>
    </tr>
    <tr>
        <td>fitNesseVerbose</td>
        <td>false</td>
        <td>Sets verbose option for fitnesse</td>
    </tr>
    <tr>
        <td>logDirectory</td>
        <td>${basedir}/log/</td>
        <td>Where to put and what to call the run log.</td>
    </tr>
    <tr>
        <td>connectionAttempts</td>
        <td>4</td>
        <td>Mafia waits for the startup of FitNesse by trying to connect to it. This configuration determines how often Mafia will attempt.</td>
    </tr>
 </table>

Configuration example with defaults and example values:

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
			&lt;connectionAttempts&gt4&lt;/connectionAttempts&gt;
		&lt;/configuration&gt;
	 &lt;/plugin&gt;
  </code>
</pre>


2 - STOPPING FitNesse.
<br/>

goal: stop<br/>
phase: clean<br/>
command: mvn mafia:stop<br/>
<br/>
This maven goal will stop the FitNesse server. You can create a configuration section in your pom to change
default behaviour.
<br/>
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
    <tr>
        <td>fitNesseAuthenticateStop</td>
        <td>no auth</td>
        <td>username:password - Enforces access for user on shutdown</td>
    </tr>
 </table>

Configuration example with defaults and exmaple values:

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


3 - RUN TEST(S), SUITE(S), OR TAG FILTERED TESTS.
<br/>

goal: test<br/>
phase: integration-test<br/>
command: mvn mafia:test<br/>
<br/>
This maven goal will run the tests listed in the configuration section. Note that the tests are run on a FitNesse
server running on separate port, default 9091.
<br/>
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
    <tr>
        <td>unpackWaitTime</td>
        <td>3000 milliseconds</td>
        <td>Time to wait after unpacking a fitnesse.</td>
    </tr>
    <tr>
        <td>fitNesseAuthenticateStart</td>
        <td>no auth</td>
        <td>username:password - Enforces access for one user or /auth/file/path/and/name - Enforces access for a file of users with encrypted passwords on startup</td>
    </tr>
    <tr>
        <td>fitNesseAuthenticateStop</td>
        <td>no auth</td>
        <td>username:password - Enforces access for user on shutdown</td>
    </tr>
    <tr>
        <td>fitNesseUpdatePrevents</td>
        <td>update</td>
        <td>Prevents (omits) updating FitNesseRoot content</td>
    </tr>
    <tr>
        <td>fitNesseVerbose</td>
        <td>not verbose</td>
        <td>Sets verbose option for fitnesse</td>
    </tr>
    <tr>
        <td>writeSurefireReports</td>
        <td>false</td>
        <td>If true, every test result will be written to the folder "surefire-reports" in the maven build directory. This enabled tools like Jenkins to recognize if a test failed.</td>
    </tr>
    <tr>
        <td>connectionAttempts</td>
        <td>4</td>
        <td>Mafia waits for the startup of FitNesse by trying to connect to it. This configuration determines how often Mafia will attempt.</td>
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
			&lt;testResultsDirectory&gt;${project.build.directory}/fitnesse/&lt;/testResultsDirectory&gt;
			&lt;stopTestsOnIgnore&gt;false&lt;/stopTestsOnIgnore&gt;
			&lt;stopTestsOnException&gt;true&lt;/stopTestsOnException&gt;
			&lt;stopTestsOnWrong&gt;true&lt;/stopTestsOnWrong&gt;
			&lt;unpackWaitTime&gt;3000&lt;/unpackWaitTime&gt;
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
			&lt;writeSurefireReports&gtfalse&lt;/writeSurefireReports&gt;
			&lt;connectionAttempts&gt4&lt;/connectionAttempts&gt;
		&lt;/configuration&gt;
	&lt;/plugin&gt;
  </code>
</pre>


4 CLASSPATH ENTRIES & DEFINITIONS.
<br/>

goal: content<br/>
phase: process-resources<br/>  
command: mvn mafia:content<br/>  
<br/>
This maven goal will create the content.txt file, root page, for FitNesse, filling it with 
classpath entries (!path <location>) and definitions (!define <definition>). Automatically including the compile and
runtime dependencies, but you can exclude dependencies in the configuration.
<br/>
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
        <td>Values are prefixed with !define and postfixed with /target/classes/ and added to content.txt (useful
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


5 - COLLECT REPORTING.
<br/>

goal: report<br/>
phase: site<br/>  
command: mvn mafia:report<br/>  

This maven goal will generate a report of the last run tests. 
<br/>
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
