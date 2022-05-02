package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nullable;

/**
 * A Livre.
 */
@Document(collection = "livre")
public class Livre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("author")
    private String author;

    @Field("is_borrowed")
    private Boolean isBorrowed;

    @DBRef
    @Field("category")
    private Category category;

    @DBRef
    @JsonIgnoreProperties(value = "livre")
    @Field("emprunt")
    @Nullable
    private Emprunt emprunt;

    public Boolean getBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(Boolean borrowed) {
        isBorrowed = borrowed;
    }

    public Emprunt getEmprunt() {
        return emprunt;
    }

    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
    }

// jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Livre id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Livre name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public Livre author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getIsBorrowed() {
        return this.isBorrowed;
    }

    public Livre isBorrowed(Boolean isBorrowed) {
        this.setIsBorrowed(isBorrowed);
        return this;
    }

    public void setIsBorrowed(Boolean isBorrowed) {
        this.isBorrowed = isBorrowed;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Livre category(Category category) {
        this.setCategory(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livre)) {
            return false;
        }
        return id != null && id.equals(((Livre) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Livre{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", author='" + getAuthor() + "'" +
            ", isBorrowed='" + getIsBorrowed() + "'" +
            "}";
    }
}
