#  WPS 1.0.0 Test Suite Release Notes

## 0.3 (2015-03-25)
	
	* [2](https://github.com/opengeospatial/ets-wps10/issues/2) - Clean structure of the test.

## r2

  * wps:general-WPS.General-InvalidRequest.1 : this test do not longer require the exceptionCode to be 'InvalidRequest' 
  * getCapabilities:core-WPS.GetCapabilities-ResponseContentsValidProccesIdentifier.1 : this test do not longer require process identifier to be valid URN.
  * The exceptionCode verification now works when the server return multiple more than one Exception element.
  * getCapabilities:core-WPS.GetCapabilities-KVPRequestRequestParameterHandling.1 : now look for exceptionCode : 'OperationNotSupported' and locator: 'GetMeASandwich'

## r1

  * Updated config file for TEAM-Engine v4.
  * Fixed schema references (local copies added to classpath).

## r0

  * first draft of the wps 1.0.0 test suite 
