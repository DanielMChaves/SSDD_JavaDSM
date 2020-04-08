# Sistema de Memoria Compartida Distribuida en Java (JavaDSM)

**Asignatura:** Sistemas Distribuidos

**Curso:** 2015/2016

**Autores:** [Daniel Melero Chaves](https://github.com/DanielMChaves) y [Miguel Nuñez Díaz-Montes](https://github.com/mnunezdm)

**Lenguaje:** Java

**Proposito:** La práctica consiste en desarrollar un sistema de memoria compartida distribuida (Distributed Shared Memory) en un entorno Java que permita que el alumno llegue a conocer de forma práctica el tipo de técnicas que se usan en estos sistemas, tal como se estudió en la parte teórica de la asignatura.

Con respecto al sistema que se va a desarrollar, se trata de un entorno con las siguientes características (se recomienda revisar la teoría de la asignatura para repasar todos los conceptos que se nombran a continuación):

- Es una implementación de tipo RT-DSM, aplicable, en principio, a cualquier tipo de objeto de Java (más adelante, se explicarán qué restricciones existen finalmente en cuanto a qué tipo de objetos compartidos puede gestionar este sistema).
- Usa un modelo de coherencia de entrada (EC) existiendo, por tanto, dos tipos de entidades en este sistema: cerrojos y objetos compartidos. Ambas entidades tienen asociado un nombre (String) que les identifica de manera única y global durante una ejecución del sistema DSM.
- Como exige el protocolo EC, el programador debe vincular cada objeto compartido con el cerrojo que se usa para asegurar la coherencia en los accesos a dicho objeto.
- Los cerrojos ofrecerán tanto acceso exclusivo como compartido (múltiples lectores/único escritor).
- Utiliza un único gestor centralizado (ServidorDSM) para manejar los cerrojos.
- Como especifica el modelo de coherencia de entrada, cuando un proceso entra en una sección crítica, hay que asegurarse de que vea los últimos cambios que se hayan hecho sobre los objetos compartidos asociados a ese cerrojo desde la última vez que este proceso accedió en sección crítica a los mismos (recuerde que si hay accesos fuera de una sección crítica no es necesario garantizar ningún comportamiento específico: el resultado es impredecible).
- En el modelo de coherencia EC, no es necesario realizar ninguna acción a la salida de una sección crítica de un cerrojo. Los datos modificados se quedarán en ese proceso hasta que otro entre en la sección crítica controlada por ese mismo cerrojo. - Sin embargo, en el sistema que se propone en este ejercicio, este esquema plantearía el problema de qué hacer si el primer proceso termina antes de que el segundo quiera entrar en esa sección crítica. Para solventarlo, aunque sea menos eficiente, el gestor centralizado también actuará como almacén de objetos compartidos.

En consecuencia, la politíca de actualización será la siguiente:
- Al entrar en la sección crítica de un determinado cerrojo, el proceso contactará con el gestor informándole de qué versión tiene de cada objeto compartido asociado a dicho cerrojo. El gestor enviará aquellos objetos cuya versión sea más actual que la que posee el proceso que actúa como cliente.
- Al salir de una sección crítica en modo exclusivo, el proceso enviará al gestor la nueva versión de todos los objetos asociados al cerrojo. Nótese que, por simplicidad, no se va a implementar ninguna estrategia para detectar qué objetos han sido modificados: se considerará que todos lo han sido.

En cuanto a la tecnología de comunicación usada en la práctica, dadas las características de la misma, se ha elegido Java RMI (si no está familiarizado con el uso de esta tecnología puede consultar esta guía sobre la programación en Java RMI).

Para completar esta sección introductoria, se incluye, a continuación, un ejemplo de un cliente de este sistema, lo que permite tener un primer contacto con el API del mismo.

```java
import java.io.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import dsm.*;

public class ClienteDSM {
    static public void main (String args[]) {

       if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // ¿"StringBuffer []"? ¿Por qué no "StringBuffer"?
        StringBuffer [] v1 = new StringBuffer[1];
        v1[0] = new StringBuffer();

        ObjetoCompartido o1 = new ObjetoCompartido("objecto1", v1);

        DSMCerrojo cerrojo = null;
        try {
            cerrojo = new DSMCerrojo("cerrojo");
            cerrojo.asociar(o1);

            if (!cerrojo.adquirir(true)) {
                System.err.println("Error en adquirir en modo exclusivo");
                return;
            }
            System.out.println("Valor de objeto al entrar:");
            System.out.println(v1[0]);
            System.out.println("S.critica exclusiva: cambia valor objetos");
            v1[0].append("|hola");
            if (!cerrojo.liberar()) {
                System.err.println("Error en liberar");
                return;
            }
            cerrojo.desasociar(o1);
        }
        catch (Exception e) {
            System.err.println("Excepcion en ClienteDSM:");
            e.printStackTrace();
        }
    }
}
```

Dadas las características de este proyecto práctico, donde existe un proceso gestor con un doble rol de manejador de cerrojos y de almacén de objetos, se propone un desarrollo incremental en tres fases:

- Implementación de un servicio remoto de cerrojos.
- Implementación de un almacén remoto de objetos.
- Implementación de la funcionalidad final: la clase DSMCerrojo, que se construirá usando los dos servicios remotos previamente implementados.
