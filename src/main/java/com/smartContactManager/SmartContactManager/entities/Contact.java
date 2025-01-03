package com.smartContactManager.SmartContactManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CONTACT")
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
   
    @NotBlank(message = "Name field is required")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name;

   @NotBlank(message = "Second Name field is required")
    @Size(min = 2, max = 6, message = "Second Name should be between 2 and 6 characters")
    private String secondName;

   
    private String work;
      
    @NotBlank(message = "Email field is required")
    @Pattern(regexp = "^(?=.{1,64}@.{1,255}$)(?=.{6,})([a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+)@([a-zA-Z0-9.-]+)\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address")
    private String email;

    private String phone;

    
    private String image;

    
    @Column(length = 5000)
    private String description;
    
    @ManyToOne
    @JsonIgnore
    private User user;

    public int getcId() {
        return cId;
    }
    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setcId(int cId) {
        this.cId = cId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSecondName() {
        return secondName;
    }
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    public String getWork() {
        return work;
    }
    public void setWork(String work) {
        this.work = work;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
     
    @Override
    public boolean equals(Object obj)
    {
        return this.cId == ((Contact)obj).getcId();
    }

    // @Override
    // public String toString() {
    //     return "Contact [cId=" + cId + ", name=" + name + ", secondName=" + secondName + ", work=" + work + ", email="
    //             + email + ", phone=" + phone + ", image=" + image + ", description=" + description + ", user=" + user
    //             + "]";
    // }
   




}
