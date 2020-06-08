package ch.jbert.services;

import java.io.IOException;
import java.util.List;

/**
 * CRUD Service
 */
public interface DataService<T> {

    T create(T entity) throws IOException;

    List<T> getAll() throws IOException;

    List<T> findAllByName(String name) throws IOException;

    T update(T original, T update) throws IOException;

    T delete(T entity) throws IOException;

}
