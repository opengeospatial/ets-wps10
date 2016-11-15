#  WPS 1.0.0 Test Suite Release Notes

## 0.7 (2016-11-14)

Fix [#9](https://github.com/opengeospatial/ets-wps10/issues/9) - Cannot find the declaration of element wps:Capabilities

## 0.6 (2016-04-12)

Fix [#6](https://github.com/opengeospatial/ets-wps10/issues/6) - Multiple Exceptions in ExceptionReport


## 0.5 (2015-07-30)

- Update pom.xml to build with Maven 2

## 0.4 (2015-05-15)

  * Fix [#4](https://github.com/opengeospatial/ets-wps10/issues/2) - Change to use local schemas and update paths on the test

## 0.3 (2015-03-25)
	
  * [#2](https://github.com/opengeospatial/ets-wps10/issues/2) - Clean structure of the test.

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
