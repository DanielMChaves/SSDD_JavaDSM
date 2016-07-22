#!/bin/sh

set -x

java -Djava.rmi.server.useCodebaseOnly=false -Djava.security.policy=servidor.permisos -cp .:dsm_servidor.jar ServidorDSM $*
