package com.example.deliciousBee.model.community;

import java.time.LocalDateTime;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CommunityWriteForm {
	@NotBlank(message = "제목을 입력해 주세요!")
	private String title;
	
	@NotBlank(message = "내용을 입력해 주세요!")
	private String content;
	
	public static CommunityBoard toCommunityBoard(CommunityWriteForm communityWriteForm) {
		CommunityBoard communityBoard = new CommunityBoard();
		
		communityBoard.setTitle(communityWriteForm.getTitle());
		communityBoard.setContent(communityWriteForm.getContent());
		communityBoard.setHit(0L);
		communityBoard.setCreatedTime(LocalDateTime.now());
		
		return communityBoard;
	}
}
