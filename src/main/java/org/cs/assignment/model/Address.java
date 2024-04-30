package org.cs.assignment.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String streetAddress;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String postcode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Address address))
            return false;
        return Objects.equals(this.id, address.id) && Objects.equals(this.streetAddress, address.streetAddress)
                && Objects.equals(this.city, address.city) && Objects.equals(this.country, address.country)
                && Objects.equals(this.postcode, address.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.streetAddress, this.city, this.country, this.postcode);
    }

    @Override
    public String toString() {
        return "Address{" + "id=" + this.id + ", e" +
                "streetAddress='" + this.streetAddress + '\'' + ", " +
                "city='" + this.city + '\'' + ", " +
                "country='" + this.country + '\'' + ", " +
                "postcode=" + postcode + '}';
    }
}
