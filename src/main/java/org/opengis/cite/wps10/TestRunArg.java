package org.opengis.cite.wps10;

/**
 * An enumerated type defining all recognized test run arguments.
 */
public enum TestRunArg {

    /**
     * An absolute URI that refers to a representation of the test subject or
     * metadata about it.
     */
    IUT,
    EXECUTE_REQUEST_FILE_RAW_DATA_OUTPUT,
    XML,
	XSD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
