package com.beyt.anouncy.region.service;

import com.beyt.anouncy.common.entity.neo4j.Region;
import com.beyt.anouncy.common.repository.RegionRepository;
import com.beyt.anouncy.region.dto.RegionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public RegionDTO findAllByParentId(String parentRegionId) {
        List<Region> regionList = regionRepository.findAllByParentRegionIdIsIn(List.of(parentRegionId));
        return new RegionDTO(regionList);
    }
}
