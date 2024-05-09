package CRUD;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allproducts")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String name;

    @Column
    private String brand;

    @Column
    private String category;

    @Column
    private String color;

    @Column
    private int ram;

    @Column
    private int storage;

    @Column
    private int price;

    @Column(columnDefinition = "TEXT")
    private String imageFile;

    public String getCompressed() {
        return brand + " " + name + color + ram + "GB RAM" + storage + "GB storage" + price + "TL";
    }

}
