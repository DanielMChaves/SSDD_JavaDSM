package dsm;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class DSMCerrojo {


    /* Atributos de la clase 'DSMCerrojo' */
    private Almacen almacenCerrojos;
    private boolean cerrojoExclusivo;
    private Cerrojo cerrojo;
    private FabricaCerrojos fabricaCerrojos;
    private List<ObjetoCompartido> listaObjetos = new ArrayList<ObjetoCompartido>();
    private String nombreCerrojo;
    private String url = "rmi://" + System.getenv("SERVIDOR") + ":" + System.getenv("PUERTO");

    public DSMCerrojo (String nombre) throws RemoteException, MalformedURLException, NotBoundException {

        almacenCerrojos = (Almacen) Naming.lookup(url + "/DSM_almacen");
        fabricaCerrojos = (FabricaCerrojos) Naming.lookup(url + "/DSM_cerrojos");
        cerrojo = fabricaCerrojos.iniciar(nombre);
        nombreCerrojo = nombre;
    }

    public void asociar(ObjetoCompartido obj) {
        listaObjetos.add(obj);
    }

    public void desasociar(ObjetoCompartido obj) {

        boolean desasociado = false;
        String nombreObj = obj.getCabecera().getNombre();

        for(int i = 0; i < listaObjetos.size() && !desasociado; i++){
            if(listaObjetos.get(i).getCabecera().getNombre().equals(nombreObj)){
                listaObjetos.remove(i);
                desasociado = true;
            }
        }
    }

    public boolean adquirir(boolean exclusivo) throws RemoteException {

        cerrojo.adquirir(exclusivo);
        cerrojoExclusivo = exclusivo;
        List<CabeceraObjetoCompartido> listaCabezas = new ArrayList<CabeceraObjetoCompartido>();
        List<ObjetoCompartido> objetosAlmacen;

        for(int i = 0; i < listaObjetos.size(); i++){
            listaCabezas.add(listaObjetos.get(i).getCabecera());
        }
        
        if(listaCabezas.size() == 0){
            return true;
        }

        objetosAlmacen = almacenCerrojos.leerObjetos(listaCabezas);

        if(objetosAlmacen == null){
            return true;            
        }
        
        for(int i = 0; i < objetosAlmacen.size(); i++){

            ObjetoCompartido obj = buscarObjeto(objetosAlmacen.get(i).getCabecera().getNombre());

            if(obj != null){
                obj.setObjeto(objetosAlmacen.get(i).getObjeto());
                obj.setVersion(objetosAlmacen.get(i).getCabecera().getVersion());
            }
        }

        return true;
    }

    public boolean liberar() throws RemoteException {

        List<ObjetoCompartido> objetosIncrementados = new ArrayList<ObjetoCompartido>();

        if(cerrojoExclusivo){

            for(int i = 0; i < listaObjetos.size(); i++){
                listaObjetos.get(i).incVersion();
                objetosIncrementados.add(listaObjetos.get(i));
            }

            almacenCerrojos.escribirObjetos(objetosIncrementados);  
        }

        return cerrojo.liberar();
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
