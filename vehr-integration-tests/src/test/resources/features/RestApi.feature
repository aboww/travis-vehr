Feature: Provide VEHR API access via REST calls
  In order to work with openEHR data
  As a client system
  I want to make REST API calls over http

  Scenario: Query openEHR data using AQL over REST

    Query openEHR data using the Archetype Query Language.
    openEHR data and an openEHR template that allows validation of
    the data should exist on the server before it can be queried.
    openEHR data should be stored under an EHR.
    The AQL query should be provided as a parameter to the REST API
    and results should be returned in a form compatible with the AQL
    results specification.

    Given The server is running
    And The client system is logged into a server session
    And The openEHR template for the composition is available to the server
    And An EHR is created
    And A composition is persisted under the EHR
    Then An AQL query should return data from the composition in the EHR

  Scenario:  Query openEHR data using session based EHR Id

    Same scenario as 'Query openEHR data using AQL over REST' but the server
    is not provided the ehr identifier for the composition commit. Instead it
    uses the session Id to find the target ehr id.

    Given The server is running
    And The client system is logged into a server session
    And The openEHR template for the composition is available to the server
    And An EHR is created
    And A composition is persisted under the EHR without an EHR identifier
    Then An AQL query should return data from the composition in the EHR