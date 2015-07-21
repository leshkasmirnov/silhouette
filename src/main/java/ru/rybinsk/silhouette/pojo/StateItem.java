/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.pojo;

/**
 * Класс для комбобокса.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class StateItem {

    private Integer id;
    private String value;

    public StateItem(int id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StateItem other = (StateItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
