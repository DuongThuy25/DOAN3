Feature: Feature

# login
  @login
  Scenario: Login with data from Excel
    Given Read login data from Excel
    When Execute login tests with each data row
    Then Should see the expected login results

# register
  @register
  Scenario: Register with data from Excel
    Given Read register data from Excel
    When Execute register tests with each data row
    Then Should see the expected register results
