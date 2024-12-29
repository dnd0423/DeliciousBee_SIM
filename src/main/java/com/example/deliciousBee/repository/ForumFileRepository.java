package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.community.ForumAttachedFile;
import com.example.deliciousBee.model.community.CommunityBoard;

public interface ForumFileRepository extends JpaRepository<ForumAttachedFile, Long>{
	ForumAttachedFile findByCommunityBoard(CommunityBoard communityBoard);
}
