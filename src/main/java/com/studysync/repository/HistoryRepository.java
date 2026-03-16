package com.studysync.repository;

import com.studysync.model.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    
    List<History> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<History> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<History> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
    
    List<History> findByUserIdAndActionOrderByCreatedAtDesc(Long userId, String action);
}
