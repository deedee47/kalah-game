package com.dee.kalah.repository;

import com.dee.kalah.model.Hole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoleRepository extends JpaRepository<Hole, Long> {
    List<Hole> findByGameId(long gameId);
    Hole findByHoleIdAndGameId(int holeId, long gameId);
}
