package com.example.deliciousBee.model.member;

import java.time.LocalDate;
import java.util.List;

import com.example.deliciousBee.model.review.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity //JPA가 관리
@Getter @Setter @ToString
public class BeeMember {
	
	@Id  
	@Column(length = 20)
	private String member_id;    
	
	@Column(length = 20, nullable = false) 
	private String password;    
	
	@Column(length = 50, nullable = false)
	private String name;         
	
	@Column(length = 10)
	@Enumerated(EnumType.STRING)    
	private GenderType gender;       
	
	@Column(name = "birth")
	private LocalDate birth;       
	
	@Column(length = 100)
	private String email;      
	
	@OneToMany(mappedBy = "beeMember")
	private List<Review> review;
	
}
