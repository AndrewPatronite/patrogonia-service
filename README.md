#patrogonia-service
#
Spring boot web service to back the patrogonia react project:
https://github.com/AndrewPatronite/patrogonia

#
Setup includes setting up a fresh database and self signed cert for local development or using lets encrypt for a deployed environment.  
1. Create an empty database.
2. Provide the database url, username, and password to the corresponding spring.datasource properties in the src/main/resources/application.properties file.
3. Create a self-signed certificate with OpenSSL or generate a certificate with lets encrypt.
4. Provide the cert properties to the corresponding server.ssl properties in the the src/main/resources/application.properties file.
5. Running the PatrogoniaService class will start the web service and create or update the database using Hibernate. 
