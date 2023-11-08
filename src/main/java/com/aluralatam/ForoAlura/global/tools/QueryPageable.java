package com.aluralatam.ForoAlura.global.tools;
/**
 * Interfaz que define métodos para obtener información
 * de paginación y ordenamiento.
 */
public interface QueryPageable{
    /**
     * Obtiene el número de página.
     * @return El número de página.
     */
    Integer getPage();
    /**
     * Obtiene cantidad elementos por página.
     * @return cantidad de elementos por página.
     */
    Integer getElementByPage();
    /**
     * Obtiene los parámetros de ordenamiento.
     * @return array de string con los parámetros de ordenamiento.
     * El primer campo es de ordenamiento, y el segundo
     * elemento es la dirección de ordenamiento
     */
    String[] sortingParams();
}