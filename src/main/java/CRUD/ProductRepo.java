package CRUD;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepo extends JpaRepository<Product, Integer>{}
