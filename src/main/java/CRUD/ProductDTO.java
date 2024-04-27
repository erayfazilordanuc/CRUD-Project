package CRUD;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProductDTO {

    @NotEmpty(message = "The name is required")
    private String name;

    @NotEmpty(message = "The brand is required")
    private String brand;

    @NotEmpty(message = "The category is required")
    private String category;

    @NotEmpty(message = "The color is required")
    private String color;

    @Min(0) // int değer null olmayacağı için en başta değeri 0 yap
    private int ram;

    @Min(0)
    private int storage;

    @Min(0)
    @Min(value = 1, message = "Do you want to sell this product for free?")
    private int price;

    private MultipartFile imageFile;

    public MultipartFile getImageFile(){
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile){
        this.imageFile = imageFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
