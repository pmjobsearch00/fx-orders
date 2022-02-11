FX-Order-Management application.
-----------------------------------
Imagine you're a developer working for a company that trades currencies and you have been given the task to implement a simplified version of a FX trading platform called “Dummy FX platform”. “Dummy FX platform” is a Java/Spring Service exposing a REST API. The requirements for phase 1 are limited in scope, not well refined and need to be interpreted by the Developer who will make his assumptions to implement the best possible lean solution. The Service should provide the following functionalities:

1) For this simple version the current price is static and it is supplied via config file (E.g. GBP/USD = 1.2100).
2) Order registration.The order contains fields userId, orderType: BID or ASK, currency(GBP/USD), price(E.g. 1.2100), amount(E.g. 8500).
3) Cancel an order.
4) Return summary regarding live not matched orders.
5) Return a summary of matched trades.

Limitations of “Dummy FX platform” for phase 1:
---------------------------------------------------
- No permanent storage required. Implement simple runtime memory storage.
- “Dummy FX platform” works only for GBP/USD trading.

Non functional requirements:
--------------------------------------
-Ensure enough test coverage.
-Provide a README file containing possible assumptions made analysing the requirements and describing design decisions whenever you think it’s valuable info.

-Make sure the the solution is easy to run via a build tool (Maven or Gradle).

Assumption:
-----------------
- As no explanation provided hence Matching orders is assumed to be a pair of opposit orders i.e. a BID and a ASK having same currency pair, price and amount e.g. GBP/USD - ASK - 1.2020 - 500 and GBP/USD - BID - 1.2020 - 500 are a set of matching orders. It may also be possible that there are 3 BID orders matching with just 1 or 2 ASK order(s) of same currency, price and amount. In this case all 5 orders will be shown in matching order list and removed from unmatching order list.

- All other orders except these matching orders are shown under non-matching orders

JUnit Test
------------------

If you have git-bash installed and configured in the path then use below command to checkout source code into your system,

git clone https://github.com/pmjobsearch01/fx-orders.git

If you have maven installed and in the path then use below command to build and run junit test cases that proves the core functions,

- Go to the folder where the code is checked-out (you should see the pom.xml in your current folder)

cd fx-orders

mvn clean install

Run Application
--------------------------
mvn spring-boot:run

Test from Browser (GUI)
-------------------------
Once application is up and running by using above run command then open a browser and test the application using below url

http://localhost:8080/
