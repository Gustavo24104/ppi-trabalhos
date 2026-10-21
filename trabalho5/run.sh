#!/bin/bash
# compila, publica no Tomcat 9 e inicia o servidor
# uso: ./run.sh        (compila e inicia)
#      ./run.sh stop   (para o Tomcat)
set -e
cd "$(dirname "$0")"

TOMCAT=${TOMCAT:-$HOME/apache-tomcat-9.0.123}

if [ "$1" = "stop" ]; then
    "$TOMCAT/bin/shutdown.sh"
    exit 0
fi

# monta a aplicacao em webapps/trabalho5 (html + classes + driver do mysql)
APP=$TOMCAT/webapps/trabalho5
rm -rf "$APP"
mkdir -p "$APP/WEB-INF/classes"
cp web/*.html "$APP/"
cp -r web/WEB-INF/lib "$APP/WEB-INF/"
javac -encoding UTF-8 -d "$APP/WEB-INF/classes" -cp "$TOMCAT/lib/servlet-api.jar" src/*.java

"$TOMCAT/bin/startup.sh"
echo "Acesse: http://localhost:8080/trabalho5/dica.html"
echo "        http://localhost:8080/trabalho5/usergame.html"
echo "Logs:   tail -f $TOMCAT/logs/catalina.out"
