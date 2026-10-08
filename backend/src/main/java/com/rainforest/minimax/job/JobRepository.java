package com.rainforest.minimax.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<GenerationJob, Long> {
    List<GenerationJob> findAllByOrderByCreatedAtDesc();
}
