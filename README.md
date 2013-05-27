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

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<configuration>
			<fitNessePort>9090</fitNessePort>
			<wikiRoot>${basedir}</wikiRoot>
			<nameRootPage>FitNesseRoot</nameRootPage>
			<retainDays>14</retainDays>
			<jvmArguments>
				<jvmArgument>CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem</jvmArgument>
			</jvmArguments>
			<jvmDependencies>
				<dependency>
					<groupId>nl.abc.fitnesse</groupId>
					<artifactId>fitnesse-plugin</artifactId>
				</dependency>
			</jvmDependencies>
			<log>${basedir}/log/</log>
		</configuration>
	</plugin>

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

Configuration example with defaults, except for the jvmArguments & jvmDependencies sections, which are not
mandatory:

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<configuration>
			<fitNessePort>9090</fitNessePort>
			<jvmArguments>
				<jvmArgument>CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem</jvmArgument>
			</jvmArguments>
			<jvmDependencies>
				<dependency>
					<groupId>nl.abc.fitnesse</groupId>
					<artifactId>fitnesse-plugin</artifactId>
				</dependency>
			</jvmDependencies>
		</configuration>
	</plugin>

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

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<configuration>
			<fitNesseRunPort>9091</fitNesseRunPort>
			<jvmArguments>
				<jvmArgument>CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem</jvmArgument>
			</jvmArguments>
			<jvmDependencies>
				<dependency>
					<groupId>nl.abc.fitnesse</groupId>
					<artifactId>fitnesse-plugin</artifactId>
				</dependency>
			</jvmDependencies>
			<host>localhost</host>
			<testResultsDirectory>${project.build.directory}/fitnesse/</testResultsDirectory>
			<stopTestsOnIgnore>false</stopTestsOnIgnore>
			<stopTestsOnException>true</stopTestsOnException>
			<stopTestsOnWrong>true</stopTestsOnWrong>
			<tests>
				<test>FrontPage.IntegrationTest</test>
				<test>...</test>
			</tests>
			<suites>
				<suite>FrontPage.IntegrationSuite</suite>
				<suite>...</suite>
			</suites>
			<suitePageName>FrontPage.SomeSuite</suitePageName>
			<suiteFilter>critical_tests</suiteFilter>
		</configuration>
	</plugin>


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

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<configuration>
		    <wikiRoot>${basedir}</wikiRoot>
		    <nameRootPage>FitNesseRoot</nameRootPage>
			<statics>
				<static>!define TEST_SYSTEM {slim}</static>
			</statics>
			<resources>
				<resource>/resource_location/resources/</resource>
			</resources>
			<targets>
				<target>../application-to-test/</target>
			</targets>
			<excludeDependencies>
				<dependency>
					<groupId>nl.abc.dependency</groupId>
					<artifactId>some-artifact</artifactId>
					<version>1.0.0</version>
				</dependency>
			</excludeDependencies>
		</configuration>
	</plugin>

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

    <reporting>
        <plugins>
            <plugin>
                <groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
                <artifactId>mafia-maven-plugin</artifactId>
                <configuration>
                    <wikiRoot>${basedir}</wikiRoot>
                    <nameRootPage>FitNesseRoot</nameRootPage>
                </configuration>
            </plugin>
        </plugins>
    </reporting>
