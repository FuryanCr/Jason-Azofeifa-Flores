package componentes;

/** Algo que puede pintarse en rojo cuando su contenido no es válido. */
public interface Validable {

    void marcarError(boolean error);
}
