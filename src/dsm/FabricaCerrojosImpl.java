package dsm;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class FabricaCerrojosImpl extends UnicastRemoteObject implements FabricaCerrojos {

	/* Clase que define el par String-Cerrojo */
	public class ParSC extends UnicastRemoteObject {

		private static final long serialVersionUID = 1L;
		
		/* Atributos de la clase 'ParSC' */
		private String id;
		private Cerrojo cerrojo;


		/* Constructor */
		public ParSC(String id, Cerrojo cerrojo) throws RemoteException {
			this.id = id;
			this.cerrojo = cerrojo;
		}

		/* Getters */

		public String getId(){
			return id;
		}

		public Cerrojo getCerrojo() {
			return cerrojo;
		}

		/* Setters */

		public void setId(String id) {
			this.id = id;
		}

		public void setCerrojo(Cerrojo cerrojo) {
			this.cerrojo = cerrojo;
		}

	}

	/* Atributos de la clase 'FabricaCerrojosImpl' */
	private ArrayList<ParSC> listaCerrojos = new ArrayList<ParSC>();
	private static final long serialVersionUID = 1L;

	public FabricaCerrojosImpl() throws RemoteException {
	}

	public synchronized	Cerrojo iniciar(String id) throws RemoteException {

		/* Buscamos si el cerrojo ya ha sido creado */
		for(int i = 0; i < listaCerrojos.size(); i++){

			if(id.equals(listaCerrojos.get(i).getId())){
				return listaCerrojos.get(i).getCerrojo();
			}
		}

		/* Si no, creamos un nuevo cerrojo y los iniciamos */
		Cerrojo nuevoCerrojo = new CerrojoImpl();
		ParSC nuevoPar = new ParSC(id, nuevoCerrojo);
		listaCerrojos.add(nuevoPar);

		return nuevoCerrojo;
	}
}

