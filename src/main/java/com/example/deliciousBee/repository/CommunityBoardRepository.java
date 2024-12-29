package com.example.deliciousBee.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.community.CommunityBoard;

public interface CommunityBoardRepository extends JpaRepository<CommunityBoard, Long> {
	
	Optional<CommunityBoard> findByTitle(String title);
	
	//검색결과
	Page<CommunityBoard> findByTitleContaining(String searchText, Pageable pageable);
}
