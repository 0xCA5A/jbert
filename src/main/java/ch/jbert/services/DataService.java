package ch.jbert.services;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;

/**
 * CRUD Service template
 */
@Singleton
public abstract class DataService<T> {

    public abstract T create(T entity) throws IOException;

    public abstract List<T> getAll() throws IOException;

    public abstract List<T> findAllByName(String name) throws IOException;

    public abstract T update(T original, T update) throws IOException;

    public abstract T delete(T entity) throws IOException;

}
