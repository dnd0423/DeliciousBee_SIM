package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.board.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
