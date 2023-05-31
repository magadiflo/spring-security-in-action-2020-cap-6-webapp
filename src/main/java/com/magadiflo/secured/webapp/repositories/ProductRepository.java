package com.magadiflo.secured.webapp.repositories;

import com.magadiflo.secured.webapp.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
