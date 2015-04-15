package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

@SuppressWarnings("serial")
public class TestResultException extends RuntimeException {

	public TestResultException(String string, Exception e) {
		super(string, e);
	}

}
