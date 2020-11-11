package org.opengis.cite.wps10;

import com.sun.jersey.api.client.Client;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.apache.xerces.xs.XSModel;
import org.w3c.dom.Document;

/**
 * An enumerated type defining ISuite attributes that may be set to constitute a
 * shared test fixture.
 */
@SuppressWarnings("rawtypes")
public enum SuiteAttribute {

    /**
     * A client component for interacting with HTTP endpoints.
     */
    CLIENT("httpClient", Client.class),
    XML("xml-data", File.class),
    /**
     * Contains the XML Schema components comprising an application schema.
     */
    XSMODEL("xsmodel", XSModel.class),
    /**
     * A DOM Document that represents the test subject or metadata about it.
     */
    TEST_SUBJECT("testSubject", Document.class),
    /**
     * An absolute URI referring to a DOM Document schema.
     */
    TEST_SUBJECT_URI("testSubjectUri", URI.class),
    /**
     * A File containing the test subject or a description of it.
     */
    TEST_SUBJ_FILE("testSubjectFile", File.class),
    /**
     * A File containing the test subject or a description of it.
     */
    EXECUTE_REQUEST_FILE_RAW_DATA_OUTPUT("EXECUTE_REQUEST_FILE_RAW_DATA_OUTPUT",Document.class),
    /*
     * A set of schema locations
     */
    SCHEMA_LOC_SET("schema-loc-set", Set.class);
	
    private final Class attrType;
    private final String attrName;

    private SuiteAttribute(String attrName, Class attrType) {
        this.attrName = attrName;
        this.attrType = attrType;
    }

    public Class getType() {
        return attrType;
    }

    public String getName() {
        return attrName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(attrName);
        sb.append('(').append(attrType.getName()).append(')');
        return sb.toString();
    }
}
