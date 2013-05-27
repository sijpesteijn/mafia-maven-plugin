package nl.sijpesteijn.testing.fitnesse.plugins.report;

/**
 * TestType enum.
 */
public enum TestType {

    /**
     * Test type.
     */
    TEST("Tests: ", "test"),

    /**
     * Suite type.
     */
    SUITE("Suites: ", "suite"),

    /**
     * Filtered suite type.
     */
    FILTERED_SUITE("Filtered Suites: ", "filtered");

    /**
     * Section title.
     */
    private String sectionTitle;

    /**
     * Type.
     */
    private String type;

    /**
     * Constructor.
     *
     * @param sectionTitle - the section title.
     * @param type         - type.
     */
    TestType(final String sectionTitle, final String type) {
        this.sectionTitle = sectionTitle;
        this.type = type;
    }

    /**
     * Get the section title.
     *
     * @return {@link java.lang.String}
     */
    public String getSectionTitle() {
        return sectionTitle;
    }

    /**
     * Get the gettype.
     *
     * @return {@link java.lang.String}
     */
    public String getType() {
        return type;
    }
}
