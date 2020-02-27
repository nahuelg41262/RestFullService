package com.telefonica.entityservice.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Functions {

	/*
	 * obj: contiene una lista de elementos
	 * listObj: contiene objetos del mismo tipo que obj
	 * funcBusquedaList: busca la lista de elementos que se encuentra en obj
	 * 
	 * return: une las listas de elementos de obj y listObj (elementos sin repetir)
	 */
	public static <T, U> List<U> unirListas(Optional<T> obj, List<Optional<T>> listObj, Function2<T, List<U>> funcBusquedaList) {
		List<U> listResul = new ArrayList<U>();
		
		listObj.forEach(item -> { 
			listResul.addAll(funcBusquedaList.perform(item.get()));
		});
		
		return listResul.stream().distinct().collect(Collectors.toList());
	}

}
