package ReproductorMusical;

import java.util.ArrayList;

/**
 *
 * @author marye
 */
public class ListaCanciones {

    private final ArrayList<Cancion> canciones;

    public ListaCanciones() {
        this.canciones = new ArrayList<>();
    }

    public Cancion getCancion(int index) {
        if (index < 0 || index >= canciones.size()) {
            return null;
        }
        return canciones.get(index);
    }

    public void agregarListaCanciones(Cancion cancion) {
        canciones.add(cancion);
    }

    public int tamanio() {
        return canciones.size();
    }

    public Cancion getPrimeraCancion() {
        if (canciones.isEmpty()) {
            return null;
        }
        return canciones.get(0);
    }

}
