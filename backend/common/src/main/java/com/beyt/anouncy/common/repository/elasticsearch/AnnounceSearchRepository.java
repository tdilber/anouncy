package com.beyt.anouncy.common.repository.elasticsearch;

import com.beyt.anouncy.common.entity.elasticsearch.AnnounceSearchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnounceSearchRepository extends ElasticsearchRepository<AnnounceSearchItem, String> {
    Page<AnnounceSearchItem> findAllByBodyContaining(String query, Pageable pageable);
}
