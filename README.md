This is a maven plugin for FitNesse.
=================================== 

mafia-maven-plugin  **MA**aven **FI**tnesse **A**dapter

This plugin let's you control: 

- **CLASSPATH ENTRIES & DEFINITIONS** can be written to root page of FitNesse using the pom information.
- **STARTING & STOPPING** FitNesse.
- **RUN TEST(S), SUITE(S), OR TAG FILTERED TESTS**.
- **COLLECT REPORTING**.

**1 CLASSPATH ENTRIES & DEFINITIONS**

goal: content  
phase: process-resources  
command: mvn mafia:content  

This maven goal will create the content.txt file, root page, for FitNesse, filling it with 
classpath entries (!path <location>) and definitions (!define <definition>).
Besides the compile and runtime dependencies, it will write the configuration, if provided, to the same file (content.txt).

Configuration:
 
   statics - Values ares copied as is to content.txt (This could also be wiki text)j
   resources - Values are prefixed with !define and copied to content.txt  
   targets - Values are prefixed with !define and postfixed with /target/classes/ and copied to content.txt
   (useful for hot-deployment).  
   excludeDependencies - Listed dependencies are excluded from the classpath entries. (Most likely the ones
   listed in the targets configuration section).
   
Example:

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
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
					<groupId>nl.abc.application</groupId>
					<artifactId>application-to-test</artifactId>
				</dependency>
			</excludeDependencies>
		</configuration>
	</plugin>

**2 - STARTING FitNesse.**

goal: start  
phase: install  
command: mvn mafia:start  

This maven goal will start the FitNesse server. You can create a configuration section in your pom to change
default behaviour.

Configuration example with defaults, except for the jvmArguments & jvmDependencies sections, which are not
mandatory:

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
			<port>9090</port>
			<log>${basedir}/log/</log>
			<retainDays>14</retainDays>
			<wikiRoot>${basedir}</wikiRoot>
			<nameRootPage>FitNesseRoot</nameRootPage>
			<jvmArguments>
				<jvmArgument>CM_SYSTEM=fitnesse.wiki.cmSystems.GitCmSystem</jvmArgument>
			</jvmArguments>				
			<jvmDependencies>
				<dependency>
					<groupId>nl.abc.fitnesse</groupId>
					<artifactId>fitnesse-plugins</artifactId>
				</dependency>
			</jvmDependencies>
		</configuration>
	</plugin>

**2 - STOPPING FitNesse**

goal: stop  
phase: clean  
command: mvn mafia:stop  

This maven goal will stop the FitNesse server. You can create a configuration section in your pom to change
default behaviour.

Configuration example with defaults, except for the jvmArguments & jvmDependencies sections, which are not
mandatory:

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
			<port>9090</port>
		</configuration>
	</plugin>
	
**3 - RUN TEST(S), SUITE(S), OR TAG FILTERED TESTS.**

goal: run-tests  
phase: integration-test  
command: mvn mafia:run-tests  

This maven goal will run the tests listed in the configuration section. Note that the tests are run on a seperate
FitNesse server running on port 9091. 

Configuration with defaults. tests, suites and a suiteFilter can be mixed. A suiteFilter needs a suitePageName.

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
			<port>9091</port>
			<wikiRoot>${basedir}</wikiRoot>
			<fitnesseOutputDirectory>${project.build.directory}/fitnesse/</fitnesseOutputDirectory>
			<stopTestsOnFailure>true</stopTestsOnFailure>
			<stopTestsOnException>true</stopTestsOnException>
			<stopTestsOnWrong>true</stopTestsOnWrong>
			<stopTestsOnIgnore>false</stopTestsOnIgnore>
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
	
**4 - COLLECT REPORTING.**

goal: collect-report  
phase: site  
command: mvn mafia:collect-report  

This maven goal will generate a report of the last run tests. 

Configuration with defaults, except reportTemplate (=optional):

	<plugin>
		<groupId>nl.sijpesteijn.testing.fitnesse.plugins</groupId>
		<artifactId>mafia-maven-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
			<workDirectory>${project.build.directory}/fitnesse</workDirectory>
			<fitnesseOutputDirectory>${project.build.directory}/site/fitnesse</fitnesseOutputDirectory>
			<reportTemplate>/location/report.template</reportTemplate>
		</configuration>
	</plugin>
