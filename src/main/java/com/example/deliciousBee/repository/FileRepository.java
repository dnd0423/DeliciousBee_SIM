package com.example.deliciousBee.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.review.AttachedFile;
import com.example.deliciousBee.model.review.Review;

public interface FileRepository extends JpaRepository<AttachedFile, Long>{
	
	// 리뷰아이디를 통해서 파일가져오기
	List<AttachedFile> findByReviewId(Long id);
	void deleteByReviewId(Long reviewId);
	AttachedFile findByReview(Review review);
	

}
