package CRUD;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

// These codes can be very simplified.

@Controller
@RequestMapping("/search")
public class SearchPageController {

    @Autowired
    private ProductService productService; // Sadece bu controller'da servis kullanıldı (Normalde productRepo)

    private List<Product> searchedProducts = new ArrayList<Product>();

    @PostMapping("/saveText")
    public String saveText(@ModelAttribute("searchingObject") SearchingObject searchedObject, Model model) {

        searchedProducts = new ArrayList<Product>();
        
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

        return "redirect:/search/show?text=" + searchedObject.getText();
    }
    
    @GetMapping("/show")
    public String search(Model model, String text){
        
        model.addAttribute("text", text);

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

}