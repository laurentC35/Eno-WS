FROM maven:3.6-slim as mvn
ADD https://github.com/InseeFr/Lunatic-Model/releases/download/v1.0.1/lunatic-model.jar .
RUN mvn install:install-file -Dfile=lunatic-model.jar -DgroupId=fr.insee.lunatic -DartifactId=lunatic-model -Dversion=1.0.1 -Dpackaging=jar
ADD https://github.com/laurentC35/Eno/releases/download/v2.0.1/eno-core.jar .
RUN mvn install:install-file -Dfile=eno-core.jar -DgroupId=fr.insee.eno -DartifactId=eno-core -Dversion=2.0.1 -Dpackaging=jar
WORKDIR /enows
COPY ./ /enows/
RUN mvn -B -f /enows/pom.xml install


FROM tomcat:jre11-slim
RUN rm -rf $CATALINA_HOME/webapps/*
ADD src/main/resources/log4j2.xml $CATALINA_HOME/webapps/log4j2.xml
ADD src/main/resources/enows-server.properties $CATALINA_HOME/webapps/enows.properties
COPY --from=mvn enows/target/enows.war $CATALINA_HOME/webapps/ROOT.war