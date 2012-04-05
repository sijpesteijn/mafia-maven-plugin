package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.Properties;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.Scm;
import org.apache.maven.reporting.AbstractMavenReport;

public abstract class AbstractMafiaMavenReport extends AbstractMavenReport {
	protected String modelVersion;
	protected String groupId;
	protected String artifactId;
	protected String version;
	protected String packaging;
	protected String name;
	protected String description;
	protected String url;
	protected License[] licenses;
	protected Developer[] developers;
	protected IssueManagement issueManagement;
	protected Scm scm;
	protected Properties properties;
	protected Dependency[] dependencies;
	/**
	 * @parameter default-value="${project.build}"
	 * @required
	 * @readonly
	 */
	protected Build build;

}
