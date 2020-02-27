package com.telefonica.entityservice.functions;

public interface Function1<R, S, T> {
	T perform(R r, S s);
}

