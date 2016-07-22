package dsm;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;


public class AlmacenImpl extends UnicastRemoteObject implements Almacen {

	/* Atributos de la clase 'AlmacenImpl' */
	private static List<ObjetoCompartido> listaObjetos = new ArrayList<ObjetoCompartido>();
	private static final long serialVersionUID = 1L;

	public AlmacenImpl() throws RemoteException {
	}

	public synchronized	List<ObjetoCompartido> leerObjetos(List<CabeceraObjetoCompartido> listaCabezas) throws RemoteException {

		List<ObjetoCompartido> listaFinal = new ArrayList<ObjetoCompartido>();

		for(int i = 0; i < listaCabezas.size(); i++){

			ObjetoCompartido obj = buscarObjeto(listaCabezas.get(i).getNombre());

			if(obj != null && obj.getCabecera().getVersion() > listaCabezas.get(i).getVersion()){
					listaFinal.add(obj);
				}
		}

		if(listaFinal.size() == 0){
			return null;
		}

		return listaFinal;
	}
	
	public synchronized void escribirObjetos(List<ObjetoCompartido> listaObjetosCompartidos) throws RemoteException  {

		for(int i = 0; i < listaObjetosCompartidos.size(); i++){

			/* Comprobamos si el obejto esta en la lista */
			ObjetoCompartido obj = buscarObjeto(listaObjetosCompartidos.get(i).getCabecera().getNombre());

			/* Si es null el objeto */
			if(obj == null){
				listaObjetos.add(listaObjetosCompartidos.get(i));
			}
			else{
				obj.setObjeto(listaObjetosCompartidos.get(i).getObjeto());
				obj.setVersion(listaObjetosCompartidos.get(i).getCabecera().getVersion());
			}


		}
	}
	
	public ObjetoCompartido buscarObjeto(String nombre){

		for(int i = 0; i < listaObjetos.size(); i++){
			
			if(listaObjetos.get(i).getCabecera().getNombre().equals(nombre)){
				return listaObjetos.get(i);
			}
		}

		return null;
	}
}

