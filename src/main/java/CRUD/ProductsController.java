package CRUD;

import java.io.File;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
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

    @Autowired
    private ProductService productService;

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

    @GetMapping("/edit{id}")
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

    @PostMapping("/edit{id}")
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

    @GetMapping("/delete{id}")
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
    @GetMapping("/show")
    public String showProduct(Model model, @RequestParam int id){

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

        return "products/showProduct";

    }

    @PostMapping("/search")
    public String saveText(@ModelAttribute("searchingObject") SearchingObject searchedObject, Model model) {

        List<Product> searchedProducts = new ArrayList<Product>();
        
        List<Product> allProducts = productService.listAll();

        String[] compressedProducts = new String[allProducts.size()];


        for(int i=0; i<allProducts.size(); i++){
            compressedProducts[i] = allProducts.get(i).getCompressed();
        }

        if(searchedObject.getCategory().equals("All")){

            for(int i=0; i<compressedProducts.length; i++){
                if(isIn(compressedProducts[i], searchedObject.getText())){
                    searchedProducts.add(allProducts.get(i));
                }
            }

        }else{

            for(int i=0; i<compressedProducts.length; i++){
                if(isIn(compressedProducts[i], searchedObject.getText()) && allProducts.get(i).getCategory().equals(searchedObject.getCategory())){
                    searchedProducts.add(allProducts.get(i));
                }
            }

        }

        model.addAttribute("products", searchedProducts); // global değişkenden alınıyor (şimdilik)

        SearchingObject searchingObject = new SearchingObject();
        model.addAttribute("searchingObject", searchingObject);

        return "products/index";
    }

    public boolean isIn(String text, String input){

        //            Alternative
        // if(input.equals("")){
        // return true;
        // }

        if(input.length()==0 || input.isBlank()){
            return true;
        }
        
        // boolean isBlank = true;
        // for(int i=0; i<input.length(); i++){
        //     if((int)input.charAt(i)<33 || (int)input.charAt(i)==127){ // obj can be different
        //         isBlank = false; // it means the input is not empty, filled
        //     }
        // }if(isBlank){
        //     return true;
        // }

        String[] inputLetters = new String[input.length()];
    
        for(int i=0; i<input.length(); i++){
            inputLetters[i] = Character.toString(input.charAt(i));
        }
    
        int findedSame = 0;
    
        for(int i=0; i<text.length()-input.length()+1; i++){
    
            int k = i;
    
            Boolean isIt = true;
    
            if(Character.toString(text.charAt(i)).equalsIgnoreCase(inputLetters[0])){
                for(int j=1; j<input.length(); j++){
                    if(!Character.toString(text.charAt(++i)).equalsIgnoreCase(inputLetters[j])){
    
                        isIt = false;
                        break;
                    }
                }
                if(isIt){
                    findedSame++;
                }
            }
            i = k;
        }
        if(findedSame>0){
            return true;
        }else{
            return false;
        }

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