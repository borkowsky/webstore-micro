package net.rewerk.webstore.products.service.entity;

import net.rewerk.webstore.dto.response.stats.StatsResponseDto;
import net.rewerk.webstore.entity.Stats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StatsService {
    Page<StatsResponseDto> findAll(Pageable pageable, Specification<Stats> specification);
}
