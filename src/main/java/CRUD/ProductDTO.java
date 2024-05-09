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

}
