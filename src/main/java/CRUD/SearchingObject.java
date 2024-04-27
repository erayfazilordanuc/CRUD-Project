package CRUD;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data // constructors and getter setters will be created by this lombock annotation
public class SearchingObject {
   
    private String text;

    private String category;

}

