@error
Feature: Error handling
    As an API consumer
    I want consistent and meaningful error responses
    So that I can handle the errors correctly

    @NotFound
    @foo
    Scenario: GET /foo request not found
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        When I GET /foo
        Then response code should be 404
        And response header Content-Type should be application/json

    @NotFound
    @foobar
    Scenario: GET /foo/bar request not found
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        When I GET /foo/bar
        Then response code should be 404
        And response header Content-Type should be application/json

    @NotFound
    @foobar
    Scenario: GET /foo/bar request not found
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        When I GET /foo/bar
        Then response code should be 404
