# Solar Watch Backend

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

## About The Project

The backend of the Solar Watch application is built with Java Spring Boot 3. It handles the core logic of the application, including user authentication, API integration, and solar data management. This backend communicates with external APIs and stores information in a PostgreSQL database.

### Built With

- [![Java][Java.com]][Java-url]
- [![Spring][Spring.io]][SpringBoot-url]
- [![SpringSec][SpringSec]][SpringSec-url]
- [![Hibernate][Hibernate]][Hibernate-url]
- [![PostgreSQL][Postgresql.org]][Postgresql-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Prerequisites

- **Java Development Kit (JDK)**: Required for running the backend.
  - Install JDK from: [Oracle JDK download page](https://www.oracle.com/java/technologies/downloads/)
- **Maven**: Required for building and running the backend.
  - Install Maven from: [Apache Maven download page](https://maven.apache.org/download.cgi)

### Project Setup

1. **Clone the Repository**

   Clone the backend repository to your local machine:

   ```
   git clone https://github.com/tolnabert/solar-watch-backend
   ```

3. **Navigate to the Backend Directory**

   Change into the backend directory from the root:
  
   ```
   cd backend
   ```

3. **Configure Environment Variables**

  You need to configure environment variables, for that I noted the necessary ones see below:

- DB_NAME: The name of the PostgreSQL database.
  
  ```
  DB_NAME=solarwatch
  ```

- DB_USERNAME: The PostgreSQL username.
  
  ```
  DB_USERNAME=postgres
  ```

- DB_PASSWORD: The PostgreSQL password.

  ```
  DB_PASSWORD=postgres
  ```
 
- DB_URL: The JDBC URL for connecting to the PostgreSQL database.

  ```
  DB_URL=jdbc:postgresql://localhost:5432/solarwatch
  ```
  
- JWT_SECRET: A secret key used for signing JWTs. Replace your_jwt_secret_here with a secure value.

  ```
  JWT_SECRET=your_jwt_secret_here
  ```
  
If you need you can run the following node.js script to generate a random string and copy it:

  ```
  node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
  ```

Replace paste-the-generated-string-here with the string you copied.

  ```
  JWT_SECRET = paste-the-generated-string-here
  ```

4. **Build and Run the Backend**

  Ensure you have JDK and Maven installed. Run the following command to build and start the backend service:

  ```
  mvn spring-boot:run
  ```

5. **Verify Backend Installation**

  Access the backend service at [http://localhost:8080/api/test/public](http://localhost:8080/api/test/public).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/badge/CONTRIBUTORS_-1-green?style=for-the-badge
[contributors-url]: https://github.com/tolnabert/solar-watch-backend/graphs/contributors
[forks-shield]: https://img.shields.io/badge/FORKS_-0-blue?style=for-the-badge
[forks-url]: https://github.com/tolnabert/solar-watch-backend/network/members
[stars-shield]: https://img.shields.io/badge/STARS-0-blue?style=for-the-badge
[stars-url]: https://github.com/tolnabert/solar-watch-backend/stargazers
[issues-shield]: https://img.shields.io/badge/ISSUES-0-yellow?style=for-the-badge
[issues-url]: https://github.com/tolnabert/solar-watch-backend/issues
[linkedin-shield]: https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white
[linkedin-url]: https://www.linkedin.com/in/tolnabert
[Java.com]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/en/
[Spring.io]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot
[SpringSec]: https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white
[SpringSec-url]: https://spring.io/projects/spring-security
[Hibernate]: https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white
[Hibernate-url]: https://docs.spring.io/spring-framework/reference/data-access/orm/hibernate.html
[Postgresql.org]: https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[Postgresql-url]: https://www.postgresql.org/

