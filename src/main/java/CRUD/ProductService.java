package CRUD;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;
 
    public List<Product> listAll(){
        return productRepo.findAll();
    }

}
