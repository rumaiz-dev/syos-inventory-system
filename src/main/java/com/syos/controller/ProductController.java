package com.syos.controller;

import com.syos.entity.Product;
import com.syos.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.findAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get product: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> productData) {
        try {
            String code = (String) productData.get("productCode");
            String name = (String) productData.get("name");
            Double price = productData.get("price") != null ? Double.parseDouble(productData.get("price").toString()) : null;
            String description = (String) productData.get("description");
            String category = (String) productData.get("category");

            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product code is required"));
            }
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product name is required"));
            }
            if (price == null || price <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Valid price is required"));
            }

            // productService.addProduct removed - using save instead
            
            Product product = new Product(code, name, description, price, category);
            product = productService.save(product);
            
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create product: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> productData) {
        try {
            return productService.findById(id)
                .map(existingProduct -> {
                    if (productData.containsKey("name")) {
                        String newName = (String) productData.get("name");
                        if (newName != null && !newName.trim().isEmpty()) {
                            existingProduct.setName(newName);
                        }
                    }
                    if (productData.containsKey("price")) {
                        try {
                            Double newPrice = Double.parseDouble(productData.get("price").toString());
                            if (newPrice > 0) {
                                existingProduct.setPrice(newPrice);
                            }
                        } catch (NumberFormatException e) {
                            return ResponseEntity.badRequest().body(Map.of("error", "Invalid price format"));
                        }
                    }
                    if (productData.containsKey("description")) {
                        existingProduct.setDescription((String) productData.get("description"));
                    }
                    if (productData.containsKey("category")) {
                        existingProduct.setCategory((String) productData.get("category"));
                    }
                    
                    Product updated = productService.save(existingProduct);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update product: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            if (productService.findById(id).isPresent()) {
                productService.delete(id);
                return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete product: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam(required = false, defaultValue = "") String q) {
        try {
            List<Product> products = productService.findAll();
            String query = q.trim().toLowerCase();
            
            List<Product> filteredProducts;
            if (query.isEmpty()) {
                filteredProducts = products;
            } else {
                filteredProducts = products.stream()
                    .filter(p -> p.getProductCode().toLowerCase().contains(query) ||
                               p.getName().toLowerCase().contains(query))
                    .toList();
            }
            
            List<Map<String, Object>> searchResults = filteredProducts.stream()
                .map(p -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", p.getProductCode());
                    result.put("name", p.getName());
                    result.put("price", p.getPrice());
                    return result;
                })
                .toList();
            
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to search products: " + e.getMessage()));
        }
    }
}