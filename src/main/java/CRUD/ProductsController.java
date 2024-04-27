package CRUD;

import java.io.File;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsController {
    
    @Autowired
    private ProductRepo productRepo;

    @GetMapping({"", "/"})
    public String showProductListSortByPrice(Model model){
        SearchingObject searchingObject = new SearchingObject();
        model.addAttribute("searchingObject", searchingObject);
        List<Product> productsList = productRepo.findAll(Sort.by(Sort.Direction.DESC, "price"));
        model.addAttribute("products", productsList);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model){
        ProductDTO productDto = new ProductDTO();
        model.addAttribute("productDto", productDto);
        return "products/createProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDTO productDto, BindingResult result, Model model){
        
        Product product = new Product();

        product.setColor(productDto.getColor());
        product.setStorage(productDto.getStorage());
        product.setRam(productDto.getRam());
        product.setCategory(productDto.getCategory());
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());

        if(productDto.getImageFile().isEmpty()){
            result.addError(new FieldError("productDto", "imageFile", "Please upload a image."));
        }

        if (result.hasErrors()) {
            model.addAttribute("productDto", productDto);
            return "products/createProduct";
        }

        MultipartFile image = productDto.getImageFile();
        String storageFileName = image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        product.setImageFile(storageFileName);

        productRepo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id){

        try {
            
            Product product = productRepo.findById(id).get();
            model.addAttribute("product", product);

            ProductDTO productDto = new ProductDTO();

            productDto.setColor(product.getColor());
            productDto.setStorage(product.getStorage());
            productDto.setRam(product.getRam());
            productDto.setCategory(product.getCategory());
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setPrice(product.getPrice());

            model.addAttribute("productDto", productDto);


        } catch (Exception e) {
            System.err.println(e.getStackTrace());                          //     "/edit" adresinin get ve post anotasyonlarında productDTO veri taşımayı sağlıyor yani önce veriler productDTO'ya eşitlenip productDTO üzerinde değiştirilip sonra asıl veriye tekrar tanımlanarak edit işltemi gerçekleştiriliyor
            return "redirect:/products";
        }

        return "products/editProduct";

    }

    @PostMapping("/edit")
    public String updateProduct(@Valid @ModelAttribute("productDto") ProductDTO productDto, BindingResult result, Model model, @RequestParam("id") int id){
        
        Product product = productRepo.findById(id).get();
        model.addAttribute("product", product);

        product.setColor(productDto.getColor());
        product.setStorage(productDto.getStorage());
        product.setRam(productDto.getRam());
        product.setCategory(productDto.getCategory());
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());

        if (result.hasErrors()) {
            model.addAttribute("productDto", productDto);
            return "products/editProduct";
        }

        if(!productDto.getImageFile().isEmpty()){
            String uploadDir = "public/images/";

            Path oldImagePath = Paths.get(uploadDir + product.getImageFile());
            
            try {
                if(fileCounter(uploadDir, product.getImageFile())>1){
                    Files.delete(oldImagePath);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

            MultipartFile image = productDto.getImageFile();
            System.out.println(image);
            String storageFileName = image.getOriginalFilename();

            try {
                Path uploadPath = Paths.get(uploadDir);

                if(!Files.exists(uploadPath)){
                    Files.createDirectories(uploadPath);
                }

                try(InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

            product.setImageFile(storageFileName);
        }

        productRepo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String showDeletePage(@RequestParam int id){

        String uploadDir = "public/images/";

        Product product = productRepo.getReferenceById(id);

        Path oldImagePath = Paths.get(uploadDir + product.getImageFile());

        if(!product.getImageFile().isEmpty()){
            
            try {
                if(fileCounter(uploadDir, product.getImageFile())>1){
                    Files.delete(oldImagePath);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        
        }

        productRepo.deleteById(id);

        return "redirect:/products";
    }

    public int fileCounter(String uploadDir, String imageName){
        int counter = 0;

        File folder = new File(uploadDir);

        // Klasördeki dosyaları listelemek için bir dizi oluşturun
        File[] files = folder.listFiles();

        for(File f : files){
            if(f.toString().equals("public\\images\\" + imageName)){
                counter++;
            }
        }

        return counter;
    }

}