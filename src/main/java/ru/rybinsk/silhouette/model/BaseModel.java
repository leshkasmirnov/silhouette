/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.model;

import java.io.Serializable;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
