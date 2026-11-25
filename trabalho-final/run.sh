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

# monta a aplicacao em webapps/trabalho8 (jsp + classes + driver do mysql)
APP=$TOMCAT/webapps/trabalhoFinal
#rm -rf "$APP"
mkdir -p "$APP/WEB-INF/classes"
cp web/*.jsp "$APP/"
cp web/game/*.jsp "$APP/"
mkdir -p "$APP/arquivos"
cp -r web/arquivos "$APP/"
cp -r web/WEB-INF/lib "$APP/WEB-INF/"
cp web/WEB-INF/web.xml "$APP/WEB-INF/"
#javac -encoding UTF-8 -d "$APP/WEB-INF/classes" -cp "$TOMCAT/lib/servlet-api.jar" src/*.java
javac -encoding UTF-8 -d "$APP/WEB-INF/classes" -cp "$TOMCAT/lib/*:$APP/WEB-INF/lib/*" src/*/*.java

"$TOMCAT/bin/startup.sh"
echo "Acesse (landing page / login): http://localhost:8080/trabalhoFinal/indexGame.jsp"
echo "Admin: http://localhost:8080/trabalhoFinal/index.jsp"
echo "        http://localhost:8080/trabalhoFinal/usuario.jsp"
echo "        http://localhost:8080/trabalhoFinal/jogo.jsp"
echo "        http://localhost:8080/trabalhoFinal/dica.jsp"
echo "        http://localhost:8080/trabalhoFinal/usergame.jsp"
echo "        http://localhost:8080/trabalhoFinal/upload.jsp"
echo "Logs:   tail -f $TOMCAT/logs/catalina.out"
