package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.enums.LoanerType;
import org.mapstruct.EnumMapping;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Loaner.
 */
@Document(collection = "loaner")
public class Loaner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("email")
    private String email;

    @Field("loaner-type")
    private LoanerType loanerType;

    @Field("id_number")
    private String idNumber;

    @DBRef
    @JsonIgnoreProperties(value = "loaner")
    @Field("emprunts")
    private List<Emprunt> emprunts;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public LoanerType getLoanerType() {
        return loanerType;
    }

    public void setLoanerType(LoanerType loanerType) {
        this.loanerType = loanerType;
    }

    public List<Emprunt> getEmprunts() {
        return emprunts;
    }

    public void setEmprunts(List<Emprunt> emprunts) {
        this.emprunts = emprunts;
    }

    public String getId() {
        return this.id;
    }

    public Loaner id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Loaner firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Loaner lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Loaner email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public Loaner idNumber(String idNumber) {
        this.setIdNumber(idNumber);
        return this;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Loaner)) {
            return false;
        }
        return id != null && id.equals(((Loaner) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Loaner{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", idNumber='" + getIdNumber() + "'" +
            "}";
    }
}
