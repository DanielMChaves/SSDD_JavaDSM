package dsm;
import java.rmi.*;
import java.rmi.server.*;

class CerrojoImpl extends UnicastRemoteObject implements Cerrojo {

	/* Atributos de la clase 'CerrojoImpl' */
	private int numEscritores = 0;
	private int numLectores = 0;
	private static final long serialVersionUID = 1L;

	CerrojoImpl() throws RemoteException {
	}

	public synchronized void adquirir (boolean exclusivo) throws RemoteException {

		boolean continuar = true;

		while(continuar){

			try{
				
				/* Modo exclusivo */
				if(exclusivo){
					
					if(numEscritores > 0 || numLectores > 0){
						wait();
					}
					else{
						numEscritores++;
						continuar = false;
					}
				}
				
				/* Modo compartido */
				else{
					
					if(numEscritores > 0){
						wait();
					}
					else{
						numLectores++;
						continuar = false;
					}
				}
				
			}catch(InterruptedException e){
				e.printStackTrace();
			}

		}

	}

	public synchronized boolean liberar() throws RemoteException {
		
		/* Liberar un cerrojo de lectura */
		if(numLectores > 0){
			numLectores--;
			if(numLectores == 0) notifyAll();
			return true;
		}
		
		/* Liberar un cerrojo de escritura */
		else if(numEscritores == 1){
			numEscritores--;
			notifyAll();
			return true;
		}
		
		/* Liberar un cerrojo ya liberado */
		else{
			return false;
		}
		
	}
}
