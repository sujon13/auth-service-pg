This is the auth service for live exam website

You will find frontend [here](https://github.com/nusrat35/exam-study-frontend).


 ### Cloning and Running the Application in local ###

-   First you need to have jdk21 installed on your pc.
-   You have to set value for following environment variables `AUTH_DB_USER`, `AUTH_DB_PASSWORD`, `AUTH_CLIENT_ID` and `AUTH_CLIENT_SECRET`

-   Clone the repo in your terminal by clicking the green clone or download button at the top right and copying the url
-   Type `git clone [repository url]`
-   Type `cd [local repository]` to go to local repository.
-   Now run the project using your IDE or first build the project using gradle (`./gradlew clean build -x test`) and then run the generated jar file (` java -jar .\auth-0.0.1.jar`) 
