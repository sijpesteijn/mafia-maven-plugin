package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
import java.io.FileInputStream;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

public abstract class AbstractFitNesseTestCase<F extends Mojo> extends AbstractMojoTestCase {

    private PlexusConfiguration plexusConfiguration;

    @SuppressWarnings("unchecked")
    F configureFitNesseMojo(final F fitNesseMojo) throws Exception {
        return (F) configureMojo(fitNesseMojo, plexusConfiguration);
    }

    public void setUp(final String mojoName) throws Exception {
        final File pom = new File(getBasedir(), "src/test/resources/mafia-plugin-content.xml");
        final FileInputStream fis = new FileInputStream(pom);
        final Xpp3Dom dom = Xpp3DomBuilder.build(fis, "utf-8");
        plexusConfiguration = this.extractPluginConfiguration("mafia-maven-plugin", dom);
        super.setUp();
    }
}
