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
    EXECUTE_HTTP_GET_URI,
    EXECUTE_REQUEST_RAW_DATA_URI,
    EXECUTE_REQUEST_RESPONSE_DOCUMENT_URI,
    EXECUTE_REQUEST_UPDATING_RESPONSE_DOCUMENT_URI,
    XML,
	XSD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

