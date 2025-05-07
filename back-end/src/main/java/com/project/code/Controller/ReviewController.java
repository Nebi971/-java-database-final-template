package com.project.code.Controller;

import com.project.code.Entity.Review;
import com.project.code.Entity.Customer;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // 3. Define the getReviews Method
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Review review : reviews) {
            Optional<Customer> customerOpt = customerRepository.findById(review.getCustomerId());
            String customerName = customerOpt.map(Customer::getName).orElse("Unknown");

            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("comment", review.getComment());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("customerName", customerName);

            responseList.add(reviewMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", responseList);
        return response;
    }
}
