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

    @Hmac
    @400.03.01
    @400.03.02
    @400.03.03
    Scenario Outline: Test /venta-normal endpoint with error 400.03.01 - 400.03.03
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        And I pipe contents of file test/integration/sample-payloads/verificar-venta-normal.json to body
        And I set Content-Type header to application/json
        And I setup HMAC using app transacciones-v1-tpp-app-cicd with values
        | name    | value |

        | issued  | <issued> |
        | nonce   | <nonce>  |
        And I remove header <header>
        When I POST to /venta-normal
        Then response code should be <status>
        And response body path $.code should be <code>
        And response body path $.message should be <message>

        Examples:
        | issued | nonce | header                | status | code      | message                                        |
        | gen()  | gen() | X-Banorte-Hmac-Token  |    400 | 400.03.01 | Cabecera "X-Banorte-Hmac-Token" es requerida.  |
        | gen()  | gen() | X-Banorte-Hmac-Nonce  |    400 | 400.03.02 | Cabecera "X-Banorte-Hmac-Nonce" es requerida.  |
        | gen()  | gen() | X-Banorte-Hmac-Issued |    400 | 400.03.03 | Cabecera "X-Banorte-Hmac-Issued" es requerida. |

    @Hmac
    @400.03.04
    @400.03.05
    @400.03.07
    Scenario Outline: Test /venta-normal endpoint with error 400.03.04 - 400.03.05
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        And I pipe contents of file test/integration/sample-payloads/verificar-venta-normal.json to body
        And I set Content-Type header to application/json
        And I setup HMAC using app transacciones-v1-tpp-app-cicd with values
            | name    | value |

            | issued  | <issued> |
            | nonce   | <nonce>  |
        When I POST to /venta-normal
        Then response code should be <status>
        And response body path $.code should be <code>
        And response body path $.message should be <message>

        Examples:
            | issued | nonce      | status | code      | message                                       |
            | gen()  | 1234567890 |    400 | 400.03.04 | Cabecera X-Banorte-Hmac-Nonce debe estar conformada por 12 digitos de la cadena "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".  |
            | asb    | gen()      |    400 | 400.03.05 | Cabecera X-Banorte-Hmac-Issued no corresponde a un UNIX timestamp.  |
            | 100    | gen()      |    400 | 400.03.07 | Token X-Banorte-Hmac-Token presenta un valor expirado.  |

    @Hmac
    @400.03.06
    Scenario Outline: Test /venta-normal endpoint with error 400.03.06
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        And I pipe contents of file test/integration/sample-payloads/verificar-venta-normal.json to body
        And I set Content-Type header to application/json
        And I setup HMAC using app transacciones-v1-tpp-app-cicd with values
            | name    | value |

            | issued  | <issued> |
            | nonce   | <nonce>  |
        And I remove header X-Banorte-Hmac-Token
        And I set X-Banorte-Hmac-Token header to InvalidHmacToken
        When I POST to /venta-normal
        Then response code should be <status>
        And response body path $.code should be <code>
        And response body path $.message should be <message>

        Examples:
            | issued | nonce | status | code      | message                               |
            | gen()  | gen() |    400 | 400.03.06 | Token HMAC presenta un valor invalido. |

    @Hmac
    @400.03.08
    Scenario: Test /venta-normal endpoint with error 400.03.08
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        And I pipe contents of file test/integration/sample-payloads/verificar-venta-normal.json to body
        And I set Content-Type header to application/json
        And I generate an hmac nonce and store it as hmacNonce
        And I setup HMAC using app transacciones-v1-tpp-app-cicd with values
            | name    | value        |

            | issued  | gen()        |
            | nonce   | `hmacNonce`  |
        And I set X-Banorte-Hmac-Token header to InvalidHmacToken
        When I POST to /venta-normal
        Then response code should be 200
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        And I pipe contents of file test/integration/sample-payloads/verificar-venta-normal.json to body
        And I setup HMAC using app transacciones-v1-tpp-app-cicd with values
            | name    | value        |

            | issued  | gen()        |
            | nonce   | `hmacNonce`  |
        When I POST to /venta-normal
        Then response code should be 400
        And response body path $.code should be 400.03.08
        And response body path $.message should be La cabecera X-Banorte-Hmac-Nonce contiene un valor previamente usado.

    @Api
    @400.06.01
    Scenario: Test /venta-normal endpoint with error 400.06.01
        Given I have a client credentials access_token using app transacciones-v1-tpp-app-cicd
        And I pipe contents of file test/integration/sample-payloads/verificar-venta-normal.json to body
        And I remove request body path $.tipoDeOperacion
        And I set Content-Type header to application/json
        When I use HMAC for application transacciones-v1-tpp-app-cicd and POST to /venta-normal
        Then response code should be 400
        And response body path $.code should be 400.06.01