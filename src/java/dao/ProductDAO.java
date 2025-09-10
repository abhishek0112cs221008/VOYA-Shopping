package dao;

import model.Product;
import java.sql.*;
import java.util.*;

public class ProductDAO {
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (id, name, category, price, quantity, image_url, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	int newId = 1;
        	ResultSet rs = ps.executeQuery("SELECT MAX(id) FROM products");
        	if(rs.next()) {
        		newId = rs.getInt(1) + 1;
        	}
        	ps.setInt(1, newId);
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategory());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getQuantity());
            ps.setString(6, product.getImageUrl());
            ps.setString(7, product.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setImageUrl(rs.getString("image_url"));
                p.setDescription(rs.getString("description"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
 // UPDATE PRODUCT
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, category=?, price=?, quantity=?, image_url=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantity());
            ps.setString(5, product.getImageUrl());
            ps.setString(6, product.getDescription());
            ps.setInt(7, product.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE PRODUCT
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // GET PRODUCT BY ID (for edit)
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setImageUrl(rs.getString("image_url"));
                p.setDescription(rs.getString("description"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void decreaseProductQuantity(int productId, int orderedQty) {
        String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderedQty);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Searches for products by name, category, or description.
     * Uses a case-insensitive search with the LIKE operator.
     *
     * @param query The search term to look for.
     * @return A list of products that match the search query.
     */
    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        // Use a SQL wildcard for a partial match
        String searchQuery = "%" + query.toLowerCase() + "%";
        
        // The SQL query checks for matches in three columns
        String sql = "SELECT * FROM products WHERE " +
                     "LOWER(product_name) LIKE ? OR " +
                     "LOWER(category) LIKE ? OR " +
                     "LOWER(description) LIKE ?";
        
        try (Connection con = DBConnection.getConnection();PreparedStatement pst = con.prepareStatement(sql)) {
            // Set the search query for each placeholder
            pst.setString(1, searchQuery);
            pst.setString(2, searchQuery);
            pst.setString(3, searchQuery);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("product_name"));
                    product.setCategory(rs.getString("category"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setImageUrl(rs.getString("image_url"));
                    product.setDescription(rs.getString("description"));
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching for products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

}