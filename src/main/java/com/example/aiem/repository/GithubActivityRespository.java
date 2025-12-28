package com.example.aiem.repository;

import com.example.aiem.entity.GithubActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubActivityRespository extends JpaRepository<GithubActivityEntity, Integer> {

}
