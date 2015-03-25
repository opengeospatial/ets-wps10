# Web Processing Service (WPS) 1.0.0 Conformance Test Suite

## Scope

This test suite is based on the following OGC specifications:

  * **OpenGIS Web Processing Servic, Version 1.0.0** [OGC 05-007r7]([http%3A%2F%2Fportal.opengeospatial.org%2Ffiles%2F%3Fartifact_id%3D24151%0A%0A)
  * **Corrigendum for OpenGIS Implementation Standard Web Processing Service (WPS) 1.0.0 (0.0.8)** [OGC 08-091r6](http%3A%2F%2Fportal.opengeospatial.org%2Ffiles%2F%3Fartifact_id%3D32766) 
  * **OGC Web Services Common Specification, Version 1.1.0 with Corrigendum 1** [OGC 06-121r3](http%3A%2F%2Fportal.opengeospatial.org%2Ffiles%2F%3Fartifact_id%3D20040) 
  * **Definition Identifier URNs in OGC Namespace, Version 1.1.0** [OGC 06-023r1](http%3A%2F%2Fportal.opengeospatial.org%2Ffiles%2F%3Fartifact_id%3D16339)

The conformance tests provided here are **not** intended to be used in a
stand-alone manner, but to be incorporated into profile-specific test suites;
they apply to all WPS-based implementations.

## What is tested

Annex A of [OGC 05-007r7] provides the abstract test suite that are implemented in
this test. 

This test only implements the **Server test module**.

  * GetCapabilities, _GET_ and _POST_ methods - Section 8 of OGC 05-007r7
  * DescribeProcess, _GET_ and _POST_ methods - Section 9 of OGC 05-007r7
  * Execute, _POST_ method - methods - Section 10 of OGC 05-007r7

## What is not tested

  * SOAP protocol
  * Execute method with an identified process and expected results

## Test Data

Test data is not required

## Namespaces

Implementations being tested must use the following namespaces:
	
	- gml: http://www.opengis.net/gml
	- ows: http://www.opengis.net/ows/1.1
	- ogc: http://www.opengis.net/ogc
	- wps:http://www.opengis.net/wps/1.0.0

## Schemas

All schemas used for validation in these tests can be found at:
<http://schemas.opengis.net/wps/>

## Release notes
Release notes are available at [here](relnotes.html).







