package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * User: gijs
 * Date: 5/21/13 10:28 PM
 */
public class MafiaCollectingDependencyNodeVisitorTest {
    private MafiaCollectingDependencyNodeVisitor visitor;

    @Before
    public void setup() throws Exception {
        visitor = new MafiaCollectingDependencyNodeVisitor();
    }

    @Test
    public void test() throws Exception {
        DependencyNode nodeMock = mock(DependencyNode.class);
        visitor.visit(nodeMock);
    }
}
