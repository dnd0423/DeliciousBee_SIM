package com.example.deliciousBee.service;


import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.RestaurantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {


    private final RestaurantRepository restaurantRepository;

    public void saveRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + id));
    }

    @Transactional
    public void updateRestaurant(Restaurant updateRestaurant) {
        Restaurant findRestaurant = findRestaurant(updateRestaurant.getId());

        restaurantRepository.save(findRestaurant);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
    
    //이미지 출력을 위한 서비스 메서드
    

}
