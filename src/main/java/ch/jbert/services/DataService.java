package ch.jbert.services;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;

/**
 * CRUD Service template
 */
@Singleton
public abstract class DataService<T> {

    public abstract T create(T dto) throws IOException;

    public abstract List<T> getAll() throws IOException;

    public abstract List<T> findAllByName(String name) throws IOException;

    public abstract T update(T originalDto, T updateDto) throws IOException;

    public abstract T delete(T dto) throws IOException;

}
